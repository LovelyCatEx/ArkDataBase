package com.lovelycatv.ark.compiler.processor.relational.children;

import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Query;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDatabase;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.processor.relational.children.base.AbstractDAOProcessor;
import com.lovelycatv.ark.compiler.processor.relational.children.base.AbstractDatabaseProcessor;
import com.lovelycatv.ark.compiler.processor.relational.objects.EntityAdapterInfo;
import com.lovelycatv.ark.compiler.utils.APTools;
import com.lovelycatv.ark.compiler.utils.StringX;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;
import com.squareup.javapoet.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DAOProcessor extends AbstractDAOProcessor {
    public DAOProcessor(AbstractDatabaseProcessor databaseProcessor) {
        super(databaseProcessor);
    }

    @Override
    public List<TypeSpec.Builder> start() throws ProcessorError {
        this.determineSupportedParametersManager();

        List<TypeSpec.Builder> result = new ArrayList<>();
        ProcessableDatabase database = getDatabaseProcessor().getProcessableDatabase();
        for (ProcessableDAO dao : database.getDaoController().getDAOList()) {
            result.add(buildDAO(dao));
        }

        debugging();

        return result;
    }

    // Define fields name constants
    public static final String FIELD_DAO_DATABASE = "__db";
    @Override
    public TypeSpec.Builder buildDAO(ProcessableDAO processableDAO) throws ProcessorError {
        // Verify DAO
        verifyDAO(processableDAO);

        // Scan adapters
        List<EntityAdapterInfo> entityAdapterInfos = scanAllUsedAdapters(processableDAO);

        // Start build
        String interfaceFullName = APTools.getClassNameFromTypeMirror(processableDAO.getDAOClassElement().asType());
        TypeSpec.Builder daoImpl = TypeSpec.classBuilder(processableDAO.getFileName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get(StringX.getClassPathInFullName(interfaceFullName), StringX.getClassNameInFullName(interfaceFullName)));

        // Adapters and Constructor
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);

        // Add ArkRelationalDatabase Field
        // ArkRelationalDatabase<? extends RelationalDatabase>
        Class<? extends RelationalDatabase> databaseManagerClass = super.getDatabaseProcessor().getDatabaseManagerClass();
        ParameterizedTypeName relationalDatabaseTypeName = ParameterizedTypeName.get(ClassName.get(ArkRelationalDatabase.class), ClassName.get(databaseManagerClass));
        daoImpl.addField(relationalDatabaseTypeName, FIELD_DAO_DATABASE, Modifier.PRIVATE, Modifier.FINAL);
        final String PARAM_CONSTRUCTOR_DATABASE = "_db";
        constructor.addParameter(relationalDatabaseTypeName, PARAM_CONSTRUCTOR_DATABASE);
        constructor.addStatement("this.$L = $L", FIELD_DAO_DATABASE, PARAM_CONSTRUCTOR_DATABASE);

        // Add Adapters
        for (EntityAdapterInfo adapterInfo : entityAdapterInfos) {
            adapterInfo.buildFiledList();
            adapterInfo.buildAdapterAnonymousTypes(getDatabaseProcessor().getProcessableDatabase().getDataBaseType());
            for (Map.Entry<Class<? extends Annotation>, FieldSpec> entry : adapterInfo.annotationWithFields.entrySet()) {
                daoImpl.addField(entry.getValue());
            }
            // Init adapters in constructor
            for (Map.Entry<Class<? extends Annotation>, TypeSpec> entry : adapterInfo.annotationWithAnonymousTypes.entrySet()) {
                constructor.addStatement(String.format("this.%s = $L", adapterInfo.getFieldName(entry.getKey())), entry.getValue());
            }
        }

        daoImpl.addMethod(constructor.build());

        // Build typeConverters
        for (ProcessableTypeConverter typeConverter : super.getDatabaseProcessor().getProcessableDatabase().getTypeConverterController().getTypeConverterList()) {
            for (ProcessableTypeConverter.Converter converter : typeConverter.getTypeConverterList()) {
                MethodSpec methodSpec = MethodSpec.methodBuilder(converter.getMethodNameInDAO())
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(ClassName.get(converter.getFrom()), "_from")
                        .returns(ClassName.get(converter.getTo()))
                        .addStatement("return $T.$L($L)", typeConverter.getTypeConverterType(), converter.getElement().getSimpleName(), "_from")
                        .build();

                daoImpl.addMethod(methodSpec);
            }

        }


        return daoImpl;
    }

    @Override
    public void verifyDAO(ProcessableDAO processableDAO) throws ProcessorError {
        for (Element interfaceElement : processableDAO.getDAOClassElement().getEnclosedElements()) {
            boolean isAnnotatedWithCommon = APTools.containsAnnotation(interfaceElement, Insert.class, Update.class, Delete.class);
            if (!APTools.containsAnnotation(interfaceElement, Query.class) && !isAnnotatedWithCommon) {
                throw new ProcessorError(String.format("The method %s() in %s must have only one of annotations like @Insert @Update @Delete or @Query",
                        interfaceElement.getSimpleName(), processableDAO.getDAOClassElement().asType().toString()));
            } else if (isAnnotatedWithCommon) {
                if (!APTools.isVoid(((ExecutableElement) interfaceElement).getReturnType())) {
                    throw new ProcessorError(String.format("The method %s() annotated with @Insert @Update or @Delete in %s must return void",
                            interfaceElement.getSimpleName(), processableDAO.getDAOClassElement().asType().toString()));
                }
            }
        }
    }

    @Override
    public List<EntityAdapterInfo> scanAllUsedAdapters(ProcessableDAO processableDAO) throws ProcessorError {
        List<EntityAdapterInfo> result = new ArrayList<>();
        for (Element interfaceElement : processableDAO.getDAOClassElement().getEnclosedElements()) {
            if (!APTools.containsAnnotation(interfaceElement, Insert.class, Update.class, Delete.class)) {
                continue;
            }
            EntityAdapterInfo entityAdapterInfo = new EntityAdapterInfo(super.getSupportedParameterManager(),
                    super.getDatabaseProcessor().getProcessableDatabase().getTypeConverterController().getTypeConverterList());

            ExecutableElement i = (ExecutableElement) interfaceElement;
            List<? extends VariableElement> parameters = i.getParameters();

            ProcessableDatabase database = getDatabaseProcessor().getProcessableDatabase();

            if (parameters == null || parameters.size() != 1 ||
                    !database.getEntityController().containsEntityType(parameters.get(0).asType())) {
                throw new ProcessorError(String.format("The method %s() in %s must have only one parameter of entity",
                        interfaceElement.getSimpleName(), processableDAO.getDAOClassElement().asType().toString()));
            }

            VariableElement var = parameters.get(0);
            ProcessableEntity entity = database.getEntityController().getEntityIndistinct(var.asType());
            entityAdapterInfo.setTargetEntity(entity);

            // If exists
            boolean exists = false;
            for (EntityAdapterInfo info : result) {
                if (APTools.isTheSameTypeMirror(info.getTargetEntity().getDeclaredEntityType().asElement().asType(), entity.getDeclaredEntityType().asElement().asType())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                result.add(entityAdapterInfo);
            }
        }
        return result;
    }


    @Override
    protected void debugging() {

    }

    @Override
    public void determineSupportedParametersManager() {
        super.setSupportedParameterManager(super.getDatabaseProcessor().getSupportedParameterManager());
    }
}
