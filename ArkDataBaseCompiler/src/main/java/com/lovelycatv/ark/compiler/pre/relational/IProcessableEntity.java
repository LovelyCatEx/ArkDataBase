package com.lovelycatv.ark.compiler.pre.relational;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.sql.IBaseSQLStatement;

public interface IProcessableEntity {
    IBaseSQLStatement createBaseSQLStatement(DataBaseType dataBaseType) throws ProcessorError;

}
