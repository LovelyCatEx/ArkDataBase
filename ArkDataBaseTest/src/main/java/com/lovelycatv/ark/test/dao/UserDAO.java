package com.lovelycatv.ark.test.dao;

import com.lovelycatv.ark.common.annotations.Dao;
import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Query;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.test.entites.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Dao
public interface UserDAO {
    @Insert
    void insert(User... user);

    @Delete
    void delete(User... user);

    @Update
    void update(User... user);

    @Query(sql = "SELECT * FROM `users` WHERE `id` = :id")
    User getUserById(int id);

    @Query(sql = "SELECT * FROM `users`")
    User[] getAllUsers();

}
