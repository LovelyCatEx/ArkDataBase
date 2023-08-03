package com.lovelycatv.ark.compiler.pre.verify.parameter.object;

public class JavaSupportedType extends SupportedParameter {
    private Class<?> parameterClass;

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    public void setParameterClass(Class<?> parameterClass) {
        this.parameterClass = parameterClass;
    }
}
