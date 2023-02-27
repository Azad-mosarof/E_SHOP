package com.example.e_shop.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_shop.data.Category
import com.example.e_shop.data.Product
import com.example.e_shop.util.Resource
import com.example.e_shop.util.constant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val fireStore: FirebaseFirestore,
    private val category: Category,
): ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()


    init {
        fetchOfferProduct()
        fetchBestProduct()
    }

    fun fetchOfferProduct(){
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }
        fireStore.collection(constant.ProductsCollections).whereEqualTo("category", category.category)
            .whereNotEqualTo("offerPercentage", null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                Log.i("product", products.toString())
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProduct(){
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        fireStore.collection(constant.ProductsCollections).whereEqualTo("category", category.category)
            .whereNotEqualTo("offerPercentage", null).limit(10).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}
