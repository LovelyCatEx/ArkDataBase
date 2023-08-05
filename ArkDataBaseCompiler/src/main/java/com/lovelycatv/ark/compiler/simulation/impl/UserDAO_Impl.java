package com.lovelycatv.ark.compiler.simulation.impl;

import com.lovelycatv.ark.compiler.simulation.dao.UserDAO;
import com.lovelycatv.ark.compiler.simulation.entites.User;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;

public class UserDAO_Impl implements UserDAO {
    private final ArkRelationalDatabase<? extends RelationalDatabase> __db;

    public UserDAO_Impl(ArkRelationalDatabase<? extends RelationalDatabase> db) {
        this.__db = db;
    }

    @Override
    public void insert(User... user) {

    }

    @Override
    public void delete(User... user) {

    }

    @Override
    public void update(User... user) {

    }
}
