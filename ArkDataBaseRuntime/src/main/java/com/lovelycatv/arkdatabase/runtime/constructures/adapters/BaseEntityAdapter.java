package com.lovelycatv.arkdatabase.runtime.constructures.adapters;

import com.lovelycatv.arkdatabase.runtime.ArkDatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BaseEntityAdapter {
    protected final ArkDatabase dataBase;
    public BaseEntityAdapter(ArkDatabase dataBase) {
        this.dataBase = dataBase;
    }
    public abstract String createQuerySQL();

    public PreparedStatement getStatement() throws SQLException {
        return dataBase.getDatabaseManager().getConnection().prepareStatement(createQuerySQL());
    }

}
