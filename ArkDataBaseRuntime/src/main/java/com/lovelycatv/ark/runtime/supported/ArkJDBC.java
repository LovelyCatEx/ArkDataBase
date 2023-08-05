package com.lovelycatv.ark.runtime.supported;

import java.util.HashMap;
import java.util.Map;

public class ArkJDBC {
    public static Map<Class<?>, String> getJavaTypeClassWithBindMethodNameMap() {
        Map<Class<?>, String> result = new HashMap<>();
        result.put(String.class, "setString");
        result.put(Integer.class, "setInt");
        result.put(int.class, "setInt");
        result.put(Long.class, "setLong");
        result.put(long.class, "setLong");
        result.put(Short.class, "setShort");
        result.put(short.class, "setShort");
        result.put(Float.class, "setFloat");
        result.put(float.class, "setFloat");
        result.put(Double.class, "setDouble");
        result.put(double.class, "setDouble");
        result.put(Character.class, "setString");
        result.put(char.class, "setString");
        result.put(Byte.class, "setShort");
        result.put(byte.class, "setShort");
        return result;
    }

    public static Map<Class<?>, String> getJavaTypeClassWithResultSetMethodNameMap() {
        Map<Class<?>, String> result = new HashMap<>();
        result.put(String.class, "getString");
        result.put(Integer.class, "getInt");
        result.put(int.class, "getInt");
        result.put(Long.class, "getLong");
        result.put(long.class, "getLong");
        result.put(Short.class, "getShort");
        result.put(short.class, "getShort");
        result.put(Float.class, "getFloat");
        result.put(float.class, "getFloat");
        result.put(Double.class, "getDouble");
        result.put(double.class, "getDouble");
        result.put(Character.class, "getString");
        result.put(char.class, "getString");
        result.put(Byte.class, "getShort");
        result.put(byte.class, "getShort");
        return result;
    }
}
