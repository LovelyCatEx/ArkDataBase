package com.lovelycatv.ark.compiler.objects;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public final class ProcessableEntity {
    private DeclaredType declaredEntityType;

    private String tableName;

    private List<EntityColumn> entityColumnList = new ArrayList<>();

    public static ProcessableEntity builder(DeclaredType entityType) throws PreProcessUnexpectedError {
        ProcessableEntity result = new ProcessableEntity();

        result.setDeclaredEntityType(entityType);

        TypeElement typeElement = (TypeElement) entityType.asElement();

        Entity entity = typeElement.getAnnotation(Entity.class);
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
            EntityColumn entityColumn = new EntityColumn();
            entityColumn.setElement(enclosedElement);

            Column column = enclosedElement.getAnnotation(Column.class);
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

    @Data
    public static class EntityColumn {
        private Element element;

        private String columnName;

        private boolean autoIncrement;

        private boolean primaryKey;

        private boolean unique;

        private boolean notNull;
    }

    public static class Controller {
        private final List<ProcessableEntity> entityList = new ArrayList<>();

        public List<ProcessableEntity> getEntityList() {
            return entityList;
        }
    }
}
