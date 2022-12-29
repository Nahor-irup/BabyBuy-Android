package com.rohan.babybuy.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "product")
public class Product implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "image")
    public String images;

    @ColumnInfo(name = "latitude")
    public Double latitude;

    @ColumnInfo(name = "longitude")
    public Double longitude;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "isPurchased")
    public Boolean isPurchased;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "key")
    public String key;


    public Product() {
    }

    public Product(String title, String description, String images, Double latitude, Double longitude, String address, Boolean isPurchased, String date) {
        this.title = title;
        this.description = description;
        this.images = images;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.isPurchased = isPurchased;
        this.date = date;
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


    public boolean isPurchased() {
        return isPurchased;
    }

    public String getKey() {return key;}

    public double getLatitude() {return latitude;}

    public double getLongitude() {return longitude;}

    public String getAddress() {return address;}

    public String getDate() {return date;}

    public void setKey(String key) {
        this.key = key;
    }
}
