package com.example.e_shop.Profile



import android.os.Bundle
import com.example.e_shop.R
import com.example.e_shop.activities.ShoppingActivity
import com.example.e_shop.fragments.shopping.UserProfile


class fragmentToActivity : ShoppingActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, UserProfile()).commit()
        }
    }
}