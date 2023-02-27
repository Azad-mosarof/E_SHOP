package com.example.e_shop.data

import java.io.Serializable

data class CartProduct(
    val id: String,
    val name: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val images: String,
    val quantity: Int,
    val star: Float,
    val store_id: String,
    val store_owner_id: String
): Serializable{
    constructor(): this("0","",0f,0f,images= "", 0, 0f, "", "")
}