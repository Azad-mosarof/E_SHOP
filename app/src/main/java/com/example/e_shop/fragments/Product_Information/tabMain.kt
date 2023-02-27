package com.example.e_shop.fragments.Product_Information

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_shop.R
import com.example.e_shop.adapters.DescriptionPagerAdapter
import com.example.e_shop.databinding.FragmentTabMainBinding
import com.example.e_shop.fragments.Product_Information.informationFragments.Details
import com.example.e_shop.fragments.Product_Information.informationFragments.Questions
import com.example.e_shop.fragments.Product_Information.informationFragments.Reviews
import com.google.android.material.tabs.TabLayoutMediator

class tabMain : Fragment(R.layout.fragment_tab_main) {

    private lateinit var binding: FragmentTabMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val informationFragments = arrayListOf<Fragment>(
            Details(),
            Questions(),
            Reviews()
        )
        binding.viewPagerHome.isUserInputEnabled = false
        val descriptionPager2Adapter = DescriptionPagerAdapter(informationFragments,childFragmentManager,lifecycle)
        binding.viewPagerHome.adapter = descriptionPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome){tab, position ->
            when(position){
                0 -> tab.text = "Information"
                1 -> tab.text = "Questions"
                2 -> tab.text = "Reviews"
            }
        }.attach()
    }
}