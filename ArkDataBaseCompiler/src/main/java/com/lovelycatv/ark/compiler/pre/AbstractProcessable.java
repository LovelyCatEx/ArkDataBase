package com.lovelycatv.ark.compiler.pre;

public abstract class AbstractProcessable {
    private ProcessableType processableType;

    public AbstractProcessable() {}

    public AbstractProcessable(ProcessableType processableType) {
        this.processableType = processableType;
    }

    public final ProcessableType getProcessableType() {
        return processableType;
    }

    public enum ProcessableType {
        DATABASE,
        ENTITY,
        TYPE_CONVERTER
    }
}
