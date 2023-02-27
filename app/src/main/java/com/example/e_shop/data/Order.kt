package com.example.e_shop.data

import java.io.Serializable

data class Order(
    val order_id: String,
    val productId: String,
    val name: String,
    val price: Int,
    val offerPercentage: Int? = null,
    val image: String,
    val storeId: String,
    val quantity: Int,
    val userId: String,
    val userPh: String,
    val address: String,
    val deliveryTime: String,
    val deliveryCharge: Int,
    val status: String
): Serializable {
    constructor(): this("","","",0,0,"","",0,"","","","",0,"")
}