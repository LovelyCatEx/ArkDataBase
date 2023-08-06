package com.lovelycatv.ark.compiler.processor.relational.children;

import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.processor.relational.children.base.AbstractDatabaseProcessor;
import com.lovelycatv.ark.compiler.processor.relational.children.base.AbstractTypeConverterProcessor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class TypeConverterProcessor extends AbstractTypeConverterProcessor {
    public TypeConverterProcessor(AbstractDatabaseProcessor databaseProcessor) {
        super(databaseProcessor);
    }

    @Override
    public List<MethodSpec.Builder> buildStaticProtectedMethods() {
        List<MethodSpec.Builder> result = new ArrayList<>();
        for (ProcessableTypeConverter typeConverter : super.getDatabaseProcessor().getProcessableDatabase().getTypeConverterController().getTypeConverterList()) {
            for (ProcessableTypeConverter.Converter converter : typeConverter.getTypeConverterList()) {
                MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(converter.getMethodNameInDAO())
                        .addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                        .addParameter(ClassName.get(converter.getFrom()), "_from")
                        .returns(ClassName.get(converter.getTo()))
                        .addStatement("return $T.$L($L)", typeConverter.getTypeConverterType(), converter.getElement().getSimpleName(), "_from");
                result.add(methodSpec);
            }
        }
        return result;
    }

    @Override
    protected void debugging() {

    }
}
