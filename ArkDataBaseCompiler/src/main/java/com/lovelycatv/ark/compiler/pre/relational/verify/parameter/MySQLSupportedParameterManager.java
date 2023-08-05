package com.lovelycatv.ark.compiler.pre.relational.verify.parameter;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.object.JavaSupportedType;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.object.SupportedParameter;

import java.util.HashMap;
import java.util.Map;

public class MySQLSupportedParameterManager extends SupportedParameterManager {
    public MySQLSupportedParameterManager() {
        super(DataBaseType.MYSQL, "AUTO_INCREMENT");
    }


    @Override
    public void initSupportedJavaTypeList() {
        Map<Class<?>, String> pre = new HashMap<>();
        pre.put(String.class, "TEXT");
        pre.put(Integer.class, "INT");
        pre.put(int.class, "INT");
        pre.put(Long.class, "BIGINT");
        pre.put(long.class, "BIGINT");
        pre.put(Short.class, "SMALLINT");
        pre.put(short.class, "SMALLINT");
        pre.put(Float.class, "FLOAT");
        pre.put(float.class, "FLOAT");
        pre.put(Double.class, "DOUBLE");
        pre.put(double.class, "DOUBLE");
        pre.put(Character.class, "TEXT");
        pre.put(char.class, "TEXT");
        pre.put(Byte.class, "TINYINT");
        pre.put(byte.class, "TINYINT");

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
