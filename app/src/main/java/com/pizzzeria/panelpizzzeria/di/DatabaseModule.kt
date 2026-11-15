package com.pizzzeria.panelpizzzeria.di

import android.content.Context
import androidx.room.Room
import com.pizzzeria.panelpizzzeria.repository.db.AppDatabase
import com.pizzzeria.panelpizzzeria.repository.db.dao.CategoryDao
import com.pizzzeria.panelpizzzeria.repository.db.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pizzzeria_database"
        ).build()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }
}
