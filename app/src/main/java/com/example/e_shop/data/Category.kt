package com.example.e_shop.data

sealed class Category(var category: String) {
    object Accessory: Category("Accessories")
    object Clothes: Category("Clothes")
    object Food: Category("Food")
    object Stationary: Category("Stationary")
}