package com.lovelycatv.arkdatabase.runtime.constructures;

import com.lovelycatv.arkdatabase.runtime.constructures.base.relational.RelationalDatabase;
import com.lovelycatv.arkdatabase.runtime.constructures.base.relational.interfaces.RelationalExecuteCallBack;
import com.lovelycatv.arkdatabase.runtime.constructures.base.relational.interfaces.RelationalExecuteQueryCallBack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SQLiteManager extends RelationalDatabase {
    public static final String AUTO_INCREMENT = "AUTOINCREMENT";
    private final String database;

    public SQLiteManager(String database) {
        this.database = database;
    }

    @Override
    public Connection getConnection() {
        try {
            if (!isConnectionValid()) {
                try {
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:"+ database);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    @Override
    public void execute(String sql, RelationalExecuteCallBack callBack) {
        super.execute(sql, callBack);
    }

    @Override
    public void executeQuery(String sql, RelationalExecuteQueryCallBack callBack) {
        super.executeQuery(sql, callBack);
    }

    @Override
    public void executeUpdate(String sql, RelationalExecuteCallBack callBack) {
        super.executeUpdate(sql, callBack);
    }

    @Override
    public void execute(PreparedStatement sql, RelationalExecuteCallBack callBack) {
        super.execute(sql, callBack);
    }

    @Override
    public void executeQuery(PreparedStatement sql, RelationalExecuteQueryCallBack callBack) {
        super.executeQuery(sql, callBack);
    }

    @Override
    public void executeUpdate(PreparedStatement sql, RelationalExecuteCallBack callBack) {
        super.executeUpdate(sql, callBack);
    }
}
