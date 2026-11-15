package com.pizzzeria.panelpizzzeria.repository.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pizzzeria.panelpizzzeria.repository.network.domain.ModificationGroupResponse

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: String,
    @ColumnInfo(name = "menu_category_id")
    val menuCategoryId: String?,
    @ColumnInfo(name = "product_name")
    val productName: String?,
    @ColumnInfo(name = "photo")
    val photo: String?,
    @ColumnInfo(name = "photo_origin")
    val photoOrigin: String?,
    @ColumnInfo(name = "price")
    val price: Map<String, String>?,
    @ColumnInfo(name = "group_modifications")
    val groupModifications: List<ModificationGroupResponse>?
)
