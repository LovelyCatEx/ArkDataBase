package com.lovelycatv.ark;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.runtime.ArkDatabase;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.BaseArkDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;

public class Ark {

    public static RelationalDatabaseBuilder getRelationalDatabaseBuilder() {
        return new RelationalDatabaseBuilder();
    }

    public static class RelationalDatabaseBuilder {
        private String host;
        private int port;
        private String username;
        private String password;
        private String database;

        private Object[] toArgs() {
            return new Object[]{host, port, username, password, database};
        }

        public <T extends ArkDatabase> T createDatabase(Class<T> databaseClass) {
            String databaseName = databaseClass.getSimpleName();
            try {
                Class<T> aClass = (Class<T>) Class.forName(String.format(ArkVars.getPackageName(databaseName) + "." + databaseName + "Impl"));
                T newInstance = aClass.newInstance();
                newInstance.initDatabase();
                newInstance.getDatabase().setArgs(toArgs());
                ((ArkRelationalDatabase<? extends RelationalDatabase>) newInstance.getDatabase()).initDataBase();
                return newInstance;
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public RelationalDatabaseBuilder mysql(String host, int port, String username, String password, String database) {
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
            this.database = database;
            return this;
        }

        public RelationalDatabaseBuilder sqlite(String databasePath) {
            this.database = databasePath;
            return this;
        }

    }
}
