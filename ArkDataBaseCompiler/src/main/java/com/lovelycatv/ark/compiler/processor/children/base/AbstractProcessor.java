package com.lovelycatv.ark.compiler.processor.children.base;

import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SupportedParameterManager;

public abstract class AbstractProcessor {
    private SupportedParameterManager supportedParameterManager;
    private boolean isDebugging;
    protected abstract void debugging();

    public abstract void determineSupportedParametersManager() throws PreProcessUnexpectedError;

    public SupportedParameterManager getSupportedParameterManager() {
        return supportedParameterManager;
    }

    public void setSupportedParameterManager(SupportedParameterManager supportedParameterManager) {
        this.supportedParameterManager = supportedParameterManager;
    }

    public final void setDebugging(boolean debugging) {
        isDebugging = debugging;
    }

    public final boolean isDebugging() {
        return isDebugging;
    }
}
