package com.lovelycatv.ark.runtime.constructures.adapters;

import com.lovelycatv.ark.runtime.ArkDatabase;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BaseEntityAdapter {
    public static final String ABSTRACT_METHOD_CREATE_QUERY_SQL = "createQuerySQL";
    public static final String ABSTRACT_METHOD_BIND = "bind";
    protected final ArkRelationalDatabase<? extends RelationalDatabase> dataBase;
    public BaseEntityAdapter(ArkRelationalDatabase<? extends RelationalDatabase> dataBase) {
        this.dataBase = dataBase;
    }
    public abstract String createQuerySQL();

    public PreparedStatement getStatement() throws SQLException {
        return dataBase.getDatabaseManager().getConnection().prepareStatement(createQuerySQL());
    }

}
