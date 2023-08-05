package com.lovelycatv.ark.compiler.pre.relational;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.sql.IBaseSQLStatement;
import com.lovelycatv.ark.compiler.pre.relational.sql.StandardSQLStatement;

public interface IProcessableEntity {
    IBaseSQLStatement createBaseSQLStatement(DataBaseType dataBaseType) throws ProcessorError;

    StandardSQLStatement getInsertSQLStatement(DataBaseType dataBaseType) throws ProcessorError;

    StandardSQLStatement getDeleteSQLStatement(DataBaseType dataBaseType) throws ProcessorError;

    StandardSQLStatement getUpdateSQLStatement(DataBaseType dataBaseType) throws ProcessorError;
}
