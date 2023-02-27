package com.example.e_shop.fragments

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_shop.R
import com.example.e_shop.activities.LoginAndGesture
import com.example.e_shop.activities.ShoppingActivity
import com.example.e_shop.databinding.ActivityIntroductionFragmentBinding

class IntroductionFragment : AppCompatActivity() {

    private lateinit var binding: ActivityIntroductionFragmentBinding
    private val SHARED_PREFS = "sharedPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroductionFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        checkBox()

        binding.startButton.setOnClickListener{
            val intent = Intent(this@IntroductionFragment,LoginAndGesture::class.java)
            startActivity(intent)
            overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
        }
    }

    private fun checkBox() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val check: String? = sharedPreferences.getString("name", "")
        if(check.equals("true")){
            val intent = Intent(this, ShoppingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}