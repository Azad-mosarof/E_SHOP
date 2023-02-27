package com.example.e_shop.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.e_shop.data.Category
import com.example.e_shop.util.fireStore
import com.example.e_shop.viewModels.CategoryViewModel
import com.example.e_shop.viewModels.factory.BaseCategoryFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

class FoodCategory: BaseCategory() {

//    @Inject
//    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val viewmodel by viewModels<CategoryViewModel> {
        BaseCategoryFactory(fireStore, Category.Food)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewmodel.offerProducts.collectLatest {
                when(it){
                    is com.example.e_shop.util.Resource.Loading -> {

                    }
                    is com.example.e_shop.util.Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)
                    }
                    is com.example.e_shop.util.Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.bestProducts.collectLatest {
                when(it){
                    is com.example.e_shop.util.Resource.Loading -> {

                    }
                    is com.example.e_shop.util.Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                    }
                    is com.example.e_shop.util.Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                    }
                    else -> Unit
                }
            }
        }
    }
}