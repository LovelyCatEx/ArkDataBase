package com.lovelycatv.ark.compiler.processor.relational.children.base;

import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDAO;
import com.lovelycatv.ark.compiler.processor.relational.objects.EntityAdapterInfo;

import java.util.List;

public abstract class AbstractDAOProcessor extends AbstractProcessor {
    private final AbstractDatabaseProcessor databaseProcessor;

    public AbstractDAOProcessor(AbstractDatabaseProcessor databaseProcessor) {
        this.databaseProcessor = databaseProcessor;
        determineSupportedParametersManager();
    }

    public abstract void start() throws ProcessorError, ProcessorUnexpectedError;

    public abstract void buildDAO(ProcessableDAO processableDAO) throws ProcessorError;

    public abstract void verifyDAO(ProcessableDAO processableDAO) throws ProcessorError;

    public abstract List<EntityAdapterInfo> scanAllUsedAdapters(ProcessableDAO processableDAO) throws ProcessorError;

    public final AbstractDatabaseProcessor getDatabaseProcessor() {
        return databaseProcessor;
    }

    @Override
    public void determineSupportedParametersManager() {
        super.setSupportedParameterManager(super.getSupportedParameterManager());
    }
}
