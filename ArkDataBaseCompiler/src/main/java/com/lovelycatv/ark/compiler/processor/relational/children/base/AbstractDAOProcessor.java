package com.lovelycatv.ark.compiler.processor.relational.children.base;

import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDAO;
import com.lovelycatv.ark.compiler.processor.relational.objects.EntityAdapterInfo;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

public abstract class AbstractDAOProcessor extends AbstractProcessor {
    private final AbstractDatabaseProcessor databaseProcessor;

    public AbstractDAOProcessor(AbstractDatabaseProcessor databaseProcessor) {
        this.databaseProcessor = databaseProcessor;
    }

    public abstract List<TypeSpec.Builder> start() throws ProcessorError, ProcessorUnexpectedError;

    public abstract TypeSpec.Builder buildDAO(ProcessableDAO processableDAO) throws ProcessorError;

    public abstract void verifyDAO(ProcessableDAO processableDAO) throws ProcessorError;

    public abstract List<EntityAdapterInfo> scanAllUsedAdapters(ProcessableDAO processableDAO) throws ProcessorError;

    public final AbstractDatabaseProcessor getDatabaseProcessor() {
        return databaseProcessor;
    }

}
