package com.pizzzeria.panelpizzzeria.repository.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pizzzeria.panelpizzzeria.repository.db.entity.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int
}