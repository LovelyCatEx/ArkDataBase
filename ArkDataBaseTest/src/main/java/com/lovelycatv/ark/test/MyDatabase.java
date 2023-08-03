package com.lovelycatv.ark.test;

import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.runtime.ArkDatabase;
import com.lovelycatv.ark.test.dao.UserDAO;
import com.lovelycatv.ark.test.entites.User;
import com.lovelycatv.ark.test.entites.Work;

@Database(dataBaseType = DataBaseType.MYSQL, entities = {User.class, Work.class}, version = 1)
public abstract class MyDatabase extends ArkDatabase {
    public abstract UserDAO userDAO();
}
