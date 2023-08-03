package com.lovelycatv.ark.compiler.pre;

import com.lovelycatv.ark.common.annotations.TypeConverter;
import com.lovelycatv.ark.compiler.exceptions.PreProcessException;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.utils.APTools;
import lombok.Data;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public final class ProcessableTypeConverter extends AbstractProcessable {
    private DeclaredType typeConverterType;
    private final List<Converter> typeConverterList = new ArrayList<>();

    public ProcessableTypeConverter() {
        super(ProcessableType.TYPE_CONVERTER);
    }

    public static ProcessableTypeConverter builder(DeclaredType typeConverterType, SupportedParameterManager supportedParameterManager) throws PreProcessException {
        ProcessableTypeConverter result = new ProcessableTypeConverter();
        result.setTypeConverterType(typeConverterType);

        for (Element enclosedElement : typeConverterType.asElement().getEnclosedElements()) {
            if (enclosedElement.getKind() != ElementKind.METHOD || enclosedElement.getAnnotation(TypeConverter.class) == null) {
                continue;
            }
            final ExecutableElement i = (ExecutableElement) enclosedElement;
            final Converter converter = new Converter();

            converter.setElement(i);

            converter.setTo(i.getReturnType());

            final List<? extends VariableElement> parameters = i.getParameters();

            if (parameters == null || parameters.size() != 1) {
                converter.setFrom(null);
            } else {
                converter.setFrom(parameters.get(0).asType());
            }

            converter.setConvertOut(supportedParameterManager.isSupportedInJavaTypes(converter.getTo()));

            result.getTypeConverterList().add(converter);
        }

        return result;
    }

    public List<Converter> getOutConverters() {
        List<Converter> result = new ArrayList<>();
        for (Converter converter : getTypeConverterList()) {
            if (converter.isConvertOut()) {
                result.add(converter);
            }
        }
        return result;
    }

    public List<Converter> getInConverters() {
        List<Converter> result = new ArrayList<>();
        for (Converter converter : getTypeConverterList()) {
            if (!converter.isConvertOut()) {
                result.add(converter);
            }
        }
        return result;
    }

    public void setTypeConverterType(DeclaredType typeConverterType) {
        this.typeConverterType = typeConverterType;
    }

    public DeclaredType getTypeConverterType() {
        return typeConverterType;
    }

    public List<Converter> getTypeConverterList() {
        return typeConverterList;
    }

    @Data
    public static class Converter {
        private ExecutableElement element;

        private TypeMirror from;

        private TypeMirror to;

        private boolean isConvertOut;
    }

    @Data
    public static class Controller {
        private final List<ProcessableTypeConverter> typeConverterList = new ArrayList<>();

        public Converter getConverterIfExists(TypeMirror typeMirror) {
            for (ProcessableTypeConverter typeConverter : getTypeConverterList()) {
                for (Converter outConverter : typeConverter.getOutConverters()) {
                    if (APTools.isTheSameTypeMirror(typeMirror, outConverter.getFrom())) {
                        return outConverter;
                    }
                }
            }
            return null;
        }
    }
}
