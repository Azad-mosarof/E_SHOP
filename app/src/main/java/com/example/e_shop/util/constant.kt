package com.example.e_shop.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.e_shop.R
import com.example.e_shop.firebase.FirebaseService
import com.example.e_shop.viewModels.FavouriteProductsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ActivityContext

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

    fun toolBarSetup(title:String,context: AppCompatActivity, bgColor: String = "#FFFFFF"){
        val backIcon: ImageView = context.findViewById(R.id.toolBarBackIcon)
        val toolTitle: TextView = context.findViewById(R.id.tootlBarTitle)
        val toolbar: ConstraintLayout = context.findViewById(R.id.toolBar)

        toolTitle.text = title

        toolbar.setBackgroundColor(Color.parseColor(bgColor))
        toolTitle.setTextColor(Color.parseColor("#FFFFFF"))
        backIcon.setColorFilter(Color.parseColor("#FFFFFF"))

        //give padding in the toolbar
        toolbar.setPadding(0,10, 0, 10)


        backIcon.setOnClickListener{
            context.finish()
            context.overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }
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