package com.example.e_shop.Digital_Store.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.e_shop.Digital_Store.fragments.DeliverdOrders
import com.example.e_shop.Digital_Store.fragments.New_Orders
import com.example.e_shop.R
import com.example.e_shop.adapters.DescriptionPagerAdapter
import com.example.e_shop.databinding.ActivityOrdersBinding
import com.example.e_shop.fragments.Product_Information.informationFragments.Details
import com.example.e_shop.fragments.Product_Information.informationFragments.Questions
import com.example.e_shop.fragments.Product_Information.informationFragments.Reviews
import com.google.android.material.tabs.TabLayoutMediator

class Orders_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val backIcon: ImageView = findViewById(R.id.toolBarBackIcon)
        backIcon.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

        val informationFragments = arrayListOf(
            New_Orders(),
            DeliverdOrders()
        )
        binding.viewPagerHome.isUserInputEnabled = false
        val descriptionPager2Adapter = DescriptionPagerAdapter(informationFragments,supportFragmentManager,lifecycle)
        binding.viewPagerHome.adapter = descriptionPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome){tab, position ->
            when(position){
                0 -> tab.text = "New Orders"
                1 -> tab.text = "Delivered Orders"
            }
        }.attach()
    }
}