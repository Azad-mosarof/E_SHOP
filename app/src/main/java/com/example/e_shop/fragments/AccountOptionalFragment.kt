package com.example.e_shop.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_shop.R
import com.example.e_shop.databinding.ActivityAccountOptionalFragmentBinding
import com.example.e_shop.databinding.ActivityIntroductionFragmentBinding

class AccountOptionalFragment : AppCompatActivity() {

    private lateinit var  binding: ActivityAccountOptionalFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountOptionalFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}