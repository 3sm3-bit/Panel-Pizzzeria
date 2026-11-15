package com.pizzzeria.panelpizzzeria.repository.network.domain

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("response")
    val response: List<ProductItemResponse>?
)

data class ProductItemResponse(
    @SerializedName("menu_category_id")
    val menuCategoryId: String?,
    @SerializedName("product_id")
    val productId: String?,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("photo_origin")
    val photoOrigin: String?,
    @SerializedName("price")
    val price: Map<String, String>?,
    @SerializedName("group_modifications")
    val groupModifications: List<ModificationGroupResponse>?
)

data class ModificationGroupResponse(
    @SerializedName("dish_modification_group_id")
    val dishModificationGroupId: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("num_min")
    val numMin: Int? = null,
    @SerializedName("num_max")
    val numMax: Int? = null,
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("modifications")
    val modifications: List<ModificationItemResponse>?
)

data class ModificationItemResponse(
    @SerializedName("dish_modification_id")
    val dishModificationId: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("price")
    val price: Double?
)
