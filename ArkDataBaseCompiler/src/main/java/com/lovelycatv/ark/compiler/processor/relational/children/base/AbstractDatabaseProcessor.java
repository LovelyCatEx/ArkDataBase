package com.lovelycatv.ark.compiler.processor.relational.children.base;

import com.lovelycatv.ark.compiler.exceptions.ProcessorException;
import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDatabase;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.processor.ArkDatabaseProcessor;
import com.lovelycatv.ark.compiler.processor.relational.children.DAOProcessor;

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

    public abstract void analysis(Element annotatedElement) throws ProcessorUnexpectedError, ProcessorException, ProcessorError;

    protected abstract List<ProcessableDAO> analysisDAO(Element annotatedElement) throws ProcessorUnexpectedError;
    protected abstract List<ProcessableEntity> analysisEntities(Element annotatedElement) throws ProcessorUnexpectedError;

    protected abstract List<ProcessableTypeConverter> analysisTypeConverters(Element annotatedElement, SupportedParameterManager supportedParameterManager) throws ProcessorUnexpectedError, ProcessorException;

    protected abstract void verifyProcessableObjects() throws ProcessorUnexpectedError, ProcessorException;

    protected abstract void startDAOProcessor() throws ProcessorError, ProcessorUnexpectedError;

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
