package com.lovelycatv.ark.compiler.processor.relational.children.base;

import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDatabase;
import com.lovelycatv.ark.compiler.processor.relational.objects.EntityAdapterInfo;
import com.lovelycatv.ark.compiler.utils.APTools;
import com.lovelycatv.ark.compiler.utils.StringX;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractDAOProcessor extends AbstractProcessor {
    private final AbstractDatabaseProcessor databaseProcessor;

    public AbstractDAOProcessor(AbstractDatabaseProcessor databaseProcessor) {
        this.databaseProcessor = databaseProcessor;
    }

    public List<TypeSpec.Builder> start() throws ProcessorError, ProcessorUnexpectedError {
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
    public TypeSpec.Builder buildDAO(ProcessableDAO processableDAO) throws ProcessorError, ProcessorUnexpectedError {
        // Verify DAO
        verifyDAO(processableDAO);

        // Start build
        String interfaceFullName = APTools.getClassNameFromTypeMirror(processableDAO.getDAOClassElement().asType());
        TypeSpec.Builder daoImpl = TypeSpec.classBuilder(processableDAO.getFileName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get(StringX.getClassPathInFullName(interfaceFullName), StringX.getClassNameInFullName(interfaceFullName)));

        // Adapters and Constructor
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);

        // Add ArkRelationalDatabase Field
        // ArkRelationalDatabase<? extends RelationalDatabase>
        Class<? extends RelationalDatabase> databaseManagerClass = getDatabaseProcessor().getDatabaseManagerClass();
        ParameterizedTypeName relationalDatabaseTypeName = ParameterizedTypeName.get(ClassName.get(ArkRelationalDatabase.class), ClassName.get(databaseManagerClass));
        daoImpl.addField(relationalDatabaseTypeName, FIELD_DAO_DATABASE, Modifier.PRIVATE, Modifier.FINAL);
        final String PARAM_CONSTRUCTOR_DATABASE = "_db";
        constructor.addParameter(relationalDatabaseTypeName, PARAM_CONSTRUCTOR_DATABASE);
        constructor.addStatement("this.$L = $L", FIELD_DAO_DATABASE, PARAM_CONSTRUCTOR_DATABASE);

        // Add Adapters
        List<EntityAdapterInfo> entityAdapterInfos = scanAllUsedAdapters(processableDAO);
        for (EntityAdapterInfo adapterInfo : entityAdapterInfos) {
            adapterInfo.build(getDatabaseProcessor().getProcessableDatabase().getDataBaseType());
            for (Map.Entry<Class<? extends Annotation>, FieldSpec> entry : adapterInfo.annotationWithFields.entrySet()) {
                daoImpl.addField(entry.getValue());
            }
            // Init adapters in constructor
            for (Map.Entry<Class<? extends Annotation>, TypeSpec> entry : adapterInfo.annotationWithAnonymousTypes.entrySet()) {
                constructor.addStatement(String.format("this.%s = $L", adapterInfo.getFieldName(entry.getKey())), entry.getValue());
            }
        }

        daoImpl.addMethod(constructor.build());

        // Add adapter methods
        for (MethodSpec.Builder method : buildAllAdapterMethods(processableDAO)) {
            daoImpl.addMethod(method.build());
        }

        return daoImpl;
    }

    public abstract void verifyDAO(ProcessableDAO processableDAO) throws ProcessorError;

    public abstract List<EntityAdapterInfo> scanAllUsedAdapters(ProcessableDAO processableDAO) throws ProcessorError;

    public abstract List<MethodSpec.Builder> buildAllAdapterMethods(ProcessableDAO processableDAO) throws ProcessorUnexpectedError;

    public final AbstractDatabaseProcessor getDatabaseProcessor() {
        return databaseProcessor;
    }

}
