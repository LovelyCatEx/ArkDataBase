package com.lovelycatv.ark.compiler.pre;

import com.lovelycatv.ark.common.enums.DataBaseType;

import java.util.ArrayList;
import java.util.List;

public final class ProcessableDatabase extends AbstractProcessable {
    private final DataBaseType dataBaseType;
    private final int version;
    private final ProcessableEntity.Controller processableEntityController = new ProcessableEntity.Controller();
    private final ProcessableTypeConverter.Controller processableTypeConverterController = new ProcessableTypeConverter.Controller();

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

    public ProcessableEntity.Controller getProcessableEntityController() {
        return processableEntityController;
    }

    public ProcessableTypeConverter.Controller getProcessableTypeConverterController() {
        return processableTypeConverterController;
    }
}
