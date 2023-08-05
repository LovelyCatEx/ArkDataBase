package com.lovelycatv.ark.compiler.pre.relational;

import com.lovelycatv.ark.common.annotations.Column;
import com.lovelycatv.ark.common.annotations.Entity;
import com.lovelycatv.ark.common.annotations.Ignore;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.sql.MySQLBaseSQLStatement;
import com.lovelycatv.ark.compiler.pre.relational.sql.SQLiteBaseSQLStatement;
import com.lovelycatv.ark.compiler.pre.relational.sql.IBaseSQLStatement;
import com.lovelycatv.ark.compiler.pre.relational.sql.StandardSQLStatement;
import com.lovelycatv.ark.compiler.utils.APTools;
import lombok.Data;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
public final class ProcessableEntity extends AbstractProcessable implements IProcessableEntity {
    private DeclaredType declaredEntityType;

    private String tableName;

    private final List<EntityColumn> entityColumnList = new ArrayList<>();

    public ProcessableEntity() {
        super(ProcessableType.ENTITY);
    }

    public static ProcessableEntity builder(DeclaredType entityType) throws ProcessorUnexpectedError {
        final ProcessableEntity result = new ProcessableEntity();

        result.setDeclaredEntityType(entityType);

        final TypeElement typeElement = (TypeElement) entityType.asElement();

        final Entity entity = typeElement.getAnnotation(Entity.class);
        if (entity == null) {
            throw new ProcessorUnexpectedError(String.format("Cannot find @Entity in %s",
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


    @Override
    public IBaseSQLStatement createBaseSQLStatement(DataBaseType dataBaseType) throws ProcessorError {
        IBaseSQLStatement supportedBaseSQLStatement;
        if (dataBaseType == DataBaseType.MYSQL) {
            supportedBaseSQLStatement = new MySQLBaseSQLStatement();
        } else if (dataBaseType == DataBaseType.SQLITE) {
            supportedBaseSQLStatement = new SQLiteBaseSQLStatement();
        } else {
            throw new ProcessorError("If you see this output error that means I've forgot to add statement in this part! class: " + this.getClass().getName());
        }
        return supportedBaseSQLStatement;
    }

    @Override
    public StandardSQLStatement getInsertSQLStatement(DataBaseType dataBaseType) throws ProcessorError {
        return createBaseSQLStatement(dataBaseType).getInsertSQLStatement(this);
    }

    @Override
    public StandardSQLStatement getDeleteSQLStatement(DataBaseType dataBaseType) throws ProcessorError {
        return createBaseSQLStatement(dataBaseType).getDeleteSQLStatement(this);
    }

    @Override
    public StandardSQLStatement getUpdateSQLStatement(DataBaseType dataBaseType) throws ProcessorError {
        return createBaseSQLStatement(dataBaseType).getUpdateSQLStatement(this);
    }

    public boolean hasPrimaryKey() {
        for (EntityColumn column : getEntityColumnList()) {
            if (column.isPrimaryKey()) {
                return true;
            }
        }
        return false;
    }

    public String getPrimaryKey() {
        for (EntityColumn column : getEntityColumnList()) {
            if (column.isPrimaryKey()) {
                return column.getColumnName();
            }
        }
        return null;
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

        private int cursor;
    }

    @Data
    public static class Controller {
        private final List<ProcessableEntity> entityList = new ArrayList<>();

        public List<ProcessableEntity> getEntityList() {
            return entityList;
        }

        /**
         * @param typeMirror Such as java.util.List<com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity>
         * @return detect whether ProcessableEntity is in list
         */
        public boolean containsEntityType(TypeMirror typeMirror) {
            for (ProcessableEntity entity : getEntityList()) {
                if (APTools.getClassNameFromTypeMirror(typeMirror).contains(entity.getDeclaredEntityType().asElement().asType().toString())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @param typeMirror Such as java.util.List<com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity>
         * @return detect whether ProcessableEntity is in list and return the ProcessableEntity object
         */
        public ProcessableEntity getEntityIndistinct(TypeMirror typeMirror) {
            for (ProcessableEntity entity : getEntityList()) {
                if (APTools.getClassNameFromTypeMirror(typeMirror).contains(entity.getDeclaredEntityType().asElement().asType().toString())) {
                    return entity;
                }
            }
            return null;
        }
    }
}
