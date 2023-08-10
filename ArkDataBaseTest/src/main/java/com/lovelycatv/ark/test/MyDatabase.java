package com.lovelycatv.ark.test;

import com.lovelycatv.ark.common.annotations.ArkDebug;
import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.runtime.ArkDatabase;
import com.lovelycatv.ark.test.dao.UserDAO;
import com.lovelycatv.ark.test.entites.User;

@ArkDebug(enabled = false)
@Database(dataBaseType = DataBaseType.MYSQL, entities = {User.class}, typeConverters = {UserTypeConverters.class}, version = 1)
public abstract class MyDatabase extends ArkDatabase {
    public abstract UserDAO userDAO();
}
