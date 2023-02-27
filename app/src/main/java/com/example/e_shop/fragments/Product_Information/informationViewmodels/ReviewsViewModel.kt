package com.example.e_shop.fragments.Product_Information.informationViewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_shop.data.Review
import com.example.e_shop.util.Resource
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import com.example.e_shop.util.storageRef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ReviewsViewModel(private val productId: String) : ViewModel() {

    private val _userReviews = MutableStateFlow<Resource<List<Review>>>(Resource.Unspecified())
    var userReviews = _userReviews
    private val _reviewImages = MutableStateFlow<Resource<List<String>>>(Resource.Unspecified())
    var reviewImages = _reviewImages
    val pId = productId
    var listOfRatings = listOf<Int>()
    var _reviews = 0

    init {
        fetchReviews()
        fetchReviewImages()
    }

    private fun fetchReviews(){
        viewModelScope.launch {
            _userReviews.emit(Resource.Loading())
        }
        fireStore.collection(constant.ProductsCollections).document(productId).collection(constant.Reviews).get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    val reviews = it.toObjects(Review::class.java)
                    _userReviews.emit(Resource.Success(reviews))
                    var r1=0
                    var r2=0
                    var r3=0
                    var r4=0
                    var r5 = 0
                    for(review in reviews){
                        when(review.star){
                            1 -> r1+=1
                            2 -> r2+=1
                            3 -> r3+=1
                            4 -> r4+=1
                            5 -> r5+=1
                        }
                        if(review.review != "" && review.title != ""){
                            _reviews += 1
                        }
                    }
                    listOfRatings = listOf(r5,r4,r3,r2,r1)
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _userReviews.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun fetchReviewImages(){
        viewModelScope.launch {
            _reviewImages.emit(Resource.Loading())
        }
        storageRef.child("Products/images/reviewImg/$productId").listAll()
            .addOnSuccessListener {
                viewModelScope.launch {
                    val items = it.items
                    val listOfUrl = mutableListOf<String>()
                    for (item in items) {
                        item.downloadUrl.addOnSuccessListener {
                            // Do something with the image URL here
                            listOfUrl.add(it.toString())
                        }
                    }
                    _reviewImages.emit(Resource.Success(listOfUrl))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _reviewImages.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}