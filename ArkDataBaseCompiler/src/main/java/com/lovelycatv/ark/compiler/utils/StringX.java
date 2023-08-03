package com.lovelycatv.ark.compiler.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringX {

    /**
     * @param classNameWithPackagePath cn.lovelycat.catroom.utils.StringX
     * @return StringX
     */
    public static String getClassNameInFullName(String classNameWithPackagePath) {
        if (classNameWithPackagePath == null || "".equals(classNameWithPackagePath)) {
            return "";
        }
        if (!classNameWithPackagePath.contains(".")) {
            return classNameWithPackagePath;
        }
        String[] sp = classNameWithPackagePath.split("\\.");
        return sp[sp.length - 1];
    }

    /**
     * @param classNameWithPackagePath cn.lovelycat.catroom.utils.StringX
     * @return cn.lovelycat.catroom.utils
     */
    public static String getClassPathInFullName(String classNameWithPackagePath) {
        if (classNameWithPackagePath == null || "".equals(classNameWithPackagePath)) {
            return "";
        }
        if (!classNameWithPackagePath.contains(".")) {
            return classNameWithPackagePath;
        }
        String[] sp = classNameWithPackagePath.split("\\.");
        return classNameWithPackagePath.replace("." + sp[sp.length -1], "");
    }

    /**
     * @param classNameWithPackagePath ()cn.lovelycat.cat(room.utils.St)ringX
     * @return cn.lovelycat.catroom.utils.StringX
     */
    public static String clearIllegalCharInClassPath(String classNameWithPackagePath) {
        return classNameWithPackagePath.replace("(","")
                .replace(")","");
    }

}
