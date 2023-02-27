package com.example.e_shop.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.engine.Resource
import com.example.e_shop.data.Category
import com.example.e_shop.util.fireStore
import com.example.e_shop.viewModels.CategoryViewModel
import com.example.e_shop.viewModels.factory.BaseCategoryFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class Accessory: BaseCategory() {

    val viewmodel by viewModels<CategoryViewModel> {
        BaseCategoryFactory(fireStore, Category.Accessory)
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