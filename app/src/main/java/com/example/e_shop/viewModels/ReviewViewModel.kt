package com.example.e_shop.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_shop.data.Review
import com.example.e_shop.data.productId
import com.example.e_shop.util.Resource
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel: ViewModel() {

    private val _reviewList = MutableStateFlow<Resource<List<Review>>>(Resource.Unspecified())
    val reviewList = _reviewList

    init {
        fetchReviews()
    }

    fun fetchReviews(){
        viewModelScope.launch {
            _reviewList.emit(Resource.Loading())
        }
        fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid)
            .collection(constant.Reviews).get()
            .addOnSuccessListener { it ->
                val productIds = it.toObjects(productId::class.java)
                viewModelScope.launch {
                    _reviewList.emit(Resource.Success(getReviews(productIds)))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _reviewList.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun getReviews(productIds: List<productId>):MutableList<Review>{
        val userReviews = mutableListOf<Review>()
        for(productId in productIds){
            fireStore.collection(constant.ProductsCollections).document(productId.id).collection(constant.Reviews).whereEqualTo("userId",
                auth.currentUser?.uid).get()
                .addOnSuccessListener {
                    val reviews = it.toObjects(Review::class.java)
                    userReviews.addAll(reviews)
                }
        }
        return userReviews
    }

}