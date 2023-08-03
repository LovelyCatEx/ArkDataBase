package com.lovelycatv.ark.compiler.processor.children;

import com.lovelycatv.ark.common.annotations.ArkDebug;
import com.lovelycatv.ark.common.annotations.Dao;
import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.PreProcessException;
import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.ProcessableDatabase;
import com.lovelycatv.ark.compiler.pre.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.verify.entity.EntityVerification;
import com.lovelycatv.ark.compiler.pre.verify.parameter.MySQLSupportedParameterManager;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SQLiteSupportedParameterManager;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.pre.verify.typeconverter.TypeConverterVerification;
import com.lovelycatv.ark.compiler.processor.ArkDatabaseProcessor;
import com.lovelycatv.ark.compiler.processor.children.base.AbstractDatabaseProcessor;
import com.lovelycatv.ark.compiler.utils.APTools;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class DatabaseProcessor extends AbstractDatabaseProcessor {
    public DatabaseProcessor(ArkDatabaseProcessor processor) {
        super(processor);
    }

    @Override
    public void analysis(Element annotatedElement) throws PreProcessUnexpectedError, PreProcessException, ProcessorError {
        Database databaseAnnotation = annotatedElement.getAnnotation(Database.class);
        if (databaseAnnotation == null) {
            throw new PreProcessUnexpectedError("Cannot find @DataBase annotation");
        }

        ArkDebug arkDebug = annotatedElement.getAnnotation(ArkDebug.class);
        if (arkDebug != null) {
            setDebugging(arkDebug.enabled());
        } else {
            setDebugging(false);
        }
        super.getDaoProcessor().setDebugging(super.isDebugging());

        super.processableDatabase = new ProcessableDatabase(databaseAnnotation.dataBaseType(), databaseAnnotation.version());

        determineSupportedParametersManager();

        // Analysis abstract dao
        List<ProcessableDAO> processableDAOList = analysisDAO(annotatedElement);
        super.getProcessableDatabase().getDaoController().getDAOList().addAll(processableDAOList);

        // Analysis typeConverters
        List<ProcessableTypeConverter> processableTypeConverterList = analysisTypeConverters(annotatedElement, super.getSupportedParameterManager());
        super.getProcessableDatabase().getTypeConverterController().getTypeConverterList().addAll(processableTypeConverterList);

        // Analysis entities
        List<ProcessableEntity> processableEntityList = analysisEntities(annotatedElement);
        super.getProcessableDatabase().getEntityController().getEntityList().addAll(processableEntityList);

        // Verify processable objects
        verifyProcessableObjects();

        // Create DAO impl files
        startDAOProcessor();

        // Debugging
        debugging();
    }

    @Override
    public void determineSupportedParametersManager() throws PreProcessUnexpectedError {
        DataBaseType dataBaseType = super.getProcessableDatabase().getDataBaseType();
        if (dataBaseType == DataBaseType.MYSQL) {
            super.setSupportedParameterManager(new MySQLSupportedParameterManager());
        } else if (dataBaseType == DataBaseType.SQLITE) {
            super.setSupportedParameterManager(new SQLiteSupportedParameterManager());
        } else {
            throw new PreProcessUnexpectedError("If you see this output error that means I've forgot to add statement in this part! class: " + this.getClass().getName());
        }
    }

    @Override
    protected List<ProcessableDAO> analysisDAO(Element annotatedElement) throws PreProcessUnexpectedError {
        Set<? extends Element> annotatedWithDAO = getProcessor().getRoundEnvironment().getElementsAnnotatedWith(Dao.class);
        List<ProcessableDAO> result = new ArrayList<>();
        List<Element> abstractMethods = APTools.getAbstractMethods(annotatedElement);
        for (Element method : abstractMethods) {
            // Use roundEnvironment get @Dao class
            for (Element classElement : annotatedWithDAO) {
                if (APTools.isTheSameTypeMirror(method.asType(), classElement.asType())) {
                    Dao dao = classElement.getAnnotation(Dao.class);
                    if (dao == null) {
                        throw new PreProcessUnexpectedError(String.format("Cannot find @Dao in %s", APTools.getClassNameFromTypeMirror(classElement.asType())));
                    }
                    result.add(ProcessableDAO.builder(method, classElement));
                }
            }
        }
        return result;
    }

    @Override
    protected List<ProcessableEntity> analysisEntities(Element annotatedElement) throws PreProcessUnexpectedError {
        List<ProcessableEntity> result = new ArrayList<>();
        List<DeclaredType> declaredTypesOfEntity = APTools.getClassArrayFromAnnotation(annotatedElement, Database.class, Database.FILED_ENTITIES, true);
        if (declaredTypesOfEntity == null) {
            throw new PreProcessUnexpectedError(String.format("Cannot read entities in database %s", annotatedElement.asType().toString()));
        }
        for (DeclaredType entityType : declaredTypesOfEntity) {
            result.add(ProcessableEntity.builder(entityType));
        }
        return result;
    }

    @Override
    protected List<ProcessableTypeConverter> analysisTypeConverters(Element annotatedElement, SupportedParameterManager supportedParameterManager)
            throws PreProcessUnexpectedError, PreProcessException {
        List<ProcessableTypeConverter> result = new ArrayList<>();
        List<DeclaredType> declaredTypesOfTypeConverter = APTools.getClassArrayFromAnnotation(annotatedElement, Database.class, Database.FILED_TYPE_CONVERTERS, true);
        if (declaredTypesOfTypeConverter == null) {
            throw new PreProcessUnexpectedError(String.format("Cannot read typeConverters in database %s", annotatedElement.asType().toString()));
        }
        for (DeclaredType typeConverterType : declaredTypesOfTypeConverter) {
            result.add(ProcessableTypeConverter.builder(typeConverterType, supportedParameterManager));
        }
        return result;
    }

    @Override
    protected void verifyProcessableObjects() throws PreProcessUnexpectedError, PreProcessException {
        // Verify typeConverters
        new TypeConverterVerification(super.getProcessableDatabase().getDataBaseType(), super.getSupportedParameterManager(),
                super.getProcessableDatabase().getTypeConverterController().getTypeConverterList()).verify();

        // Verify entities
        for (ProcessableEntity entity : super.getProcessableDatabase().getEntityController().getEntityList()) {
            new EntityVerification(super.getProcessableDatabase().getDataBaseType(), super.getSupportedParameterManager(), entity,
                    super.getProcessableDatabase().getTypeConverterController()).verify();
        }
    }

    @Override
    protected void startDAOProcessor() throws ProcessorError {
        super.getDaoProcessor().start();
    }

    @Override
    protected void debugging() {
        if (isDebugging()) {
            List<ProcessableTypeConverter> processableTypeConverterList = super.getProcessableDatabase().getTypeConverterController().getTypeConverterList();
            List<ProcessableEntity> processableEntityList = super.getProcessableDatabase().getEntityController().getEntityList();
            List<ProcessableDAO> daoList = super.getProcessableDatabase().getDaoController().getDAOList();
            // Debug
            for (ProcessableTypeConverter typeConverter : processableTypeConverterList) {
                super.getProcessor().info(typeConverter.getTypeConverterType().asElement(), "Analysing typeConverter class: " + typeConverter.getTypeConverterType().asElement().asType().toString() +
                        String.format(" | %s converter(s)", typeConverter.getTypeConverterList().size()));
                for (ProcessableTypeConverter.Converter converter : typeConverter.getTypeConverterList()) {
                    super.getProcessor().info(typeConverter.getTypeConverterType().asElement(), (converter.isConvertOut() ? "[in]" : "[out]") + converter.getFrom().toString() + " return " + converter.getTo());
                }
            }

            for (ProcessableEntity entity : processableEntityList) {
                super.getProcessor().info(entity.getDeclaredEntityType().asElement(), "Analysing entity: " + entity.getTableName() +
                        String.format(" | %s column(s)", entity.getEntityColumnList().size()));
                for (ProcessableEntity.EntityColumn column : entity.getEntityColumnList()) {
                    super.getProcessor().info(entity.getDeclaredEntityType().asElement(), "- column: " + column.getElement().getSimpleName() + " : " + column.getElement().asType().toString());
                }
            }

            for (ProcessableDAO dao : daoList) {
                super.getProcessor().info(dao.getDAOClassElement(), "Analysing DAO: " + dao.getDAOClassElement().asType().toString());
            }

        }
    }
}
