package com.rohan.babybuy.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "product")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int productId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name="description")
    public String description;

    @ColumnInfo(name = "image")
    public String images;

    @ColumnInfo(name = "location")
    public int plId;
}
