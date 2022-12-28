package com.rohan.babybuy.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "product")
public class Product implements Serializable {
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

    @ColumnInfo(name = "isPurchased")
    public boolean isPurchased;

    @ColumnInfo(name = "key")
    public String key;


    public Product(){}
    public Product(String title, String description, String images, int plId, boolean isPurchased) {
        this.title = title;
        this.description = description;
        this.images = images;
        this.plId = plId;
        this.isPurchased = isPurchased;
    }

    public int getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImages() {
        return images;
    }

    public int getPlId() {
        return plId;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}
}
