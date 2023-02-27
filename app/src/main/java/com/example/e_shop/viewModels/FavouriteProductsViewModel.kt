package com.example.e_shop.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_shop.data.Product
import com.example.e_shop.data.productId
import com.example.e_shop.util.Resource
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavouriteProductsViewModel: ViewModel() {

    private val _favouriteProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val favouriteProducts = _favouriteProducts.asStateFlow()

    init {
        fetchFavouriteProducts()
    }

    fun fetchFavouriteProducts(){
        viewModelScope.launch {
            _favouriteProducts.emit(Resource.Loading())
        }
        fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid)
            .collection(constant.FavouriteProductsCollections).get()
            .addOnSuccessListener { it ->
                val productsId = it.toObjects(productId::class.java)
                val products = arrayListOf<Product>()
                Log.i("length", productsId.toString())
                var x = 0
                for(id in productsId){
                    fireStore.collection(constant.ProductsCollections).document(id.id).get()
                        .addOnSuccessListener {
                            it.toObject(Product::class.java)?.let { it1 -> products.add(it1) }
                        }
                        .addOnFailureListener{
                            //
                        }
                    x+=1
                }
                if(x == productsId.size){
                    viewModelScope.launch {
                        _favouriteProducts.emit(Resource.Success(products))
                    }
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _favouriteProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}