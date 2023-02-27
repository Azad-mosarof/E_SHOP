package com.example.e_shop.Digital_Store.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_shop.Digital_Store.Viewmodels.NewOrdersViewModel

class NewOrdersFactory(val storeId: String): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewOrdersViewModel(storeId) as T
    }
}