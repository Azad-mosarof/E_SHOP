package com.example.e_shop.data

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val Phone: String = "",
    val imgPath: String = "",
    val haveStore: Boolean = false,
    val storeId: String = "",
    val address: String = "",
    val reg_token: String = ""
) {
    constructor(): this("", "", "", "", imgPath ="", haveStore = false, storeId = "", address = "", reg_token="")
}