package com.example.e_shop.util

import com.example.e_shop.fragments.RegisterFragment

sealed class RegisterValidation(){
    object Success: RegisterValidation()
    data class Failed(val message: String): RegisterValidation()
}

data class RegisterFailedState(
    val email: RegisterValidation,
    val password: RegisterValidation
)
