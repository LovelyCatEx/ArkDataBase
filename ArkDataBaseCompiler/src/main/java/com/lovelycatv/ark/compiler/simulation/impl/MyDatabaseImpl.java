package com.lovelycatv.ark.compiler.simulation.impl;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.MySQLManager;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;
import com.lovelycatv.ark.runtime.simulation.MyDatabase;
import com.lovelycatv.ark.runtime.simulation.dao.UserDAO;

public final class MyDatabaseImpl extends MyDatabase {
    private volatile UserDAO_Impl userDAO;

    @Override
    public ArkRelationalDatabase<? extends RelationalDatabase> getDatabase() {
        return (ArkRelationalDatabase<? extends RelationalDatabase>) super.getDatabase();
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
        if (this.userDAO == null) {
            this.userDAO = new UserDAO_Impl(this.getDatabase());
        }
        return this.userDAO;
    }
}
