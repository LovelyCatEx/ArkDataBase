package com.lovelycatv.ark.compiler.objects;

import com.lovelycatv.ark.common.enums.DataBaseType;

import java.util.ArrayList;
import java.util.List;

public final class ProcessableDatabase {
    private final DataBaseType dataBaseType;
    private final int version;
    private final List<ProcessableEntity> processableEntityList = new ArrayList<>();
    private final List<ProcessableTypeConverter> processableTypeConverterList = new ArrayList<>();

    public ProcessableDatabase(DataBaseType dataBaseType, int version) {
        this.dataBaseType = dataBaseType;
        this.version = version;
    }

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public int getVersion() {
        return version;
    }

    public List<ProcessableEntity> getProcessableEntityList() {
        return processableEntityList;
    }

    public List<ProcessableTypeConverter> getProcessableTypeConverterList() {
        return processableTypeConverterList;
    }
}
