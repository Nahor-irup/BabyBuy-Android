package com.rohan.babybuy.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey()
    @NonNull
    public String id;

    @ColumnInfo(name="username")
    @NonNull
    public String username;

    @ColumnInfo(name = "email")
    @NonNull
    public String email;

    @ColumnInfo(name = "password")
    @NonNull
    public String password;

    @ColumnInfo(name = "images")
    @Nullable
    public String images;

    @ColumnInfo(name = "contact")
    @Nullable
    public String contact;

    @ColumnInfo(name = "key")
    public String key;

    public User() {
    }

    public User(@NonNull String id, @NonNull String username, @NonNull String email, @NonNull String password, @Nullable String images, @Nullable String contact) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.images = images;
        this.contact = contact;
    }

    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}
}
