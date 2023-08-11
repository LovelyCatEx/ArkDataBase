package com.lovelycatv.ark.compiler.processor.relational.children;

import com.lovelycatv.ark.ArkVars;
import com.lovelycatv.ark.common.annotations.Dao;
import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.ProcessorException;
import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.relational.sql.IBaseSQLStatement;
import com.lovelycatv.ark.compiler.pre.relational.sql.StandardSQLStatement;
import com.lovelycatv.ark.compiler.pre.relational.verify.dao.DAOVerification;
import com.lovelycatv.ark.compiler.pre.relational.verify.entity.EntityVerification;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.MySQLSupportedParameterManager;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SQLiteSupportedParameterManager;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.pre.relational.verify.typeconverter.TypeConverterVerification;
import com.lovelycatv.ark.compiler.processor.ArkDatabaseProcessor;
import com.lovelycatv.ark.compiler.processor.relational.children.base.AbstractDatabaseProcessor;
import com.lovelycatv.ark.compiler.utils.APTools;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class DatabaseProcessor extends AbstractDatabaseProcessor {
    public DatabaseProcessor(ArkDatabaseProcessor processor) {
        super(processor);
    }

    @Override
    public void determineSupportedParametersManager() throws ProcessorUnexpectedError {
        DataBaseType dataBaseType = super.getProcessableDatabase().getDataBaseType();
        if (dataBaseType == DataBaseType.MYSQL) {
            super.setSupportedParameterManager(new MySQLSupportedParameterManager());
        } else if (dataBaseType == DataBaseType.SQLITE) {
            super.setSupportedParameterManager(new SQLiteSupportedParameterManager());
        } else {
            throw new ProcessorUnexpectedError("If you see this output error that means I've forgot to add statement in this part! class: " + this.getClass().getName());
        }
    }

    @Override
    protected List<ProcessableDAO> analysisDAO(Element annotatedElement) throws ProcessorUnexpectedError {
        Set<? extends Element> annotatedWithDAO = getProcessor().getRoundEnvironment().getElementsAnnotatedWith(Dao.class);
        List<ProcessableDAO> result = new ArrayList<>();
        List<Element> abstractMethods = APTools.getAbstractMethods(annotatedElement);
        for (Element method : abstractMethods) {
            // Use roundEnvironment get @Dao class
            for (Element classElement : annotatedWithDAO) {
                if (APTools.isTheSameTypeMirror(method.asType(), classElement.asType())) {
                    Dao dao = classElement.getAnnotation(Dao.class);
                    if (dao == null) {
                        throw new ProcessorUnexpectedError(String.format("Cannot find @Dao in %s", APTools.getClassNameFromTypeMirror(classElement.asType())));
                    }
                    result.add(ProcessableDAO.builder(method, classElement));
                }
            }
        }
        return result;
    }

    @Override
    protected List<ProcessableEntity> analysisEntities(Element annotatedElement) throws ProcessorUnexpectedError {
        List<ProcessableEntity> result = new ArrayList<>();
        List<DeclaredType> declaredTypesOfEntity = APTools.getClassArrayFromAnnotation(annotatedElement, Database.class, Database.FILED_ENTITIES, true);
        if (declaredTypesOfEntity == null) {
            throw new ProcessorUnexpectedError(String.format("Cannot read entities in database %s", annotatedElement.asType().toString()));
        }
        for (DeclaredType entityType : declaredTypesOfEntity) {
            result.add(ProcessableEntity.builder(entityType));
        }
        return result;
    }

    @Override
    protected List<ProcessableTypeConverter> analysisTypeConverters(Element annotatedElement, SupportedParameterManager supportedParameterManager)
            throws ProcessorUnexpectedError, ProcessorException {
        List<ProcessableTypeConverter> result = new ArrayList<>();
        List<DeclaredType> declaredTypesOfTypeConverter = APTools.getClassArrayFromAnnotation(annotatedElement, Database.class, Database.FILED_TYPE_CONVERTERS, true);
        if (declaredTypesOfTypeConverter == null) {
            throw new ProcessorUnexpectedError(String.format("Cannot read typeConverters in database %s", annotatedElement.asType().toString()));
        }
        for (DeclaredType typeConverterType : declaredTypesOfTypeConverter) {
            result.add(ProcessableTypeConverter.builder(typeConverterType, supportedParameterManager));
        }
        return result;
    }

    @Override
    protected void verifyProcessableObjects() throws ProcessorUnexpectedError, ProcessorException {
        // Verify typeConverters
        new TypeConverterVerification(super.getProcessableDatabase().getDataBaseType(), super.getSupportedParameterManager(),
                super.getProcessableDatabase().getTypeConverterController().getTypeConverterList()).verify();

        // Verify entities
        for (ProcessableEntity entity : super.getProcessableDatabase().getEntityController().getEntityList()) {
            new EntityVerification(super.getProcessableDatabase().getDataBaseType(), super.getSupportedParameterManager(), entity,
                    super.getProcessableDatabase().getTypeConverterController()).verify();
        }

        // Verify dao
        for (ProcessableDAO processableDAO : super.getProcessableDatabase().getDaoController().getDAOList()) {
            new DAOVerification(super.getProcessableDatabase().getDataBaseType(), super.getSupportedParameterManager(), processableDAO).verify();
        }

    }

    @Override
    protected void startTypeConverterProcessor() throws ProcessorUnexpectedError {
        TypeSpec.Builder start = super.getTypeConverterProcessor().start();
        try {
            JavaFile.builder(ArkVars.getPackageName(super.getProcessableDatabase().getClassElement().getSimpleName().toString()),
                    start.build()).build().writeTo(super.getProcessor().getFiler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void startDAOProcessor() throws ProcessorError, ProcessorUnexpectedError, ProcessorException {
        List<TypeSpec.Builder> daoImpls = super.getDaoProcessor().start();

        for (TypeSpec.Builder daoImpl : daoImpls) {
            try {
                JavaFile.builder(ArkVars.getDAOPackageName(super.getProcessableDatabase().getClassElement().getSimpleName().toString()),
                        daoImpl.build()).build().writeTo(super.getProcessor().getFiler());
            } catch (IOException e) {
                throw new ProcessorError("Cannot write DAO impls to your project");
            }
        }

    }

    @Override
    protected List<CodeBlock> getCodeInInitDatabase() throws ProcessorError {
        List<CodeBlock> result = new ArrayList<>();
        for (ProcessableEntity processableEntity : super.getProcessableDatabase().getEntityController().getEntityList()) {
            IBaseSQLStatement iBaseSQLStatement = processableEntity.createBaseSQLStatement(super.getSupportedParameterManager().getDataBaseType());
            List<StandardSQLStatement> createTableStatement = iBaseSQLStatement.getCreateTableStatement(processableEntity,
                    super.getProcessableDatabase().getTypeConverterController().getTypeConverterList(), super.getSupportedParameterManager());
            for (StandardSQLStatement statement : createTableStatement) {
                result.add(CodeBlock.builder().add("super.getDatabaseManager().execute($S, null)", statement.getSql()).build());
            }
        }
        return result;
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
                    super.getProcessor().info(entity.getDeclaredEntityType().asElement(), column.getGetter().getMethodElement().getSimpleName().toString());
                    super.getProcessor().info(entity.getDeclaredEntityType().asElement(), column.getSetter().getMethodElement().getSimpleName().toString());
                }
            }

            for (ProcessableDAO dao : daoList) {
                super.getProcessor().info(dao.getDAOClassElement(), "Analysing DAO: " + dao.getDAOClassElement().asType().toString());
            }

        }
    }
}
