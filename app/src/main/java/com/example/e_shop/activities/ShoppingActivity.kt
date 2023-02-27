package com.example.e_shop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.e_shop.R
import com.example.e_shop.databinding.ActivityShoppingBinding
import com.example.e_shop.fragments.shopping.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class ShoppingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

//        val value = intent.getIntExtra("key",0)
//
//        if(value != 0){
//            val fragment = UserProfile()
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.frame_layout, fragment)
//                .commit()
//        }else{
//            replaceFragment(home_fragment())
//        }

        replaceFragment(home_fragment())

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(home_fragment())
                R.id.cart -> replaceFragment(cart())
                R.id.profile -> replaceFragment(UserProfile())
                R.id.search -> replaceFragment(search())

                else -> {

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}