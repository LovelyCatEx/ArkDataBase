package com.lovelycatv.ark.test.sqlite.entities;

import com.lovelycatv.ark.common.annotations.Column;
import com.lovelycatv.ark.common.annotations.Entity;

@Entity(tableName = "accessLogs")
public class AccessLog {
    @Column(columnName = "id", primaryKey = true, autoIncrease = true)
    private int id;
    private String accessName;
    private long accessTimestamp;

    public void setId(int id) {
        this.id = id;
    }

    public void setAccessName(String accessName) {
        this.accessName = accessName;
    }

    public void setAccessTimestamp(long accessTimestamp) {
        this.accessTimestamp = accessTimestamp;
    }

    public int getId() {
        return id;
    }

    public long getAccessTimestamp() {
        return accessTimestamp;
    }

    public String getAccessName() {
        return accessName;
    }
}
