package com.lovelycatv.ark.common.enums;

public enum DataBaseType {
    MYSQL(Type.RELATIONAL),
    SQLITE(Type.RELATIONAL);

    public Type type;
    DataBaseType(Type type) {
        this.type = type;
    }

    public enum Type {
        RELATIONAL,
        NOSQL
    }
}
