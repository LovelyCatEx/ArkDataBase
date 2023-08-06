package com.lovelycatv.ark.compiler.pre.relational.verify.entity;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.ProcessorException;
import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.relational.verify.AbstractProcessableVerification;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.utils.APTools;

import java.util.ArrayList;
import java.util.List;

public class EntityVerification extends AbstractProcessableVerification<ProcessableEntity> {
    private final ProcessableTypeConverter.Controller typeConverterController;

    public EntityVerification(DataBaseType dataBaseType, SupportedParameterManager supportedParameterManager, ProcessableEntity processableObject, ProcessableTypeConverter.Controller typeConverterController) {
        super(dataBaseType, supportedParameterManager,processableObject);
        this.typeConverterController = typeConverterController;
    }

    @Override
    public void verify() throws ProcessorException, ProcessorUnexpectedError {
        ProcessableEntity entity = getProcessableObject();
        // General verification
        {
            List<String> tmpColumnNames = new ArrayList<>();
            for (ProcessableEntity.EntityColumn column : entity.getEntityColumnList()) {
                if (tmpColumnNames.contains(column.getColumnName())) {
                    throw new ProcessorException(String.format("Entity %s has duplicated column %s",
                            APTools.getClassNameFromTypeMirror(entity.getDeclaredEntityType().asElement().asType()), column.getColumnName()));
                } else {
                    tmpColumnNames.add(column.getColumnName());
                }
            }
        }

        if (getDataBaseType() == DataBaseType.MYSQL) {
            mysqlVerification(entity);
        } else if (getDataBaseType() == DataBaseType.SQLITE) {
            // Just like mysql
            mysqlVerification(entity);
        } else {
            throw new ProcessorUnexpectedError("If you see this output error that means I've forgot to add statement in this part! class: " + this.getClass().getName());
        }
    }

    private void mysqlVerification(ProcessableEntity entity) throws ProcessorException {
        final String entityFullName = entity.getDeclaredEntityType().asElement().asType().toString();
        // Only one primary key
        int countOfPrimaryKey = 0;
        for (ProcessableEntity.EntityColumn column : entity.getEntityColumnList()) {
            if (column.isPrimaryKey()) {
                countOfPrimaryKey++;
            }

            // Auto-increment could only apply to int type
            if (column.isAutoIncrement()) {
                if (!SupportedParameterManager.isJavaNumberTypes(column.getElement().asType())) {
                    throw new ProcessorException(String.format("Auto Increment Column %s in %s could only apply to number type!", column.getColumnName(), entityFullName));
                }
            }

            // Whether the field type is supported
            if (!getSupportedParameterManager().isSupportedInJavaTypes(column.getElement().asType())) {
                // Find type converter
                if (typeConverterController.getConverterIfExists(column.getElement().asType()) == null) {
                    throw new ProcessorException(String.format("Unrecognized type of column %s in %s, if you want to skip this field, please annotate it with @Ignore. Or you could try TypeConverter to transform this type to another that database could save.", column.getColumnName(), entityFullName));
                }
            }
        }

        if (countOfPrimaryKey > 1) {
            throw new ProcessorException(String.format("%s can only have one primary key!", entityFullName));
        }
    }


}
