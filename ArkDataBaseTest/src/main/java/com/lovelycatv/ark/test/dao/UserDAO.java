package com.lovelycatv.ark.test.dao;

import com.lovelycatv.ark.common.annotations.Dao;
import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.test.entites.User;

@Dao
public interface UserDAO {
    @Insert
    void insert(User... user);

    @Delete
    void delete(User... user);

    @Update
    void update(User... user);
}
