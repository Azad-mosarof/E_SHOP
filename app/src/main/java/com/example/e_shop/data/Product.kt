package com.example.e_shop.data

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>,
    val storeId: String,
    val stock: Int
){
    constructor(): this("0","","",0f,images= emptyList(), storeId = "", stock = 0)
}
