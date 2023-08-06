package com.lovelycatv.ark.compiler.processor.relational.objects;

import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.ProcessorVars;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.relational.sql.StandardSQLStatement;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.processor.relational.children.DAOProcessor;
import com.lovelycatv.ark.compiler.processor.relational.children.base.AbstractDatabaseProcessor;
import com.lovelycatv.ark.runtime.constructures.adapters.BaseEntityAdapter;
import com.lovelycatv.ark.runtime.constructures.adapters.EntityDeleteAdapter;
import com.lovelycatv.ark.runtime.constructures.adapters.EntityInsertAdapter;
import com.lovelycatv.ark.runtime.constructures.adapters.EntityUpdateAdapter;
import com.lovelycatv.ark.runtime.supported.ArkJDBC;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityAdapterInfo {
    private ProcessableEntity targetEntity;
    private final SupportedParameterManager supportedParameterManager;
    private final List<ProcessableTypeConverter> typeConverterList;
    private final AbstractDatabaseProcessor databaseProcessor;

    public EntityAdapterInfo(AbstractDatabaseProcessor databaseProcessor, SupportedParameterManager supportedParameterManager, List<ProcessableTypeConverter> typeConverterList) {
        this.supportedParameterManager = supportedParameterManager;
        this.typeConverterList = typeConverterList;
        this.databaseProcessor = databaseProcessor;
    }

    public Map<Class<? extends Annotation>, FieldSpec> annotationWithFields = new HashMap<>();
    public Map<Class<? extends Annotation>, TypeSpec> annotationWithAnonymousTypes = new HashMap<>();

    public void buildFiledList() {
        final FieldSpec insert = FieldSpec.builder(ClassName.get(EntityInsertAdapter.class), getFieldName(Insert.class), Modifier.PRIVATE, Modifier.FINAL).build();
        final FieldSpec delete = FieldSpec.builder(ClassName.get(EntityDeleteAdapter.class), getFieldName(Delete.class), Modifier.PRIVATE, Modifier.FINAL).build();
        final FieldSpec update = FieldSpec.builder(ClassName.get(EntityUpdateAdapter.class), getFieldName(Update.class), Modifier.PRIVATE, Modifier.FINAL).build();
        annotationWithFields.put(Insert.class, insert);
        annotationWithFields.put(Update.class, update);
        annotationWithFields.put(Delete.class, delete);
    }

    public void buildAdapterAnonymousTypes(DataBaseType dataBaseType) throws ProcessorError {
        final ParameterizedTypeName insertAdapter = ParameterizedTypeName.get(ClassName.get(EntityInsertAdapter.class), ClassName.get(targetEntity.getDeclaredEntityType()));
        final ParameterizedTypeName deleteAdapter = ParameterizedTypeName.get(ClassName.get(EntityDeleteAdapter.class), ClassName.get(targetEntity.getDeclaredEntityType()));
        final ParameterizedTypeName updateAdapter = ParameterizedTypeName.get(ClassName.get(EntityUpdateAdapter.class), ClassName.get(targetEntity.getDeclaredEntityType()));

        final StandardSQLStatement insertStatement = targetEntity.createBaseSQLStatement(dataBaseType).getInsertSQLStatement(targetEntity);
        final StandardSQLStatement deleteStatement = targetEntity.createBaseSQLStatement(dataBaseType).getDeleteSQLStatement(targetEntity);
        final StandardSQLStatement updateStatement = targetEntity.createBaseSQLStatement(dataBaseType).getUpdateSQLStatement(targetEntity);

        final ParameterizedTypeName[] adapters = new ParameterizedTypeName[]{insertAdapter, updateAdapter, deleteAdapter};
        final StandardSQLStatement[] statements = new StandardSQLStatement[]{insertStatement, updateStatement, deleteStatement};

        for (int i = 0, adaptersLength = adapters.length; i < adaptersLength; i++) {
            ParameterizedTypeName adapter = adapters[i];
            StandardSQLStatement statement = statements[i];

            final String parameterName_bind_preparedStatement = "_preparedStatement";
            final String parameterName_bind_entity = "_entity";

            MethodSpec.Builder bind = MethodSpec.methodBuilder(BaseEntityAdapter.ABSTRACT_METHOD_BIND)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(PreparedStatement.class, parameterName_bind_preparedStatement)
                    .addParameter(ClassName.get(targetEntity.getDeclaredEntityType()), parameterName_bind_entity)
                    .returns(void.class);

            bind.beginControlFlow("try");
            for (Map.Entry<Integer, String> stmtEntry : statement.getSlotWithColumnName().entrySet()) {
                ProcessableEntity.EntityColumn column = targetEntity.getColumnByName(stmtEntry.getValue());
                if (column == null) {
                    throw new ProcessorError(String.format("Could not find column when trying to process adapters in %s",
                            targetEntity.getDeclaredEntityType().asElement().asType().toString()));
                }
                String methodName = "";
                for (Map.Entry<Class<?>, String> tmpEntry : ArkJDBC.getJavaTypeClassWithBindMethodNameMap().entrySet()) {
                    if (tmpEntry.getKey().getName().equals(column.getColumnType(supportedParameterManager, typeConverterList).toString())) {
                        methodName = ArkJDBC.getJavaTypeClassWithBindMethodNameMap().get(tmpEntry.getKey());
                        break;
                    }
                }
                if (methodName == null || "".equals(methodName)) {
                    throw new ProcessorError(String.format("Could not find binding method name of PreparedStatement when trying to process adapters in %s",
                            targetEntity.getDeclaredEntityType().asElement().asType().toString()));
                }
                boolean usingTypeConverter = column.isAboutToTypeConverter(supportedParameterManager);
                if (usingTypeConverter) {
                    ProcessableTypeConverter.Converter converter = column.getTypeConverter(typeConverterList);
                    bind.addStatement("$L.$L($L, $L.$L($L.$L()))", parameterName_bind_preparedStatement, methodName, stmtEntry.getKey(),
                            ProcessorVars.getTypeConverterClassname(this.databaseProcessor.getProcessableDatabase().getClassElement().getSimpleName().toString()), converter.getMethodNameInDAO(),
                            parameterName_bind_entity, column.getGetter().getMethodElement().getSimpleName().toString());
                } else {
                    bind.addStatement("$L.$L($L, $L.$L())", parameterName_bind_preparedStatement, methodName, stmtEntry.getKey(), parameterName_bind_entity,
                            column.getGetter().getMethodElement().getSimpleName().toString());
                }

            }
            bind.nextControlFlow("catch ($T e)", SQLException.class);
            bind.addStatement("throw new $T(e)", RuntimeException.class);
            bind.endControlFlow();

            TypeSpec type = TypeSpec.anonymousClassBuilder("this." + DAOProcessor.FIELD_DAO_DATABASE)
                    .addSuperinterface(adapter)
                    .addMethod(MethodSpec.methodBuilder(BaseEntityAdapter.ABSTRACT_METHOD_CREATE_QUERY_SQL)
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(String.class)
                            .addStatement("return \"" + statement.getSql() + "\"")
                            .build())
                    .addMethod(bind.build())
                    .build();

            Class<? extends Annotation> annotation = null;
            switch (i) {
                case 0:
                    annotation = Insert.class;
                    break;
                case 1:
                    annotation = Update.class;
                    break;
                case 2:
                    annotation = Delete.class;
                    break;
                default:
                    break;
            }
            annotationWithAnonymousTypes.put(annotation, type);
        }
    }

    public String getFieldName(Class<? extends Annotation> target) {
        return "__" + targetEntity.getTableName() + target.getSimpleName() + "Adapter";
    }

    public ProcessableEntity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(ProcessableEntity targetEntity) {
        this.targetEntity = targetEntity;
    }
}
