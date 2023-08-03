package com.lovelycatv.ark.compiler.pre.relational.sql;

import java.util.HashMap;
import java.util.Map;

public class StandardSQLStatement {
    private String sql;
    private final Map<Integer, String> slotWithColumnName = new HashMap<>();

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<Integer, String> getSlotWithColumnName() {
        return slotWithColumnName;
    }

    public String getSql() {
        return sql;
    }
}
