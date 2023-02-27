package com.example.e_shop.util

import android.annotation.SuppressLint
import android.view.View
import android.widget.ProgressBar
import com.example.e_shop.firebase.FirebaseService
import com.example.e_shop.viewModels.FavouriteProductsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Utility{
    fun newPrice(oldPrice:Float,offer:Float): Int {
        return  (oldPrice - (oldPrice * offer)/100).toInt()
    }

    fun showLoading(progressBar: ProgressBar){
        progressBar.visibility = View.VISIBLE
    }

    fun hideLoading(progressBar: ProgressBar){
        progressBar.visibility = View.GONE
    }
}

object constant {
    const val UserCollection = "Users"
    const val StoresCollection
    = "Stores"
    const val ProductsCollections = "Products"
    const val FavouriteProductsCollections = "FavProducts"
    const val Notifications = "Notifications"
    const val Reviews = "Reviews"
    const val Cart = "Cart"
    const val Orders = "Orders"
}

const val login_reg: Int = 100
const val logReg_reg: Int = 101
var url: String = ""
@SuppressLint("StaticFieldLeak")
var fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
var auth: FirebaseAuth = FirebaseAuth.getInstance()
var bigProductViewUrl: String = ""
var userProfileCreateState: Boolean = false
val storageRef = FirebaseStorage.getInstance().reference
var buyOverCart = false