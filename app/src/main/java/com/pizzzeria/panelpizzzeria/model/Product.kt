package com.pizzzeria.panelpizzzeria.model

data class Product(
    val productId: String,
    val menuCategoryId: String,
    val productName: String,
    val photoUrl: String?,
    val price: Double,
    val groupModifications: List<ModificationGroup> = emptyList()
)

data class ModificationGroup(
    val dishModificationGroupId: Int,
    val name: String,
    val numMin: Int = 0,
    val numMax: Int = 1,
    val modifications: List<ModificationItem> = emptyList()
)

data class ModificationItem(
    val dishModificationId: Int,
    val name: String,
    val price: Double
)
