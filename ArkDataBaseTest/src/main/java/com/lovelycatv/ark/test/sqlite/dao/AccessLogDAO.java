package com.lovelycatv.ark.test.sqlite.dao;

import com.lovelycatv.ark.common.annotations.Dao;
import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Query;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.test.sqlite.entities.AccessLog;

import java.util.LinkedList;
import java.util.List;

@Dao
public interface AccessLogDAO {
    @Insert
    void insert(AccessLog... accessLogs);

    @Update
    void update(AccessLog... accessLogs);

    @Delete
    void delete(AccessLog... accessLogs);

    @Query(sql = "SELECT * FROM `accessLogs` WHERE `timestamp` WHERE `id` = :id")
    List<AccessLog> getAccessLogsById(int id);

    @Query(sql = "SELECT * FROM `accessLogs`")
    LinkedList<AccessLog> getAllAccessLogs();

    @Query(sql = "DELETE FROM `accessLogs`", executeOnly = true)
    void clearTable();

    @Query(sql = "UPDATE sqlite_sequence SET seq = 0 WHERE name='accessLogs'", executeOnly = true)
    void resetAI();
}
