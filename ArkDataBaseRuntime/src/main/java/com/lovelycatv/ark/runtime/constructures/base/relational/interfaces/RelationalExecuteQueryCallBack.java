package com.lovelycatv.ark.runtime.constructures.base.relational.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;

public interface RelationalExecuteQueryCallBack {
    void executedQuery(Statement statement, ResultSet resultSet);
}
