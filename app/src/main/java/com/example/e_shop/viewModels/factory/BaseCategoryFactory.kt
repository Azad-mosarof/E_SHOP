package com.example.e_shop.viewModels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_shop.data.Category
import com.example.e_shop.viewModels.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryFactory(
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firestore, category) as T
    }
}