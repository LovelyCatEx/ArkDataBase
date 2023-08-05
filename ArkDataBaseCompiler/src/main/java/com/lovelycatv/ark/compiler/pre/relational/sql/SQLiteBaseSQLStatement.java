package com.lovelycatv.ark.compiler.pre.relational.sql;

import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;

import java.util.List;

public class SQLiteBaseSQLStatement implements IBaseSQLStatement {
    @Override
    public List<StandardSQLStatement> getCreateTableStatement(ProcessableEntity processableEntity,
                                                              List<ProcessableTypeConverter> typeConverterList,
                                                              SupportedParameterManager supportedParameterManager) {
        return null;
    }

    @Override
    public StandardSQLStatement getInsertSQLStatement(ProcessableEntity processableEntity) {
        return null;
    }

    @Override
    public StandardSQLStatement getUpdateSQLStatement(ProcessableEntity processableEntity) {
        return null;
    }

    @Override
    public StandardSQLStatement getDeleteSQLStatement(ProcessableEntity processableEntity) {
        return null;
    }
}
