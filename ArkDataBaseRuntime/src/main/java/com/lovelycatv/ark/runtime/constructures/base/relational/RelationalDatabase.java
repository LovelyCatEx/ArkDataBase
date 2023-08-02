package com.lovelycatv.ark.runtime.constructures.base.relational;

import com.lovelycatv.ark.runtime.constructures.base.AnyDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.interfaces.IRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.interfaces.RelationalExecuteCallBack;
import com.lovelycatv.ark.runtime.constructures.base.relational.interfaces.RelationalExecuteQueryCallBack;

import java.sql.*;

public abstract class RelationalDatabase extends AnyDatabase implements IRelationalDatabase {
    protected Connection connection;
    @Override
    public Connection getConnection() {
        return null;
    }

    protected boolean isConnectionValid() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(String sql, RelationalExecuteCallBack callBack) {
        try (Statement statement = getConnection().createStatement()) {
            statement.execute(sql);
            if (callBack != null) {
                callBack.executed(statement);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeQuery(String sql, RelationalExecuteQueryCallBack callBack) {
        try {
            Statement statement = getConnection().createStatement();
            ResultSet res = statement.executeQuery(sql);
            if (callBack != null) {
                callBack.executedQuery(statement, res);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeUpdate(String sql, RelationalExecuteCallBack callBack) {
        try (Statement statement = getConnection().createStatement()) {
            statement.executeUpdate(sql);
            if (callBack != null) {
                callBack.executed(statement);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(PreparedStatement sql, RelationalExecuteCallBack callBack) {
        try {
            sql.execute();
            if (callBack != null) {
                callBack.executed(sql);
            }
            sql.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeQuery(PreparedStatement sql, RelationalExecuteQueryCallBack callBack) {
        try {
            ResultSet res = sql.executeQuery();
            if (callBack != null) {
                callBack.executedQuery(sql, res);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeUpdate(PreparedStatement sql, RelationalExecuteCallBack callBack) {
        try {
            sql.executeUpdate();
            if (callBack != null) {
                callBack.executed(sql);
            }
            sql.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
