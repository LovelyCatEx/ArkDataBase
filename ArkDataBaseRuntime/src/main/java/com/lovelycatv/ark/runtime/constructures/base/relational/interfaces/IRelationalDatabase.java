package com.lovelycatv.ark.runtime.constructures.base.relational.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface IRelationalDatabase {
    Connection getConnection();
    void execute(String sql, RelationalExecuteCallBack callBack);

    void executeQuery(String sql, RelationalExecuteQueryCallBack callBack);

    void executeUpdate(String sql, RelationalExecuteCallBack callBack);

    void execute(PreparedStatement sql, RelationalExecuteCallBack callBack);

    void executeQuery(PreparedStatement sql, RelationalExecuteQueryCallBack callBack);

    void executeUpdate(PreparedStatement sql, RelationalExecuteCallBack callBack);
}
