package com.lovelycatv.ark.test;


import com.lovelycatv.ark.test.sqlite.MyLocalDatabase;
import com.lovelycatv.ark.test.sqlite.entities.AccessLog;

import java.util.Random;
import java.util.UUID;

public class SQLiteTest {
    private static final MyLocalDatabase database = MyLocalDatabase.getInstance();

    public static void main(String[] args) {
        database.accessLogDAO().clearTable();
        database.accessLogDAO().resetAI();
        sqlite_insert();
        printAllLogs();
        System.out.println("Insert completed!");
        sqlite_update();
        printAllLogs();
        System.out.println("Update completed!");
        sqlite_delete(10);
        printAllLogs();
        System.out.println("Delete completed!");
    }

    public static void printAllLogs() {
        for (AccessLog accessLog : database.accessLogDAO().getAllAccessLogs()) {
            System.out.println(accessLog.getId() + " : " + accessLog.getAccessName() + " : " + accessLog.getAccessTimestamp());
        }

    }

    public static void sqlite_update() {
        AccessLog log = new AccessLog();
        log.setId(new Random().nextInt(5) + 1);
        log.setAccessName("newAccessName");
        log.setAccessTimestamp(123456789);
        database.accessLogDAO().update(log);
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
