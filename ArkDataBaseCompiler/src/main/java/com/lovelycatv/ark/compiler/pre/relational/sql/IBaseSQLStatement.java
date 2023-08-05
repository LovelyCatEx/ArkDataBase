package com.lovelycatv.ark.compiler.pre.relational.sql;

import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;

import java.util.List;

public interface IBaseSQLStatement {
    List<StandardSQLStatement> getCreateTableStatement(ProcessableEntity processableEntity, List<ProcessableTypeConverter> typeConverterList, SupportedParameterManager supportedParameterManager);

    StandardSQLStatement getInsertSQLStatement(ProcessableEntity processableEntity);

    StandardSQLStatement getUpdateSQLStatement(ProcessableEntity processableEntity) throws ProcessorError;

    StandardSQLStatement getDeleteSQLStatement(ProcessableEntity processableEntity) throws ProcessorError;
}
