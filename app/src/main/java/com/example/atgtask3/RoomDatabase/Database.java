package com.example.atgtask3.RoomDatabase;

import androidx.room.RoomDatabase;

import com.example.atgtask3.User.User;


@androidx.room.Database(entities = {User.class},version = 1,exportSchema = false)
public abstract class Database extends RoomDatabase {
        public abstract PhotoDao userDao();

}
