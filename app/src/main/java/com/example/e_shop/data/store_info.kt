package com.example.e_shop.data


data class store_info(
    val s_ID: String,
    val s_name: String,
    val s_o_name: String,
    val s_category: String,
    val s_email: String,
    val s_location: String,
    val customer_service: String,
    val s_o_uid: String,
    val new_orders: Int
) {
    constructor() : this("","","","","", "","","", 0)
}