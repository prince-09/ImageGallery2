package com.example.atgtask3.RoomDatabase;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.atgtask3.User.User;

import java.util.List;

@Dao
public interface PhotoDao {

    @Query("SELECT*FROM user")
    List<User> getAllUsers();

    @Insert
    void insert(User user);

}
