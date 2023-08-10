package com.lovelycatv.ark.test.mysql.enums;

public enum UserSex {
    UNKNOWN(-1),
    MALE(0),
    FEMALE(1);

    public int id;

    UserSex(int id) {
        this.id = id;
    }

    public static UserSex getSexById(int id) {
        for (UserSex value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
