package com.lovelycatv.ark.test.mysql.dao;

import com.lovelycatv.ark.common.annotations.Dao;
import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Query;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.test.mysql.entites.User;

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

    @Query(sql = "SELECT * FROM `users` WHERE `sex` = :sex")
    User getUserBySex(int sex);

    @Query(sql = "SELECT * FROM `users`")
    User[] getAllUsers();

    @Query(sql = "TRUNCATE `users`", executeOnly = true)
    void clearTable();

}
