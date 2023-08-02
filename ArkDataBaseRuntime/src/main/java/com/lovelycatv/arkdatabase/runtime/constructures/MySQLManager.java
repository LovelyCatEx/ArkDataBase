package com.lovelycatv.arkdatabase.runtime.constructures;

import com.lovelycatv.arkdatabase.runtime.constructures.base.relational.RelationalDatabase;
import com.lovelycatv.arkdatabase.runtime.constructures.base.relational.interfaces.RelationalExecuteCallBack;
import com.lovelycatv.arkdatabase.runtime.constructures.base.relational.interfaces.RelationalExecuteQueryCallBack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class MySQLManager extends RelationalDatabase {
    public static final String AUTO_INCREMENT = "AUTO_INCREMENT";
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String database;

    public MySQLManager(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    @Override
    public Connection getConnection() {
        try {
            if (!isConnectionValid()) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://"+ host + ":" + port +"/" + database + "?useUnicode=true&characterEncoding=utf-8&useSSL=false",
                            username,password);
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
