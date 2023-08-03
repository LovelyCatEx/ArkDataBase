package com.lovelycatv.ark.runtime;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;

public abstract class ArkRelationalDatabase<T extends RelationalDatabase> extends BaseArkDatabase {
    protected T databaseManager;

    public ArkRelationalDatabase(DataBaseType dataBaseType) {
        super(dataBaseType);
    }

    public T getDatabaseManager() {
        return databaseManager;
    }

    public abstract void initDataBase();
}
