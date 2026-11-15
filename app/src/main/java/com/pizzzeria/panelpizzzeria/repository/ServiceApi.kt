package com.pizzzeria.panelpizzzeria.repository

import com.pizzzeria.panelpizzzeria.repository.network.domain.CategoryResponse
import com.pizzzeria.panelpizzzeria.repository.network.domain.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ServiceApi {

    @GET("menu.getCategories")
    suspend fun getCategories(@Query("token") token: String): Response<CategoryResponse>

    @GET("menu.getProducts")
    suspend fun getProducts(@Query("token") token: String): Response<ProductResponse>

    @POST("security")
    suspend fun loadProduct(): Response<Unit>
}
