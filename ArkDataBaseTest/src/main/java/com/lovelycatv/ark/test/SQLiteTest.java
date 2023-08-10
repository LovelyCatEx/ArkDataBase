package com.lovelycatv.ark.test;


import com.lovelycatv.ark.test.sqlite.MyLocalDatabase;
import com.lovelycatv.ark.test.sqlite.entities.AccessLog;

import java.util.UUID;

public class SQLiteTest {
    private static final MyLocalDatabase database = MyLocalDatabase.getInstance();

    public static void main(String[] args) {
        sqlite_insert();
    }

    public static void sqlite_delete(int logId) {
        AccessLog log = new AccessLog();
        log.setId(logId);
        database.accessLogDAO().delete(log);
    }

    public static void sqlite_insert() {
        AccessLog log = new AccessLog();
        for (int i = 0; i < 10; i++) {
            log.setAccessName(UUID.randomUUID().toString());
            log.setAccessTimestamp((long) (System.currentTimeMillis() + (Math.random() * 1000)));
            database.accessLogDAO().insert(log);
        }
    }
}
