package com.lovelycatv.ark.test.sqlite;

import com.lovelycatv.ark.Ark;
import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.runtime.ArkDatabase;
import com.lovelycatv.ark.test.mysql.MyDatabase;
import com.lovelycatv.ark.test.sqlite.dao.AccessLogDAO;
import com.lovelycatv.ark.test.sqlite.entities.AccessLog;

@Database(dataBaseType = DataBaseType.SQLITE, entities = {AccessLog.class}, typeConverters = {}, version = 1)
public abstract class MyLocalDatabase extends ArkDatabase {
    private static MyLocalDatabase INSTANCE;

    public abstract AccessLogDAO accessLogDAO();

    public static synchronized MyLocalDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = Ark.getRelationalDatabaseBuilder()
                    .sqlite("./arkSQLite.db")
                    .createDatabase(MyLocalDatabase.class);
        }
        return INSTANCE;
    }
}
