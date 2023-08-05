package com.lovelycatv.ark.compiler.pre.relational.verify.parameter;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.object.JavaSupportedType;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.object.SupportedParameter;
import com.lovelycatv.ark.compiler.utils.APTools;

import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public abstract class SupportedParameterManager {
    private final DataBaseType dataBaseType;
    protected final List<JavaSupportedType> supportedJavaTypeList = new ArrayList<>();
    protected final List<SupportedParameter> supportedEntityParamTypeList = new ArrayList<>();
    protected final List<SupportedParameter> supportedDAOParamTypeList = new ArrayList<>();

    public final String AUTO_INCREMENT;

    public SupportedParameterManager(DataBaseType dataBaseType, String AUTO_INCREMENT) {
        this.dataBaseType = dataBaseType;
        this.AUTO_INCREMENT = AUTO_INCREMENT;

        initSupportedJavaTypeList();
    }

    public abstract void initSupportedJavaTypeList();

    public abstract void addSupportedEntityParamTypeList(SupportedParameter... supportedParameters);

    public abstract void addSupportedDAOParamTypeList(SupportedParameter... supportedParameters);

    public boolean isSupportedInJavaTypes(TypeMirror javaTypeClass) {
        return isSupportedInJavaTypes(APTools.getClassNameFromTypeMirror(javaTypeClass));
    }

    public boolean isSupportedInJavaTypes(Class<?> javaTypeClass) {
        for (JavaSupportedType type : getSupportedJavaTypeList()) {
            if (type.getParameterClass().isAssignableFrom(javaTypeClass)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSupportedInJavaTypes(String javaTypeClassName) {
        for (JavaSupportedType type : getSupportedJavaTypeList()) {
            if (type.getParameterClass().getName().equals(javaTypeClassName)) {
                return true;
            }
        }
        return false;
    }

    public JavaSupportedType getSupportedInJavaTypes(TypeMirror javaTypeClass) {
        for (JavaSupportedType type : getSupportedJavaTypeList()) {
            if (type.getParameterClass().getName().equals(APTools.getClassNameFromTypeMirror(javaTypeClass))) {
                return type;
            }
        }
        return null;
    }

    public final List<JavaSupportedType> getSupportedJavaTypeList() {
        return supportedJavaTypeList;
    }

    public final List<SupportedParameter> getSupportedDAOParamTypeList() {
        return supportedDAOParamTypeList;
    }

    public final List<SupportedParameter> getSupportedEntityParamTypeList() {
        return supportedEntityParamTypeList;
    }

    public final DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public static boolean isJavaNumberTypes(TypeMirror typeMirror) {
        for (Class<?> type : getJavaNumberTypes()) {
            if (type.getName().equals(APTools.getClassNameFromTypeMirror(typeMirror))) {
                return true;
            }
        }
        return false;
    }

    public static List<Class<?>> getJavaNumberTypes() {
        List<Class<?>> pre = new ArrayList<>();
        pre.add(String.class);
        pre.add(Integer.class);
        pre.add(int.class);
        pre.add(Long.class);
        pre.add(long.class);
        pre.add(Short.class);
        pre.add(short.class);
        pre.add(Float.class);
        pre.add(float.class);
        pre.add(Double.class);
        pre.add(double.class);
        pre.add(Character.class);
        pre.add(char.class);
        pre.add(Byte.class);
        pre.add(byte.class);
        return pre;
    }
}
