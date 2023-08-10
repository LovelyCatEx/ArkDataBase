package com.lovelycatv.ark.test.mysql;

import com.lovelycatv.ark.Ark;
import com.lovelycatv.ark.common.annotations.ArkDebug;
import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.runtime.ArkDatabase;
import com.lovelycatv.ark.test.mysql.dao.UserDAO;
import com.lovelycatv.ark.test.mysql.entites.User;

@ArkDebug(enabled = false)
@Database(dataBaseType = DataBaseType.MYSQL, entities = {User.class}, typeConverters = {UserTypeConverters.class}, version = 1)
public abstract class MyDatabase extends ArkDatabase {
    private static MyDatabase INSTANCE;
    public abstract UserDAO userDAO();

    public static synchronized MyDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = Ark.getRelationalDatabaseBuilder()
                    .mysql("192.168.2.102",3306,"ark","ark","ark")
                    .createDatabase(MyDatabase.class);
        }
        return INSTANCE;
    }
}
