package com.lovelycatv.ark.compiler.pre.relational.verify.parameter;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.object.SupportedParameter;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.object.JavaSupportedType;

import java.util.HashMap;
import java.util.Map;

public class SQLiteSupportedParameterManager extends SupportedParameterManager {
    public SQLiteSupportedParameterManager() {
        super(DataBaseType.MYSQL, "AUTOINCREMENT");
    }


    @Override
    public void initSupportedJavaTypeList() {
        Map<Class<?>, String> pre = new HashMap<>();
        pre.put(String.class, "TEXT");
        pre.put(Integer.class, "INTEGER");
        pre.put(int.class, "INTEGER");
        pre.put(Long.class, "INTEGER");
        pre.put(long.class, "INTEGER");
        pre.put(Short.class, "INTEGER");
        pre.put(short.class, "INTEGER");
        pre.put(Float.class, "INTEGER");
        pre.put(float.class, "INTEGER");
        pre.put(Double.class, "INTEGER");
        pre.put(double.class, "INTEGER");
        pre.put(Character.class, "TEXT");
        pre.put(char.class, "TEXT");
        pre.put(Byte.class, "INTEGER");
        pre.put(byte.class, "INTEGER");

        for (Map.Entry<Class<?>, String> entry : pre.entrySet()) {
            JavaSupportedType javaSupportedType = new JavaSupportedType();
            javaSupportedType.setParameterClass(entry.getKey());
            javaSupportedType.setTypeNameInDatabase(entry.getValue());
            super.getSupportedJavaTypeList().add(javaSupportedType);
        }

    }

    @Override
    public void addSupportedEntityParamTypeList(SupportedParameter... supportedParameters) {

    }

    @Override
    public void addSupportedDAOParamTypeList(SupportedParameter... supportedParameters) {

    }
}
