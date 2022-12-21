package com.rohan.babybuy.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "product_location")
public class ProductionLocation {
    @PrimaryKey(autoGenerate = true)
    public int plId;

    @ColumnInfo(name = "latitude")
    public String latitude;

    @ColumnInfo(name = "longitude")
    public String longitude;
}
