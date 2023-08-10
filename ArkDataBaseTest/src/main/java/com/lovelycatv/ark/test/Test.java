package com.lovelycatv.ark.test;


import com.lovelycatv.ark.Ark;
import com.lovelycatv.ark.test.entites.User;

import java.util.UUID;

public class Test {


    public static void main(String[] args) {
        MyDatabase database = Ark.getRelationalDatabaseBuilder()
                .mysql("192.168.2.102", 3306, "catroom", "catroom", "catroom")
                .createDatabase(MyDatabase.class);
        /*User uj = new User();
        for (int i = 0; i < 10; i++) {
            uj.setUsername(new int[]{(int) (Math.random() * 10), (int) (Math.random() * 10), (int) (Math.random() * 10)});
            uj.setPassword(UUID.randomUUID().toString());
            database.userDAO().insert(uj);
        }*/

        for (User user : database.userDAO().getAllUsers()) {
            System.out.println(user.getPassword());
        }

    }
}
