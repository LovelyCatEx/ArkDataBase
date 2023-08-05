package com.lovelycatv.ark.compiler.utils;

import java.util.regex.Pattern;

public class StringX {

    public static String getJavaBeanMethodName(String fieldName, boolean getterOrSetter, boolean isBoolean, boolean noPrefix) {
        String prefix = isBoolean ? "is" : (getterOrSetter ? "get" : "set");
        if (noPrefix) {
            prefix = "";
        }
        char char0 = fieldName.charAt(0);
        if (isAlphabetic(char0) && Character.isLowerCase(char0)) {
            if (fieldName.length() > 1) {
                char char1 = fieldName.charAt(1);
                if ((isAlphabetic(char1) && Character.isLowerCase(char1)) || !isAlphabetic(char1)) {
                    return prefix + String.valueOf(char0).toUpperCase() + fieldName.substring(1);
                } else {
                    return prefix + fieldName;
                }
            } else {
                return prefix + String.valueOf(char0).toUpperCase();
            }
        } else {
            return prefix + fieldName;
        }
    }

    public static boolean isAlphabetic(char str) {
        return String.valueOf(str).matches("^[a-zA-Z].*");
    }

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


    public static boolean isInteger(String str){
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }
        return str.matches("[a-zA-Z]+");
    }

    public  static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);

    }

}
