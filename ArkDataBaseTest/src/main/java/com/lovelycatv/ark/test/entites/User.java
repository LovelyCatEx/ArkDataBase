package com.lovelycatv.ark.test.entites;

import com.lovelycatv.ark.common.annotations.Column;
import com.lovelycatv.ark.common.annotations.Entity;
import com.lovelycatv.ark.common.annotations.Ignore;

import java.util.LinkedList;
import java.util.List;

@Entity(tableName = "users")
public class User {
    @Column(columnName = "username")
    private int[] username;
    @Column(columnName = "password")
    private String password;
    @Column(columnName = "abc")
    private String abc;
}
