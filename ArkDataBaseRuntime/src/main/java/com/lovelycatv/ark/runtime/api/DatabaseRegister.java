package com.lovelycatv.ark.runtime.api;

import com.lovelycatv.ark.runtime.constructures.base.AnyDatabase;
import com.lovelycatv.ark.runtime.constructures.base.nosql.NoSQLDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRegister {
    public static final List<AnyDatabase> registeredDatabaseList = new ArrayList<>();

    public static DatabaseRegister getRegister() {
        return new DatabaseRegister();
    }

    public boolean registerDatabase(AnyDatabase anyDatabase) {
        if (anyDatabase instanceof RelationalDatabase) {

        } else if (anyDatabase instanceof NoSQLDatabase) {

        } else {
            return false;
        }
    }

    private boolean registerRelationalDatabase(RelationalDatabase relationalDatabase) {

    }

    private boolean registerNoSQLDatabase(NoSQLDatabase noSQLDatabase) {

    }


}
