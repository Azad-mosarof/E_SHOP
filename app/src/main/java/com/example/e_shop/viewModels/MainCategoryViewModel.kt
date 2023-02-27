package com.example.e_shop.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_shop.data.Product
import com.example.e_shop.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore
): ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pageInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDealsProducts()
        fetchBestProducts()
    }

    fun fetchSpecialProducts(){
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        fireStore.collection("Products").whereEqualTo("category","Special Products")
            .get().addOnSuccessListener {result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDealsProducts(){
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }
        fireStore.collection("Products").whereEqualTo("category","Best Deals")
            .get().addOnSuccessListener {result ->
                val bestDealsProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsProductsList))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts(){
        if(!pageInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            fireStore.collection("Products").limit(pageInfo.bestProductPage * 10)
                .whereEqualTo("category", "Best Products")
                .get().addOnSuccessListener { result ->
                    val bestProductsList = result.toObjects(Product::class.java)
                    pageInfo.isPagingEnd = bestProducts == pageInfo.oldBestProducts
                    pageInfo.oldBestProducts = bestProductsList
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProductsList))
                    }
                    pageInfo.bestProductPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}

internal data class PagingInfo(
    var bestProductPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)