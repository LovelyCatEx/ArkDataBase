package com.lovelycatv.ark.test;


import com.lovelycatv.ark.Ark;
import com.lovelycatv.ark.mydatabase.MyDatabaseTypeConverters;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.MySQLManager;
import com.lovelycatv.ark.test.entites.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Test {


    public static void main(String[] args) throws SQLException {
        MyDatabase database = Ark.getRelationalDatabaseBuilder()
                .mysql("192.168.2.102", 3306, "catroom", "catroom", "catroom")
                .createDatabase(MyDatabase.class);
        User uj = database.userDAO().getUserById(2);
        if (uj != null) {
            System.out.println(Arrays.toString(uj.getUsername()));
            System.out.println(uj.getPassword());
        }

    }
}
