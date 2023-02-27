package com.example.e_shop.data

data class Notification(
    val title: String,
    val message: String,
    val imgUrl: String,
    val arrivalTime: String
) {
    constructor(): this("", "", "", "")
}
