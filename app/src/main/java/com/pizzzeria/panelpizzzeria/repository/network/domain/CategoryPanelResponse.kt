package com.pizzzeria.panelpizzzeria.repository.network.domain

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("response")
    val response: List<CategoryItemResponse>?
)

data class CategoryItemResponse(
    @SerializedName("category_id")
    val categoryId: String?,
    @SerializedName("category_name")
    val categoryName: String?
)
