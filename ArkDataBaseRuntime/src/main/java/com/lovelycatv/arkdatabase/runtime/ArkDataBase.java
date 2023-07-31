package com.lovelycatv.arkdatabase.runtime;

import com.lovelycatv.arkdatabase.runtime.constructures.base.RelationalDataBase;
import com.lovelycatv.arkdatabase.runtime.enums.DataBaseType;

public abstract class ArkDataBase<T extends RelationalDataBase> {
    private DataBaseType dataBaseType;
    protected T databaseManager;

    public ArkDataBase(DataBaseType dataBaseType) {
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
