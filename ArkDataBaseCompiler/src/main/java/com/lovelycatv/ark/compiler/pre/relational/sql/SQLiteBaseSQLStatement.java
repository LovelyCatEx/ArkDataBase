package com.lovelycatv.ark.compiler.pre.relational.sql;

import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;

public class SQLiteBaseSQLStatement implements IBaseSQLStatement {
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
