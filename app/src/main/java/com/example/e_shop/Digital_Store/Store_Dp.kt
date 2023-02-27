package com.example.e_shop.Digital_Store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_shop.R
import com.example.e_shop.databinding.ActivityStoreDpBinding

class Store_Dp : AppCompatActivity() {

    private lateinit var binding: ActivityStoreDpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreDpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        binding.skip.setOnClickListener{
            val storeIntent = Intent(this, Store::class.java)
            storeIntent.putExtra("storeId",intent.getStringExtra("storeId"))
            startActivity(storeIntent)
        }
    }
}