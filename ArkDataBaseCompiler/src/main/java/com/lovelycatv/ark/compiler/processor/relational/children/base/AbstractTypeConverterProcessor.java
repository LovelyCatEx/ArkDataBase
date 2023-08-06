package com.lovelycatv.ark.compiler.processor.relational.children.base;

import com.lovelycatv.ark.compiler.ProcessorVars;
import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.List;

public abstract class AbstractTypeConverterProcessor extends AbstractProcessor {
    private final AbstractDatabaseProcessor databaseProcessor;

    public AbstractTypeConverterProcessor(AbstractDatabaseProcessor databaseProcessor) {
        this.databaseProcessor = databaseProcessor;
    }

    public TypeSpec.Builder start() throws ProcessorUnexpectedError {
        this.determineSupportedParametersManager();

        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(ProcessorVars.getTypeConverterClassname(
                this.databaseProcessor.getProcessableDatabase().getClassElement().getSimpleName().toString()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (MethodSpec.Builder method : buildStaticProtectedMethods()) {
            typeSpec.addMethod(method.build());
        }

        return typeSpec;
    }

    public abstract List<MethodSpec.Builder> buildStaticProtectedMethods();

    public AbstractDatabaseProcessor getDatabaseProcessor() {
        return databaseProcessor;
    }

    @Override
    public void determineSupportedParametersManager() throws ProcessorUnexpectedError {
        super.setSupportedParameterManager(this.getDatabaseProcessor().getSupportedParameterManager());
    }
}
