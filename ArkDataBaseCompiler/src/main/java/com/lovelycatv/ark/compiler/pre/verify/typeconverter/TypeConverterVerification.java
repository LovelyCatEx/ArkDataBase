package com.lovelycatv.ark.compiler.pre.verify.typeconverter;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.PreProcessException;
import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.pre.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.verify.AbstractProcessableVerification;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.utils.APTools;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeConverterVerification extends AbstractProcessableVerification<Iterable<ProcessableTypeConverter>> {

    public TypeConverterVerification(DataBaseType dataBaseType, SupportedParameterManager supportedParameterManager, Iterable<ProcessableTypeConverter> processableObject) {
        super(dataBaseType, supportedParameterManager, processableObject);
    }

    @Override
    public void verify() throws PreProcessException, PreProcessUnexpectedError {
        Iterable<ProcessableTypeConverter> processableTypeConverters = getProcessableObject();

        final Map<TypeMirror, List<TypeMirror>> fromTo = new HashMap<>();
        final Map<TypeMirror, List<TypeMirror>> toFrom = new HashMap<>();

        // Check whether the return type and parameter type is valid
        for (ProcessableTypeConverter processableTypeConverter : processableTypeConverters) {
            for (ProcessableTypeConverter.Converter converter : processableTypeConverter.getTypeConverterList()) {
                if (converter.isConvertOut()) {
                    if (!fromTo.containsKey(converter.getFrom())) {
                        fromTo.put(converter.getFrom(), new ArrayList<>());
                    }
                    final List<TypeMirror> tmp = fromTo.get(converter.getFrom());
                    tmp.add(converter.getTo());
                    fromTo.put(converter.getFrom(), tmp);
                } else {
                    if (!toFrom.containsKey(converter.getFrom())) {
                        toFrom.put(converter.getFrom(), new ArrayList<>());
                    }
                    final List<TypeMirror> tmp = toFrom.get(converter.getFrom());
                    tmp.add(converter.getTo());
                    toFrom.put(converter.getFrom(), tmp);
                }

                if (APTools.isVoid(converter.getTo())) {
                    throw new PreProcessException(String.format("The return type of TypeConverter %s() in %s must be not void",
                            converter.getElement().getSimpleName(), processableTypeConverter.getTypeConverterType().asElement().asType().toString()));
                }

                if (converter.getFrom() == null) {
                    throw new PreProcessException(String.format("The TypeConverter %s() in %s must have only exactly one parameter",
                            converter.getElement().getSimpleName(), processableTypeConverter.getTypeConverterType().asElement().asType().toString()));
                }
            }
        }

        // Check whether the typeConverter has duplicate out
        for (Map.Entry<TypeMirror, List<TypeMirror>> entry : fromTo.entrySet()) {
            if (entry.getValue() == null || entry.getValue().size() == 0) {
                throw new PreProcessUnexpectedError(String.format("An unexpected error occurred while verifying typeConverter %s(), could not find any return type",
                        entry.getKey().toString()));
            }

            if (entry.getValue().size() > 1) {
                throw new PreProcessException(String.format("The type %s has multiple returns, make sure that it has only one return type",
                        entry.getKey().toString()));
            }
        }

        // Check whether the typeConverter has in and out methods
        for (Map.Entry<TypeMirror, List<TypeMirror>> entry : fromTo.entrySet()) {
            TypeMirror from = entry.getKey();
            TypeMirror to = entry.getValue().get(0);
            boolean couldTurnBack = toFrom.containsKey(to) && toFrom.get(to).contains(from);
            if (!couldTurnBack) {
                throw new PreProcessException(String.format("Type %s does not have any method that could transform data from database to it!", from.toString()));
            }
        }
    }
}
