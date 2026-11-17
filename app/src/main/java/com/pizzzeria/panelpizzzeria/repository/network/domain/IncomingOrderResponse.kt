package com.pizzzeria.panelpizzzeria.repository.network.domain

import com.google.gson.annotations.SerializedName

data class IncomingOrderResponse(
    @SerializedName("response")
    val response: IncomingOrderData?
)

data class IncomingOrderData(
    @SerializedName("incoming_order_id")
    val incomingOrderId: Long?
)
