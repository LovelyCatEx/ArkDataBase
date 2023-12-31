package com.lovelycatv.ark.compiler.pre.relational;

public abstract class AbstractProcessable {
    private ProcessableType processableType;

    public AbstractProcessable(ProcessableType processableType) {
        this.processableType = processableType;
    }

    public final ProcessableType getProcessableType() {
        return processableType;
    }

    public enum ProcessableType {
        DATABASE,
        ENTITY,
        TYPE_CONVERTER,
        DAO
    }
}
