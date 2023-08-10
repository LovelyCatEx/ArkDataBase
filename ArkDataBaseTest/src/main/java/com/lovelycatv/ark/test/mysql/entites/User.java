package com.lovelycatv.ark.test.mysql.entites;

import com.lovelycatv.ark.common.annotations.Column;
import com.lovelycatv.ark.common.annotations.Entity;
import com.lovelycatv.ark.test.mysql.enums.UserSex;

@Entity(tableName = "users")
public class User {
    @Column(columnName = "id", primaryKey = true, autoIncrease = true)
    private int id;
    @Column(columnName = "username")
    private String username;
    @Column(columnName = "password")
    private String password;
    @Column(columnName = "sex")
    private UserSex sex;
    @Column(columnName = "birthday")
    private String birthday;

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getBirthday() {
        return birthday;
    }

    public UserSex getSex() {
        return sex;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setSex(UserSex sex) {
        this.sex = sex;
    }
}


