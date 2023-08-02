package com.lovelycatv.ark.runtime.constructures.base.relational.interfaces;

import java.sql.Statement;

public interface RelationalExecuteCallBack {
    void executed(Statement statement);
}
