package com.lovelycatv.ark.compiler.pre.relational.sql;

import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;

public interface IBaseSQLStatement {
    StandardSQLStatement getInsertSQLStatement(ProcessableEntity processableEntity);

    StandardSQLStatement getUpdateSQLStatement(ProcessableEntity processableEntity) throws ProcessorError;

    StandardSQLStatement getDeleteSQLStatement(ProcessableEntity processableEntity) throws ProcessorError;
}
