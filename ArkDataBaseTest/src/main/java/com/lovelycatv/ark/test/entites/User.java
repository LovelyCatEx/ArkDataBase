package com.lovelycatv.ark.test.entites;

import com.lovelycatv.ark.common.annotations.Column;
import com.lovelycatv.ark.common.annotations.Entity;

@Entity(tableName = "users")
public class User {
    @Column(columnName = "username")
    private String username;
    @Column(columnName = "password")
    private String password;
}
