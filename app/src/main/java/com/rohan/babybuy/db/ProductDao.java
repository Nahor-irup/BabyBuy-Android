package com.rohan.babybuy.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("select * from product")
    List<Product> getAllProduct();

    @Insert
    void insertProduct(Product product);

    @Query("select * from product where productId=:productId Limit 1")
    Product getProduct(Integer productId);

    @Query("select * from product where title=:title Limit 1")
    Product productExist(String title);

}
