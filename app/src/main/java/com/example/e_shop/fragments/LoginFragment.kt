package com.example.e_shop.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_shop.R
import com.example.e_shop.databinding.ActivityLoginBinding
import com.example.e_shop.databinding.ActivityLoginFragmentBinding

class LoginFragment : AppCompatActivity() {

    private  lateinit var binding: ActivityLoginFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}