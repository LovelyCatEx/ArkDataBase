package com.lovelycatv.ark.runtime.constructures.adapters;

import com.lovelycatv.ark.runtime.ArkDatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class EntityUpdateAdapter<T> extends BaseEntityAdapter {

    public EntityUpdateAdapter(ArkDatabase dataBase) {
        super(dataBase);
    }
    public abstract void bind(PreparedStatement preparedStatement, T entity);

    public final void update(T entity) {
        try {
            PreparedStatement preparedStatement = getStatement();
            bind(preparedStatement, entity);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public final void update(T... entities) {
        for (T t : entities) {
            update(t);
        }
    }

    public final void delete(Iterable<? extends T> entities) {
        for (T t : entities) {
            update(t);
        }
    }
}
