package com.lovelycatv.ark.runtime;

public abstract class ArkDatabase {
    private BaseArkDatabase baseArkDatabase;

    public BaseArkDatabase getDatabase() {
        return this.baseArkDatabase;
    }

    protected void setBaseArkDatabase(BaseArkDatabase baseArkDatabase) {
        this.baseArkDatabase = baseArkDatabase;
    }

    public abstract void initDatabase();
}
