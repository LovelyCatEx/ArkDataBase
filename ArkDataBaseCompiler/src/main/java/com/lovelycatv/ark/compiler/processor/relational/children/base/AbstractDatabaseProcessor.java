package com.lovelycatv.ark.compiler.processor.relational.children.base;

import com.lovelycatv.ark.common.annotations.ArkDebug;
import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.ProcessorVars;
import com.lovelycatv.ark.compiler.exceptions.ProcessorException;
import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDatabase;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.processor.ArkDatabaseProcessor;
import com.lovelycatv.ark.compiler.processor.relational.children.DAOProcessor;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.MySQLManager;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;
import com.squareup.javapoet.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;

public abstract class AbstractDatabaseProcessor extends AbstractProcessor {
    private final ArkDatabaseProcessor processor;
    protected ProcessableDatabase processableDatabase;
    private final DAOProcessor daoProcessor;

    public AbstractDatabaseProcessor(ArkDatabaseProcessor processor) {
        this.processor = processor;
        this.daoProcessor = new DAOProcessor(this);
    }

    public Class<? extends RelationalDatabase> getDatabaseManagerClass() {
        return DataBaseType.getRelationalDatabaseByType(this.processableDatabase.getDataBaseType());
    }

    public void analysis(Element annotatedElement) throws ProcessorUnexpectedError, ProcessorException, ProcessorError {
        final Database databaseAnnotation = annotatedElement.getAnnotation(Database.class);
        if (databaseAnnotation == null) {
            throw new ProcessorUnexpectedError("Cannot find @DataBase annotation");
        }

        final ArkDebug arkDebug = annotatedElement.getAnnotation(ArkDebug.class);
        if (arkDebug != null) {
            super.setDebugging(arkDebug.enabled());
        } else {
            super.setDebugging(false);
        }

        this.getDaoProcessor().setDebugging(super.isDebugging());

        this.processableDatabase = new ProcessableDatabase(databaseAnnotation.dataBaseType(), databaseAnnotation.version());

        // Init supported parameters manager
        determineSupportedParametersManager();

        // Analysis abstract dao
        this.getProcessableDatabase().getDaoController().getDAOList().addAll(analysisDAO(annotatedElement));

        // Analysis typeConverters
        this.getProcessableDatabase().getTypeConverterController().getTypeConverterList().addAll(analysisTypeConverters(annotatedElement, super.getSupportedParameterManager()));

        // Analysis entities
        this.getProcessableDatabase().getEntityController().getEntityList().addAll(analysisEntities(annotatedElement));

        // Verify processable objects
        verifyProcessableObjects();

        // Create DAO impl files
        startDAOProcessor();

        // Debugging
        debugging();

        final TypeSpec.Builder databaseImpl = TypeSpec.classBuilder(annotatedElement.getSimpleName() + "Impl")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(annotatedElement.asType());

        final Class<? extends RelationalDatabase> relationalDatabaseClass = getDatabaseManagerClass();
        final ParameterizedTypeName relationalDatabaseType = ParameterizedTypeName.get(ClassName.get(ArkRelationalDatabase.class), ClassName.get(relationalDatabaseClass));

        // Override super.getDatabase()
        {
            final MethodSpec getDatabase = MethodSpec.methodBuilder("getDatabase")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(relationalDatabaseType)
                    .addStatement("return ($T<$T>) super.getDatabase()", ArkRelationalDatabase.class, relationalDatabaseClass)
                    .build();

            databaseImpl.addMethod(getDatabase);
        }

        // Override super.initDatabase()
        {
            final MethodSpec.Builder initDatabaseInside = MethodSpec.methodBuilder("initDataBase")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC);

            for (CodeBlock codeBlock : getCodeInInitDatabase()) {
                initDatabaseInside.addStatement(codeBlock);
            }

            final TypeSpec anonymous = TypeSpec.anonymousClassBuilder("$T." + databaseAnnotation.dataBaseType().name(), DataBaseType.class)
                    .superclass(relationalDatabaseType)
                    .addMethod(initDatabaseInside.build())
                    .build();

            final MethodSpec initDatabase = MethodSpec.methodBuilder("initDatabase")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("super.setBaseArkDatabase($L)", anonymous)
                    .build();

            databaseImpl.addMethod(initDatabase);
        }

        // DAO impls
        for (ProcessableDAO processableDAO : getProcessableDatabase().getDaoController().getDAOList()) {
            final String DAOFileName = processableDAO.getFileName();
            final String FIELD_DAO = "_" + DAOFileName;
            final ClassName daoClassName = ClassName.get(ProcessorVars.PACKAGE_NAME, DAOFileName);
            FieldSpec daoImpl = FieldSpec.builder(daoClassName, FIELD_DAO)
                    .addModifiers(Modifier.PRIVATE, Modifier.VOLATILE)
                    .build();

            databaseImpl.addField(daoImpl);

            MethodSpec daoMethod = MethodSpec.methodBuilder(processableDAO.getDAOAbstractMethodElement().getSimpleName().toString())
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(daoClassName)
                    .beginControlFlow("if(this.$L == null)", FIELD_DAO)
                    .addStatement("this.$L = new $T(this.getDatabase())", FIELD_DAO, daoClassName)
                    .endControlFlow()
                    .addStatement("return this.$L", FIELD_DAO)
                    .build();

            databaseImpl.addMethod(daoMethod);
        }


        try {
            JavaFile.builder(ProcessorVars.PACKAGE_NAME, databaseImpl.build()).build().writeTo(getProcessor().getFiler());
        } catch (IOException e) {
            throw new ProcessorError(String.format("Cannot write database impl (%s) to your project", annotatedElement.asType().toString()));
        }
    }

    protected abstract List<ProcessableDAO> analysisDAO(Element annotatedElement) throws ProcessorUnexpectedError;
    protected abstract List<ProcessableEntity> analysisEntities(Element annotatedElement) throws ProcessorUnexpectedError;

    protected abstract List<ProcessableTypeConverter> analysisTypeConverters(Element annotatedElement, SupportedParameterManager supportedParameterManager) throws ProcessorUnexpectedError, ProcessorException;

    protected abstract void verifyProcessableObjects() throws ProcessorUnexpectedError, ProcessorException;

    protected abstract void startDAOProcessor() throws ProcessorError;

    protected abstract List<CodeBlock> getCodeInInitDatabase() throws ProcessorError;

    public final ArkDatabaseProcessor getProcessor() {
        return processor;
    }

    protected final DAOProcessor getDaoProcessor() {
        return daoProcessor;
    }

    public final ProcessableDatabase getProcessableDatabase() {
        return processableDatabase;
    }

}
