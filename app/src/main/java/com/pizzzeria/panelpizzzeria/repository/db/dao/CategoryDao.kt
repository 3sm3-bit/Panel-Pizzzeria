package com.pizzzeria.panelpizzzeria.repository.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pizzzeria.panelpizzzeria.repository.db.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun clearAllCategories()

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int
}