package com.lovelycatv.ark.compiler.simulation;

import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.simulation.dao.UserDAO;
import com.lovelycatv.ark.compiler.simulation.entites.User;
import com.lovelycatv.ark.runtime.ArkDatabase;

@Database(dataBaseType = DataBaseType.MYSQL, entities = {User.class}, typeConverters = {})
public abstract class MyDatabase extends ArkDatabase {
    public abstract UserDAO userDAO();
}
