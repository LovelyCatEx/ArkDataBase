package com.lovelycatv.ark.runtime;

import com.lovelycatv.ark.common.enums.DataBaseType;

public class BaseArkDatabase {
    private DataBaseType dataBaseType;

    public BaseArkDatabase(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public final DataBaseType getDataBaseType() {
        return dataBaseType;
    }
}
