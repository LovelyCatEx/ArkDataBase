package com.lovelycatv.ark.compiler.processor.children;

import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.objects.ProcessableDatabase;
import com.lovelycatv.ark.compiler.objects.ProcessableEntity;
import com.lovelycatv.ark.compiler.objects.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.processor.ArkDatabaseProcessor;

import javax.lang.model.element.Element;
import java.util.List;

public abstract class AbstractDatabaseProcessor {
    protected ArkDatabaseProcessor processor;
    protected ProcessableDatabase processableDatabase;
    protected final DaoProcessor daoProcessor;

    public AbstractDatabaseProcessor(ArkDatabaseProcessor processor) {
        this.processor = processor;
        this.daoProcessor = new DaoProcessor(this);
    }

    public abstract void analysis(Element annotatedElement) throws PreProcessUnexpectedError;

    protected abstract List<ProcessableEntity> analysisEntities(Element annotatedElement) throws PreProcessUnexpectedError;

    protected abstract List<ProcessableTypeConverter> analysisTypeConverters(Element annotatedElement) throws PreProcessUnexpectedError;

}
