package com.example.e_shop.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.e_shop.R
import com.example.e_shop.adapters.homeViewPagerAdapter
import com.example.e_shop.databinding.HomeBinding
import com.example.e_shop.fragments.categories.*
import com.google.android.material.tabs.TabLayoutMediator

class home_fragment: Fragment(R.layout.home) {
    private lateinit var binding: HomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragment = arrayListOf<Fragment>(
            MainCategory(),
            FoodCategory(),
            StationaryCategory(),
            ClothesCategory(),
            Accessory()
        )
        binding.viewPagerHome.isUserInputEnabled = false
        val viewPager2Adapter = homeViewPagerAdapter(categoriesFragment,childFragmentManager,lifecycle)
        binding.viewPagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPagerHome){tab, position ->
            when(position){
                0 -> tab.text = "Main"
                1 -> tab.text = "Food"
                2 -> tab.text = "Stationary"
                3 -> tab.text = "Clothes"
                4 -> tab.text = "Accessory"
            }
        }.attach()
    }
}