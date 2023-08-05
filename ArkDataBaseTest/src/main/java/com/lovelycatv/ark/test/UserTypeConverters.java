package com.lovelycatv.ark.test;

import com.lovelycatv.ark.common.annotations.TypeConverter;

public class UserTypeConverters {
    @TypeConverter
    public static String a(int[] a) {
        return "";
    }

    @TypeConverter
    public static int[] ab(String a) {
        return new int[]{};
    }
}
