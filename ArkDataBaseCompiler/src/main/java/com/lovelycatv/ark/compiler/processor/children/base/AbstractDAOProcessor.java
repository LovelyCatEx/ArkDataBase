package com.lovelycatv.ark.compiler.processor.children.base;

import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SupportedParameterManager;

public abstract class AbstractDAOProcessor extends AbstractProcessor {
    private final AbstractDatabaseProcessor databaseProcessor;

    public AbstractDAOProcessor(AbstractDatabaseProcessor databaseProcessor) {
        this.databaseProcessor = databaseProcessor;
        determineSupportedParametersManager();
    }

    public abstract void start() throws ProcessorError;

    public abstract void buildDAO(ProcessableDAO processableDAO) throws ProcessorError;

    public abstract void verifyDAO(ProcessableDAO processableDAO) throws ProcessorError;

    public final AbstractDatabaseProcessor getDatabaseProcessor() {
        return databaseProcessor;
    }

    @Override
    public void determineSupportedParametersManager() {
        super.setSupportedParameterManager(super.getSupportedParameterManager());
    }
}
