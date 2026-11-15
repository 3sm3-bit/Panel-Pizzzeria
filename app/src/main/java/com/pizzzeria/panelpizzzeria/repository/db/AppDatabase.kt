package com.pizzzeria.panelpizzzeria.repository.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pizzzeria.panelpizzzeria.repository.db.dao.CategoryDao
import com.pizzzeria.panelpizzzeria.repository.db.entity.CategoryEntity
import com.pizzzeria.panelpizzzeria.repository.db.dao.ProductDao
import com.pizzzeria.panelpizzzeria.repository.db.entity.ProductEntity

@Database(entities = [CategoryEntity::class, ProductEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pizzzeria_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
