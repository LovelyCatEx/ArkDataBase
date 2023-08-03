package com.lovelycatv.ark.runtime.constructures.adapters;

import com.lovelycatv.ark.runtime.ArkDatabase;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class EntityInsertAdapter<T> extends BaseEntityAdapter {

    public EntityInsertAdapter(ArkRelationalDatabase<? extends RelationalDatabase> dataBase) {
        super(dataBase);
    }
    public abstract void bind(PreparedStatement preparedStatement, T entity);

    public final void insert(T entity) {
        try {
            PreparedStatement preparedStatement = getStatement();
            bind(preparedStatement, entity);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public final void insert(T... entities) {
        for (T t : entities) {
            insert(t);
        }
    }

    public final void insert(Iterable<? extends T> entities) {
        for (T t : entities) {
            insert(t);
        }
    }
}
