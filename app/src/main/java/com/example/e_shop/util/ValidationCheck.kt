package com.example.e_shop.util

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation {
    if(email.isEmpty()) {
        return RegisterValidation.Failed("Email Can not be empty")
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return RegisterValidation.Failed("Wrong Email Format")
    }

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {
    if(password.isEmpty()){
        return RegisterValidation.Failed("Password can not be empty")
    }
    if(password.length < 6)
        return RegisterValidation.Failed("Password must be contain at least 6 character")

    return RegisterValidation.Success
}
