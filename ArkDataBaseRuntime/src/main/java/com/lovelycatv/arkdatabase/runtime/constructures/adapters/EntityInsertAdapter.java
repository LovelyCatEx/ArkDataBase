package com.lovelycatv.arkdatabase.runtime.constructures.adapters;

import com.lovelycatv.arkdatabase.runtime.ArkDatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class EntityInsertAdapter<T> extends BaseEntityAdapter {

    public EntityInsertAdapter(ArkDatabase dataBase) {
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
