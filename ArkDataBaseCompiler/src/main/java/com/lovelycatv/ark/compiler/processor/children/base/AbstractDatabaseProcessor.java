package com.lovelycatv.ark.compiler.processor.children.base;

import com.lovelycatv.ark.compiler.exceptions.PreProcessException;
import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.ProcessableDatabase;
import com.lovelycatv.ark.compiler.pre.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.processor.ArkDatabaseProcessor;
import com.lovelycatv.ark.compiler.processor.children.DAOProcessor;

import javax.lang.model.element.Element;
import java.util.List;

public abstract class AbstractDatabaseProcessor extends AbstractProcessor {
    private final ArkDatabaseProcessor processor;
    protected ProcessableDatabase processableDatabase;
    private final DAOProcessor daoProcessor;

    public AbstractDatabaseProcessor(ArkDatabaseProcessor processor) {
        this.processor = processor;
        this.daoProcessor = new DAOProcessor(this);
    }

    public abstract void analysis(Element annotatedElement) throws PreProcessUnexpectedError, PreProcessException, ProcessorError;

    protected abstract List<ProcessableDAO> analysisDAO(Element annotatedElement) throws PreProcessUnexpectedError;
    protected abstract List<ProcessableEntity> analysisEntities(Element annotatedElement) throws PreProcessUnexpectedError;

    protected abstract List<ProcessableTypeConverter> analysisTypeConverters(Element annotatedElement, SupportedParameterManager supportedParameterManager) throws PreProcessUnexpectedError, PreProcessException;

    protected abstract void verifyProcessableObjects() throws PreProcessUnexpectedError, PreProcessException;

    protected abstract void startDAOProcessor() throws ProcessorError;

    public final ArkDatabaseProcessor getProcessor() {
        return processor;
    }

    protected final DAOProcessor getDaoProcessor() {
        return daoProcessor;
    }

    public final ProcessableDatabase getProcessableDatabase() {
        return processableDatabase;
    }

}