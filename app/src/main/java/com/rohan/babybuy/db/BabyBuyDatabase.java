package com.rohan.babybuy.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class},version = 1)
public abstract class BabyBuyDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();
}
