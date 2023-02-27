package com.example.e_shop.fragments.Product_Information.informationViewmodels

import androidx.lifecycle.*
import com.example.e_shop.data.Product
import com.example.e_shop.util.Resource
import com.example.e_shop.util.constant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class ProductDescriptionViewModel(
    private val productId: String,
): ViewModel() {
    class ViewModelFactory(private val productId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductDescriptionViewModel(productId) as T
        }
    }

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _clickedProducts = MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val clickedProduct: MutableStateFlow<Resource<Product>> = _clickedProducts

    var totalImages = mutableListOf<String>()

    init {
        fetchClickedProduct()
    }

    fun fetchClickedProduct(){
        viewModelScope.launch {
            _clickedProducts.emit(Resource.Loading())
        }

        fireStore.collection(constant.ProductsCollections).document(productId)
            .get().addOnSuccessListener {result ->
                val product = result.toObject(Product::class.java)!!
                totalImages = product.images as MutableList<String>
                viewModelScope.launch {
                    _clickedProducts.emit(Resource.Success(product))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _clickedProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}
