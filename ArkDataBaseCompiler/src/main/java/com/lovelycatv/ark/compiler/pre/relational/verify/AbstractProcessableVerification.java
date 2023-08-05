package com.lovelycatv.ark.compiler.pre.relational.verify;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.ProcessorException;
import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;

public abstract class AbstractProcessableVerification<T> {
    private final DataBaseType dataBaseType;
    private final SupportedParameterManager supportedParameterManager;
    private final T processableObject;

    public AbstractProcessableVerification(DataBaseType dataBaseType, SupportedParameterManager supportedParameterManager, T processableObject) {
        this.dataBaseType = dataBaseType;
        this.supportedParameterManager = supportedParameterManager;
        this.processableObject = processableObject;
    }

    public abstract void verify() throws ProcessorException, ProcessorUnexpectedError;

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
