package com.lovelycatv.ark.runtime.simulation.impl;

import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.BaseArkDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;
import com.lovelycatv.ark.runtime.simulation.MyDatabase;
import com.lovelycatv.ark.runtime.simulation.dao.UserDAO;

public class MyDatabaseImpl extends MyDatabase {
    @Override
    public ArkRelationalDatabase<? extends RelationalDatabase> getDatabase() {
        return (ArkRelationalDatabase<? extends RelationalDatabase>) super.getDatabase();
    }

    @Override
    public void initDatabase() {

    }

    @Override
    public UserDAO userDAO() {
        return null;
    }
}
