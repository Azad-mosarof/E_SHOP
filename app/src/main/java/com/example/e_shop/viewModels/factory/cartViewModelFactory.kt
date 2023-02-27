package com.example.e_shop.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_shop.viewModels.CartViewModel

class cartViewModelFactory(
    private val productId: String?
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CartViewModel(productId!!) as T
    }
}