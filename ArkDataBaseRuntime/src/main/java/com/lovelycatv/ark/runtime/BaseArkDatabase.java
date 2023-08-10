package com.lovelycatv.ark.runtime;

import com.lovelycatv.ark.common.enums.DataBaseType;

public class BaseArkDatabase {
    private DataBaseType dataBaseType;

    private Object[] args;

    public BaseArkDatabase(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public final DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public void setArgs(Object... args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}
