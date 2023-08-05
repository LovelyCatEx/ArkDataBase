package com.lovelycatv.ark.common.enums;

import com.lovelycatv.ark.runtime.constructures.base.relational.MySQLManager;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.SQLiteManager;

public enum DataBaseType {
    MYSQL(Type.RELATIONAL),
    SQLITE(Type.RELATIONAL);

    public static Class<? extends RelationalDatabase> getRelationalDatabaseByType(DataBaseType type) {
        if (type == MYSQL) {
            return MySQLManager.class;
        } else if (type == SQLITE) {
            return SQLiteManager.class;
        }
        return null;
    }

    public Type type;
    DataBaseType(Type type) {
        this.type = type;
    }

    public enum Type {
        RELATIONAL,
        NOSQL
    }
}
