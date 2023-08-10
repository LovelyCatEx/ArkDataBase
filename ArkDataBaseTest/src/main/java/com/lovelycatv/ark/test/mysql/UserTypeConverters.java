package com.lovelycatv.ark.test.mysql;

import com.lovelycatv.ark.common.annotations.TypeConverter;
import com.lovelycatv.ark.test.mysql.enums.UserSex;

public class UserTypeConverters {
    @TypeConverter
    public static int userSex2Int(UserSex userSex) {
        return userSex.id;
    }

    @TypeConverter
    public static UserSex int2UserSex(int sexId) {
        return UserSex.getSexById(sexId);
    }
}
