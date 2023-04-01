package com.example.e_shop.activities.shopLandingPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_shop.R
import com.example.e_shop.databinding.ActivityShopLandingPageBinding

class ShopLandingPage : AppCompatActivity() {

    private lateinit var binding: ActivityShopLandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}