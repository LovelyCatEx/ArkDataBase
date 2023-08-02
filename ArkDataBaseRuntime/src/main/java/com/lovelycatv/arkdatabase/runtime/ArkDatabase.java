package com.lovelycatv.arkdatabase.runtime;

import com.lovelycatv.arkdatabase.runtime.constructures.base.relational.RelationalDatabase;
import com.lovelycatv.arkdatabase.runtime.enums.DataBaseType;

public abstract class ArkDatabase<T extends RelationalDatabase> {
    private DataBaseType dataBaseType;
    protected T databaseManager;

    public ArkDatabase(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public T getDatabaseManager() {
        return databaseManager;
    }

    public abstract void initDataBase();

}
