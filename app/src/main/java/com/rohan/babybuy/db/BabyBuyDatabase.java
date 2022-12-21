package com.rohan.babybuy.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class,Product.class,ProductionLocation.class},version = 1)
public abstract class BabyBuyDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();
    public abstract ProductDao getProductDao();
}
