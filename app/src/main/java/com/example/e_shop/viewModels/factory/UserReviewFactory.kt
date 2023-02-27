package com.example.e_shop.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_shop.fragments.Product_Information.informationViewmodels.ReviewsViewModel

class UserReviewFactory(private val productId: String):
    ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReviewsViewModel(productId) as T
        }
}