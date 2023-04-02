package com.example.e_shop.activities.shopLandingPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_shop.data.Product
import com.example.e_shop.util.Resource
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SLPViewModel(val storeId: String): ViewModel() {

    private val _SLPProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val SLPProducts: StateFlow<Resource<List<Product>>> = _SLPProducts

    init {
        fetchSLPProducts()
    }

    private fun fetchSLPProducts() {
        viewModelScope.launch {
            _SLPProducts.emit(Resource.Loading())
        }
        fireStore.collection(constant.ProductsCollections).whereEqualTo("storeId", storeId)
            .get().addOnSuccessListener {result ->
                val storeProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _SLPProducts.emit(Resource.Success(storeProductsList))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _SLPProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}