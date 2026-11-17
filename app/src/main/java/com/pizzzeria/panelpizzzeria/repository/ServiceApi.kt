package com.pizzzeria.panelpizzzeria.repository

import com.pizzzeria.panelpizzzeria.repository.network.domain.CategoryResponse
import com.pizzzeria.panelpizzzeria.repository.network.domain.IncomingOrderResponse
import com.pizzzeria.panelpizzzeria.repository.network.domain.ProductResponse
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ServiceApi {

    @GET("menu.getCategories")
    suspend fun getCategories(
        @Query("token") token: String,
        @Query("spot_id") spotId: String? = null
    ): Response<CategoryResponse>

    @GET("menu.getProducts")
    suspend fun getProducts(
        @Query("token") token: String,
        @Query("spot_id") spotId: String? = null
    ): Response<ProductResponse>

    @FormUrlEncoded
    @POST("incomingOrders.createIncomingOrder")
    suspend fun createIncomingOrder(
        @Query("token") token: String,
        @FieldMap fields: Map<String, String>
    ): Response<IncomingOrderResponse>

    @POST("security")
    suspend fun loadProduct(): Response<Unit>
}
