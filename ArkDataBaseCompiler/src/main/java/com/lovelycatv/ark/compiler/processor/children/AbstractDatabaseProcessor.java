package com.lovelycatv.ark.compiler.processor.children;

import com.lovelycatv.ark.compiler.exceptions.PreProcessException;
import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.pre.ProcessableDatabase;
import com.lovelycatv.ark.compiler.pre.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.processor.ArkDatabaseProcessor;

import javax.lang.model.element.Element;
import java.util.List;

public abstract class AbstractDatabaseProcessor {
    private boolean isDebugging;
    protected ArkDatabaseProcessor processor;
    protected SupportedParameterManager supportedParameterManager;
    protected ProcessableDatabase processableDatabase;
    protected final DaoProcessor daoProcessor;

    public AbstractDatabaseProcessor(ArkDatabaseProcessor processor) {
        this.processor = processor;
        this.daoProcessor = new DaoProcessor(this);
    }

    public abstract void analysis(Element annotatedElement) throws PreProcessUnexpectedError, PreProcessException;

    protected abstract List<ProcessableEntity> analysisEntities(Element annotatedElement) throws PreProcessUnexpectedError;

    protected abstract List<ProcessableTypeConverter> analysisTypeConverters(Element annotatedElement, SupportedParameterManager supportedParameterManager) throws PreProcessUnexpectedError, PreProcessException;

    public ArkDatabaseProcessor getProcessor() {
        return processor;
    }

    public DaoProcessor getDaoProcessor() {
        return daoProcessor;
    }

    public ProcessableDatabase getProcessableDatabase() {
        return processableDatabase;
    }

    public void setDebugging(boolean debugging) {
        isDebugging = debugging;
    }

    public boolean isDebugging() {
        return isDebugging;
    }
}
