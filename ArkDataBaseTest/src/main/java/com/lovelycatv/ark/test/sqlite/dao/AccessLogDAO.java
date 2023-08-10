package com.lovelycatv.ark.test.sqlite.dao;

import com.lovelycatv.ark.common.annotations.Dao;
import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Query;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.test.sqlite.entities.AccessLog;

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
}
