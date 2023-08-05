package com.lovelycatv.ark.compiler.processor.relational.objects;

import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.sql.StandardSQLStatement;
import com.lovelycatv.ark.runtime.constructures.adapters.BaseEntityAdapter;
import com.lovelycatv.ark.runtime.constructures.adapters.EntityDeleteAdapter;
import com.lovelycatv.ark.runtime.constructures.adapters.EntityInsertAdapter;
import com.lovelycatv.ark.runtime.constructures.adapters.EntityUpdateAdapter;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityAdapterInfo {
    private ProcessableEntity targetEntity;

    public Map<Class<? extends Annotation>, FieldSpec> annotationWithFields = new HashMap<>();
    public Map<Class<? extends Annotation>, TypeSpec> annotationWithAnonymousTypes = new HashMap<>();

    public List<FieldSpec> buildFiledList() {
        final List<FieldSpec> result = new ArrayList<>();
        final FieldSpec insert = FieldSpec.builder(ClassName.get(EntityInsertAdapter.class), getFieldName(Insert.class), Modifier.PRIVATE, Modifier.FINAL).build();
        final FieldSpec delete = FieldSpec.builder(ClassName.get(EntityDeleteAdapter.class), getFieldName(Delete.class), Modifier.PRIVATE, Modifier.FINAL).build();
        final FieldSpec update = FieldSpec.builder(ClassName.get(EntityUpdateAdapter.class), getFieldName(Update.class), Modifier.PRIVATE, Modifier.FINAL).build();
        annotationWithFields.put(Insert.class, insert);
        annotationWithFields.put(Update.class, update);
        annotationWithFields.put(Delete.class, delete);
        result.add(insert);
        result.add(delete);
        result.add(update);
        return result;
    }

    public void buildAdapterAnonymousTypes(DataBaseType dataBaseType) throws ProcessorError {
        final ParameterizedTypeName insertAdapter = ParameterizedTypeName.get(ClassName.get(EntityInsertAdapter.class), ClassName.get(targetEntity.getDeclaredEntityType()));
        final ParameterizedTypeName deleteAdapter = ParameterizedTypeName.get(ClassName.get(EntityDeleteAdapter.class), ClassName.get(targetEntity.getDeclaredEntityType()));
        final ParameterizedTypeName updateAdapter = ParameterizedTypeName.get(ClassName.get(EntityUpdateAdapter.class), ClassName.get(targetEntity.getDeclaredEntityType()));

        final StandardSQLStatement insertStatement = targetEntity.getInsertSQLStatement(dataBaseType);
        final StandardSQLStatement deleteStatement = targetEntity.getDeleteSQLStatement(dataBaseType);
        final StandardSQLStatement updateStatement = targetEntity.getUpdateSQLStatement(dataBaseType);

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
            for (Map.Entry<Integer, String> entry : statement.getSlotWithColumnName().entrySet()) {

            }
            bind.nextControlFlow("catch ($T e)", SQLException.class);
            bind.addStatement("throw new $T(e)", RuntimeException.class);
            bind.endControlFlow();

            TypeSpec type = TypeSpec.anonymousClassBuilder("")
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
