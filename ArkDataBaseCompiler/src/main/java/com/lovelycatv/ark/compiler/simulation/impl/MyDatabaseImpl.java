package com.lovelycatv.ark.compiler.simulation.impl;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.simulation.MyDatabase;
import com.lovelycatv.ark.compiler.simulation.dao.UserDAO;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.MySQLManager;

public final class MyDatabaseImpl extends MyDatabase {
    private volatile UserDAO_Impl _userDAO;

    @Override
    public ArkRelationalDatabase<MySQLManager> getDatabase() {
        return (ArkRelationalDatabase<MySQLManager>) super.getDatabase();
    }

    @Override
    public void initDatabase() {
        super.setBaseArkDatabase(new ArkRelationalDatabase<MySQLManager>(DataBaseType.MYSQL) {
            @Override
            public void initDataBase() {
                // Create Tables
                getDatabaseManager().execute("", null);
            }
        });
    }

    @Override
    public UserDAO userDAO() {
        if (this._userDAO == null) {
            this._userDAO = new UserDAO_Impl(this.getDatabase());
        }
        return this._userDAO;
    }
}
