package com.pizzzeria.panelpizzzeria.repository.network

import com.pizzzeria.panelpizzzeria.repository.network.domain.CategoryResponse
import com.pizzzeria.panelpizzzeria.repository.network.domain.IncomingOrderResponse
import com.pizzzeria.panelpizzzeria.repository.network.domain.ProductResponse
import com.pizzzeria.panelpizzzeria.repository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class DataNetwork @Inject constructor(
    private val serviceApi: ServiceApi
) {
    suspend fun getCategories(token: String): Response<CategoryResponse> {
        return serviceApi.getCategories(token)
    }

    suspend fun getProducts(token: String): Response<ProductResponse> {
        return serviceApi.getProducts(token)
    }

    suspend fun createIncomingOrder(token: String, fields: Map<String, String>): Response<IncomingOrderResponse> {
        return serviceApi.createIncomingOrder(token, fields)
    }
}
