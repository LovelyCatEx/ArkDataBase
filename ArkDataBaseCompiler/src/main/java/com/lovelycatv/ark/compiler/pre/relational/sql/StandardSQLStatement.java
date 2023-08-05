package com.lovelycatv.ark.compiler.pre.relational.sql;

import java.util.HashMap;
import java.util.Map;

/**
 * With BaseSQLStatement, this class could indicate columns that each ? placeholder corresponding
 */
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
