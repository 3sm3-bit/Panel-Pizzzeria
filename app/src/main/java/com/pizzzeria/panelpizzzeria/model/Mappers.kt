package com.pizzzeria.panelpizzzeria.model

import com.pizzzeria.panelpizzzeria.repository.db.entity.CategoryEntity
import com.pizzzeria.panelpizzzeria.repository.db.entity.ProductEntity
import com.pizzzeria.panelpizzzeria.repository.network.domain.CategoryItemResponse
import com.pizzzeria.panelpizzzeria.repository.network.domain.ModificationGroupResponse
import com.pizzzeria.panelpizzzeria.repository.network.domain.ModificationItemResponse
import com.pizzzeria.panelpizzzeria.repository.network.domain.ProductItemResponse

fun CategoryItemResponse.toEntity(): CategoryEntity? {
    val id = categoryId ?: return null
    return CategoryEntity(
        categoryId = id,
        categoryName = categoryName ?: "Sin nombre"
    )
}

fun ProductItemResponse.toEntity(): ProductEntity? {
    val id = productId ?: return null
    val name = productName ?: "Sin nombre"
    if (name.contains("orilla", ignoreCase = true)) {
        return null
    }
    return ProductEntity(
        productId = id,
        menuCategoryId = menuCategoryId ?: "",
        productName = name,
        photo = photo,
        photoOrigin = photoOrigin,
        price = price,
        groupModifications = groupModifications
    )
}

fun CategoryEntity.toDomain(): Category {
    val mappedName = when (categoryId) {
        "4" -> "BEBIDAS C"
        "11" -> "BEBIDAS G"
        "8" -> "GRANDE"
        "9" -> "MEDIANO"
        "10" -> "CHICO"
        else -> categoryName ?: "Sin nombre"
    }

    return Category(
        categoryId = categoryId,
        categoryName = categoryName ?: "Sin nombre",
        namePanel = mappedName
    )
}

fun ProductEntity.toDomain(): Product {
    val rawPriceStr = price?.values?.firstOrNull() ?: "0"
    val rawPriceDouble = rawPriceStr.toDoubleOrNull() ?: 0.0
    val finalPrice = rawPriceDouble / 100.0

    return Product(
        productId = productId,
        menuCategoryId = menuCategoryId ?: "",
        productName = productName ?: "Sin nombre",
        photoUrl = photo?.let { "https://joinposter.com$it" },
        price = finalPrice,
        groupModifications = groupModifications?.map { it.toDomain() } ?: emptyList()
    )
}

fun ModificationGroupResponse.toDomain(): ModificationGroup {
    return ModificationGroup(
        dishModificationGroupId = dishModificationGroupId ?: 0,
        name = name ?: "",
        numMin = numMin ?: 0,
        numMax = numMax ?: 1,
        modifications = modifications?.map { it.toDomain() } ?: emptyList()
    )
}

fun ModificationItemResponse.toDomain(): ModificationItem {
    val rawPrice = price ?: 0.0
    val finalPrice = if (rawPrice >= 100) rawPrice / 100.0 else rawPrice
    return ModificationItem(
        dishModificationId = dishModificationId ?: 0,
        name = name ?: "",
        price = finalPrice
    )
}
