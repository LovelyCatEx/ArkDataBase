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
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.utils.APTools;
import lombok.Data;

import javax.lang.model.element.*;
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

            List<Element> beanMethodElement = APTools.findBeanMethod(enclosedElement, typeElement.getEnclosedElements());

            if (beanMethodElement.size() != 2) {
                throw new ProcessorUnexpectedError(String.format("Cannot find getter and setter method of field %s in %s",
                        enclosedElement.getSimpleName(), entityType.asElement().asType().toString()));
            }

            for (Element element : beanMethodElement) {
                boolean isSetter = element.getSimpleName().toString().startsWith("set");
                BeanMethod beanMethod = new BeanMethod();
                beanMethod.setMethodElement(element);
                beanMethod.setGetter(!isSetter);
                if (isSetter) {
                    entityColumn.setSetter(beanMethod);
                } else {
                    entityColumn.setGetter(beanMethod);
                }
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

    public EntityColumn getColumnByName(String columnName) {
        for (EntityColumn column : entityColumnList) {
            if (columnName.equals(column.getColumnName())) {
                return column;
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
    public static class BeanMethod {
        private Element methodElement;

        // If not, it must be setter
        private boolean isGetter;
    }

    @Data
    public static class EntityColumn {
        private Element element;

        private String columnName;

        private boolean autoIncrement;

        private boolean primaryKey;

        private boolean unique;

        private boolean notNull;

        private BeanMethod getter;

        private BeanMethod setter;

        /**
         * The combination of isAboutToTypeConverter() and getTypeConverter(), by using this method, you could get the column type directly.
         * Assuming the column type is User.class, corresponding typeConverter will transform the User to String. By using this method, you will get the TypeMirror of String.class.
         * If it not found the typeConverter but it should be, then the method will return null
         * @param supportedParameterManager supportedParameterManager
         * @param typeConverters typeConverters
         * @return typeMirror
         */
        public TypeMirror getColumnType(SupportedParameterManager supportedParameterManager, List<ProcessableTypeConverter> typeConverters) {
            if (isAboutToTypeConverter(supportedParameterManager)) {
                return getOutTypeConverter(typeConverters).getTo();
            } else {
                return this.element.asType();
            }
        }

        /**
         * @param supportedParameterManager supportedParameterManager
         * @return Whether the column type should be parsed by typeConverter
         */
        public boolean isAboutToTypeConverter(SupportedParameterManager supportedParameterManager) {
            return !supportedParameterManager.isSupportedInJavaTypes(this.element.asType());
        }

        /**
         * @param typeConverters registered type converters
         * @return find the typeConverter from @param typeConverters of this column
         */
        public ProcessableTypeConverter.Converter getOutTypeConverter(List<ProcessableTypeConverter> typeConverters) {
            ProcessableTypeConverter.Converter found = null;
            out:
            for (ProcessableTypeConverter parentConverterObject : typeConverters) {
                for (ProcessableTypeConverter.Converter outConverter : parentConverterObject.getOutConverters()) {
                    if (APTools.isTheSameTypeMirror(outConverter.getFrom(), this.getElement().asType())) {
                        found = outConverter;
                        break out;
                    }
                }
            }
            return found;
        }

        /**
         * @param typeConverters registered type converters
         * @return find the typeConverter from @param typeConverters of this column
         */
        public ProcessableTypeConverter.Converter getInTypeConverter(List<ProcessableTypeConverter> typeConverters) {
            ProcessableTypeConverter.Converter found = null;
            out:
            for (ProcessableTypeConverter parentConverterObject : typeConverters) {
                for (ProcessableTypeConverter.Converter inConverter : parentConverterObject.getInConverters()) {
                    if (APTools.isTheSameTypeMirror(inConverter.getTo(), this.getElement().asType())) {
                        found = inConverter;
                        break out;
                    }
                }
            }
            return found;
        }

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
