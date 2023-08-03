package com.lovelycatv.ark.test;

import com.lovelycatv.ark.common.annotations.TypeConverter;

public class UserTypeConverters {
    @TypeConverter
    public String a(int[] a) {
        return "";
    }

    @TypeConverter
    public int[] ab(String a) {
        return new int[]{};
    }
}
