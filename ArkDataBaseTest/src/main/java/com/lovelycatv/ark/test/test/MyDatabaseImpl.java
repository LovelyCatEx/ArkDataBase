package com.lovelycatv.ark.test.test;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.MySQLManager;
import com.lovelycatv.ark.test.MyDatabase;
import com.lovelycatv.ark.test.dao.UserDAO;

public final class MyDatabaseImpl extends MyDatabase {

    @Override
    public UserDAO userDAO() {
        return null;
    }

    @Override
    public void initDatabase() {

    }
}
