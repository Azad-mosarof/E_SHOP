package com.example.e_shop.activities.shopLandingPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SLPFactory(val storeId : String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SLPViewModel(storeId) as T
    }
}