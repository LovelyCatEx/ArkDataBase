package com.lovelycatv.ark.test;

import com.lovelycatv.ark.common.annotations.TypeConverter;

import java.util.Arrays;

public class UserTypeConverters {
    @TypeConverter
    public static String a(int[] a) {
        return Arrays.toString(a);
    }

    @TypeConverter
    public static int[] ab(String a) {
        return new int[]{};
    }
}
