package com.lovelycatv.ark.compiler.pre;

import com.lovelycatv.ark.common.annotations.Column;
import com.lovelycatv.ark.common.annotations.Entity;
import com.lovelycatv.ark.common.annotations.Ignore;
import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import lombok.Data;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.List;
public final class ProcessableEntity extends AbstractProcessable {
    private DeclaredType declaredEntityType;

    private String tableName;

    private final List<EntityColumn> entityColumnList = new ArrayList<>();

    public ProcessableEntity() {
        super(ProcessableType.ENTITY);
    }

    public static ProcessableEntity builder(DeclaredType entityType) throws PreProcessUnexpectedError {
        final ProcessableEntity result = new ProcessableEntity();

        result.setDeclaredEntityType(entityType);

        final TypeElement typeElement = (TypeElement) entityType.asElement();

        final Entity entity = typeElement.getAnnotation(Entity.class);
        if (entity == null) {
            throw new PreProcessUnexpectedError(String.format("Cannot find @Entity in %s",
                    entityType.asElement().asType().toString()));
        }

        result.setTableName(entity.tableName());

        // Find all columns
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement.getKind() != ElementKind.FIELD || enclosedElement.getAnnotation(Ignore.class) != null) {
                continue;
            }
            final EntityColumn entityColumn = new EntityColumn();
            entityColumn.setElement(enclosedElement);

            final Column column = enclosedElement.getAnnotation(Column.class);
            if (column == null) {
                entityColumn.setColumnName(enclosedElement.getSimpleName().toString());
            } else {
                entityColumn.setColumnName(column.columnName());
                entityColumn.setPrimaryKey(column.primaryKey());
                entityColumn.setUnique(column.unique());
                entityColumn.setNotNull(column.notNull());
                entityColumn.setAutoIncrement(column.autoIncrease());
            }
            result.getEntityColumnList().add(entityColumn);
        }

        return result;
    }


    public String getTableName() {
        return tableName;
    }

    public DeclaredType getDeclaredEntityType() {
        return declaredEntityType;
    }

    public List<EntityColumn> getEntityColumnList() {
        return entityColumnList;
    }

    public void setDeclaredEntityType(DeclaredType declaredEntityType) {
        this.declaredEntityType = declaredEntityType;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Data
    public static class EntityColumn {
        private Element element;

        private String columnName;

        private boolean autoIncrement;

        private boolean primaryKey;

        private boolean unique;

        private boolean notNull;
    }

    @Data
    public static class Controller {
        private final List<ProcessableEntity> entityList = new ArrayList<>();

        public List<ProcessableEntity> getEntityList() {
            return entityList;
        }
    }
}
