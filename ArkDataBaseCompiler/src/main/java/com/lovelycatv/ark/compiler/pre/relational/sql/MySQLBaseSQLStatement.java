package com.lovelycatv.ark.compiler.pre.relational.sql;

import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.utils.APTools;

import java.util.ArrayList;
import java.util.List;

public class MySQLBaseSQLStatement implements IBaseSQLStatement {
    @Override
    public List<StandardSQLStatement> getCreateTableStatement(ProcessableEntity processableEntity,
                                                              List<ProcessableTypeConverter> typeConverterList,
                                                              SupportedParameterManager supportedParameterManager) {
        List<StandardSQLStatement> result = new ArrayList<>();
        StringBuilder values = new StringBuilder();

        for (ProcessableEntity.EntityColumn column : processableEntity.getEntityColumnList()) {
            values.append(String.format("`%s` %s ", column.getColumnName(), supportedParameterManager
                    .getSupportedInJavaTypes(column.getColumnType(supportedParameterManager, typeConverterList)).getTypeNameInDatabase()));
            if (column.isPrimaryKey()) {
                values.append(" PRIMARY KEY");
            }
            if (column.isUnique()) {
                values.append(" UNIQUE");
            }
            if (column.isAutoIncrement()) {
                values.append(" ").append(supportedParameterManager.AUTO_INCREMENT);
            }
            if (column.isNotNull()) {
                values.append(" NOT NULL");
            }
            values.append(", ");
        }

        values = new StringBuilder(values.substring(0, values.length() - 2));
        String sql = String.format("CREATE TABLE IF NOT EXISTS `%s` (%s)", processableEntity.getTableName(), values);
        StandardSQLStatement statement = new StandardSQLStatement();
        statement.setSql(sql);

        result.add(statement);

        return result;
    }

    @Override
    public StandardSQLStatement getInsertSQLStatement(ProcessableEntity processableEntity) {
        StandardSQLStatement standardSQLStatement = new StandardSQLStatement();

        List<ProcessableEntity.EntityColumn> columns = processableEntity.getEntityColumnList();
        String result = "INSERT INTO `%s` (%s) VALUES (%s)";
        StringBuilder values = new StringBuilder();
        StringBuilder placeHolders = new StringBuilder();
        int index = 1;
        for (ProcessableEntity.EntityColumn column : columns) {
            if (column.isAutoIncrement()) {
                continue;
            }
            values.append("`").append(column.getColumnName()).append("`, ");
            placeHolders.append(", ");
            standardSQLStatement.getSlotWithColumnName().put(index, column.getColumnName());
            index++;
        }
        values = new StringBuilder(values.substring(0, values.length() - 2));
        placeHolders = new StringBuilder(placeHolders.substring(0, placeHolders.length() - 2));
        standardSQLStatement.setSql(String.format(result, processableEntity.getTableName(), values, placeHolders));
        return standardSQLStatement;
    }

    @Override
    public StandardSQLStatement getUpdateSQLStatement(ProcessableEntity processableEntity) throws ProcessorError {
        StandardSQLStatement standardSQLStatement = new StandardSQLStatement();

        List<ProcessableEntity.EntityColumn> columns = processableEntity.getEntityColumnList();
        String result = "UPDATE `%s` SET %s WHERE `%s` = ?";

        if (!processableEntity.hasPrimaryKey()) {
            throw new ProcessorError(String.format("To use the @Update, you must define a primary key in entity %s", APTools.getClassNameFromTypeMirror(processableEntity.getDeclaredEntityType())));
        }

        String primaryKey = processableEntity.getPrimaryKey();
        String setBody = "";
        int index = 1;
        for (ProcessableEntity.EntityColumn column : columns) {
            setBody = String.format("`%s` = ?, ", column.getColumnName());
            standardSQLStatement.getSlotWithColumnName().put(index, column.getColumnName());
            index++;
        }
        setBody = setBody.substring(0, setBody.length() - 2);
        standardSQLStatement.setSql(String.format(result, processableEntity.getTableName(), setBody, primaryKey));
        return standardSQLStatement;
    }

    @Override
    public StandardSQLStatement getDeleteSQLStatement(ProcessableEntity processableEntity) throws ProcessorError {
        StandardSQLStatement standardSQLStatement = new StandardSQLStatement();
        String result = "DELETE FROM `%s` WHERE `%s` = ?";

        if (!processableEntity.hasPrimaryKey()) {
            throw new ProcessorError(String.format("To use the @Update, you must define a primary key in entity %s", APTools.getClassNameFromTypeMirror(processableEntity.getDeclaredEntityType())));
        }

        String primaryKey = processableEntity.getPrimaryKey();
        standardSQLStatement.getSlotWithColumnName().put(1, primaryKey);
        standardSQLStatement.setSql(String.format(result, processableEntity.getTableName(), primaryKey));
        return standardSQLStatement;
    }
}
