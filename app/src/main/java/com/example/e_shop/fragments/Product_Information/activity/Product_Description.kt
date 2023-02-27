package com.example.e_shop.fragments.Product_Information.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.e_shop.R
import com.example.e_shop.databinding.ActivityProductDescriptionBinding
import com.example.e_shop.fragments.Product_Information.informationViewmodels.ProductDescriptionViewModel
import com.example.e_shop.fragments.Product_Information.informationViewmodels.ReviewsViewModel
import com.example.e_shop.fragments.Product_Information.tabMain
import com.example.e_shop.viewModels.factory.UserReviewFactory

lateinit var viewModel: ProductDescriptionViewModel
lateinit var reviewViewModel: ReviewsViewModel

class Product_Description : AppCompatActivity() {

    private lateinit var binding: ActivityProductDescriptionBinding
    lateinit var productId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val toolBarTitle: TextView = findViewById(R.id.StoreToolBarTitle)
        toolBarTitle.text = ""

        val toolIcon: ImageView = findViewById(R.id.toolMenu)
        toolIcon.setImageResource(R.drawable.ic_search)

        productId = intent.getStringExtra("productId")!!

        viewModel = ViewModelProvider(this, ProductDescriptionViewModel.ViewModelFactory(productId))[ProductDescriptionViewModel::class.java]
        reviewViewModel = ViewModelProvider(this, UserReviewFactory(productId))[ReviewsViewModel::class.java]

        replaceFragment(tabMain())

        val backArrow: ImageView = findViewById(R.id.storeToolbarBackIcon)
        backArrow.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }
    }



    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.des_frame_layout, fragment)
        fragmentTransaction.commit()
    }

}
