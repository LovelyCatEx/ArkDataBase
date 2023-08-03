package com.lovelycatv.ark.compiler.pre.verify;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.PreProcessException;
import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.pre.AbstractProcessable;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SupportedParameterManager;

public abstract class AbstractProcessableVerification<T> {
    private final DataBaseType dataBaseType;
    private final SupportedParameterManager supportedParameterManager;
    private final T processableObject;

    public AbstractProcessableVerification(DataBaseType dataBaseType, SupportedParameterManager supportedParameterManager, T processableObject) {
        this.dataBaseType = dataBaseType;
        this.supportedParameterManager = supportedParameterManager;
        this.processableObject = processableObject;
    }

    public abstract void verify() throws PreProcessException, PreProcessUnexpectedError;

    public final T getProcessableObject() {
        return processableObject;
    }

    public final SupportedParameterManager getSupportedParameterManager() {
        return supportedParameterManager;
    }

    public final DataBaseType getDataBaseType() {
        return dataBaseType;
    }
}
