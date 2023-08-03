package com.lovelycatv.ark.compiler.pre.verify.entity;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.PreProcessException;
import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.pre.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.verify.AbstractProcessableVerification;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SupportedParameterManager;

import java.util.ArrayList;
import java.util.List;

public class EntityVerification extends AbstractProcessableVerification<ProcessableEntity> {
    private final ProcessableTypeConverter.Controller typeConverterController;

    public EntityVerification(DataBaseType dataBaseType, SupportedParameterManager supportedParameterManager, ProcessableEntity processableObject, ProcessableTypeConverter.Controller typeConverterController) {
        super(dataBaseType, supportedParameterManager,processableObject);
        this.typeConverterController = typeConverterController;
    }

    @Override
    public void verify() throws PreProcessException, PreProcessUnexpectedError {
        ProcessableEntity entity = getProcessableObject();
        if (getDataBaseType() == DataBaseType.MYSQL) {
            mysqlVerification(entity);
        } else if (getDataBaseType() == DataBaseType.SQLITE) {
            // Just like mysql
            mysqlVerification(entity);
        } else {
            throw new PreProcessUnexpectedError("If you see this output error that means I've forgot to add statement in this part! class: " + this.getClass().getName());
        }
    }

    private void mysqlVerification(ProcessableEntity entity) throws PreProcessException {
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
                    throw new PreProcessException(String.format("Auto Increment Column %s in %s could only apply to number type!", column.getColumnName(), entityFullName));
                }
            }

            // Whether the field type is supported
            if (!getSupportedParameterManager().isSupportedInJavaTypes(column.getElement().asType())) {
                // Find type converter
                if (typeConverterController.getConverterIfExists(column.getElement().asType()) == null) {
                    throw new PreProcessException(String.format("Unrecognized type of column %s in %s, if you want to skip this field, please annotate it with @Ignore. Or you could try TypeConverter to transform this type to another that database could save.", column.getColumnName(), entityFullName));
                }
            }
        }

        if (countOfPrimaryKey > 1) {
            throw new PreProcessException(String.format("%s can only have one primary key!", entityFullName));
        }
    }


}