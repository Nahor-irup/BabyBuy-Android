package com.rohan.babybuy.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Query("select * from user")
    List<User> getAllUser();

    @Insert
    void insertUser(User user);

    @Query("select * from user where email=:email and password=:password Limit 1")
    User getUser(String email, String password);

    @Query("select * from user where email=:email Limit 1")
    User userExist(String email);

}
