package com.lovelycatv.ark.compiler.pre.relational;

import com.lovelycatv.ark.common.enums.DataBaseType;

public final class ProcessableDatabase extends AbstractProcessable {
    private final DataBaseType dataBaseType;
    private final int version;
    private final ProcessableEntity.Controller entityController = new ProcessableEntity.Controller();
    private final ProcessableTypeConverter.Controller typeConverterController = new ProcessableTypeConverter.Controller();
    private final ProcessableDAO.Controller daoController = new ProcessableDAO.Controller();

    public ProcessableDatabase(DataBaseType dataBaseType, int version) {
        super(ProcessableType.DATABASE);
        this.dataBaseType = dataBaseType;
        this.version = version;
    }

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public int getVersion() {
        return version;
    }

    public final ProcessableDAO.Controller getDaoController() {
        return daoController;
    }

    public ProcessableEntity.Controller getEntityController() {
        return entityController;
    }

    public ProcessableTypeConverter.Controller getTypeConverterController() {
        return typeConverterController;
    }
}
