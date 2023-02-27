package com.example.e_shop.data

data class Review(
    val star: Int,
    val title: String,
    val review: String,
    val productId: String,
    val userId: String,
    val customerName: String,
    val place: String,
    val verifiedPurchase: Boolean,
    val time: String,
    val likes: Int,
    val disLikes: Int,
    val images: List<String>
) {
    constructor(): this(0,"", "", "","","", "", false,time="",  likes = 0, disLikes = 0, listOf())
}