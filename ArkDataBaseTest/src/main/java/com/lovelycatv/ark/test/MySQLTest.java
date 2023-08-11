package com.lovelycatv.ark.test;


import com.lovelycatv.ark.test.mysql.MyDatabase;
import com.lovelycatv.ark.test.mysql.entites.User;
import com.lovelycatv.ark.test.mysql.enums.UserSex;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class MySQLTest {
    private static final MyDatabase database = MyDatabase.getInstance();

    public static void main(String[] args) {
        database.userDAO().clearTable();
        mysql_insert();
        mysql_printAllUsers();
        System.out.println("Insert completed!");
        mysql_update();
        mysql_printAllUsers();
        System.out.println("Update completed!");
        mysql_delete(10);
        mysql_printAllUsers();
        System.out.println("Delete completed!");
    }

    public static void mysql_printAllUsers() {
        User[] allUsers = database.userDAO().getAllUsers();
        for (User user : allUsers) {
            System.out.println(user.getId() + " : " + user.getUsername() + " : " + user.getPassword() + " : " + user.getBirthday());
        }
    }

    public static void mysql_update() {
        User user = new User();
        user.setId(new Random().nextInt(5) + 1);
        user.setUsername("newUserName");
        user.setPassword("newPassword!!");
        user.setBirthday("newBirthDay!!!");
        user.setSex(UserSex.UNKNOWN);
        database.userDAO().update(user);
    }

    public static void mysql_delete(int userId) {
        User user = new User();
        user.setId(userId);
        database.userDAO().delete(user);
    }

    public static void mysql_insert() {
        User user = new User();
        for (int i = 0; i < 10; i++) {
            user.setSex(Math.random() >= 0.5 ? UserSex.MALE : UserSex.FEMALE);
            user.setUsername(UUID.randomUUID().toString());
            user.setPassword(UUID.randomUUID().toString());
            user.setBirthday(new Date().toString());
            database.userDAO().insert(user);
        }
    }
}
