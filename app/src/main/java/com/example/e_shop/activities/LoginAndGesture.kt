package com.example.e_shop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.e_shop.LoginActivity
import com.example.e_shop.R
import com.example.e_shop.databinding.ActivityLoginAndGestureBinding
import com.example.e_shop.fragments.RegisterFragment
import com.example.e_shop.util.logReg_reg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginAndGesture : AppCompatActivity() {

    private lateinit var binding: ActivityLoginAndGestureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAndGestureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val toolBar: ConstraintLayout = findViewById(R.id.toolBar)
        toolBar.setBackgroundColor(resources.getColor(R.color.light_background))

        binding.loginBt.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
        }

        binding.registerBt.setOnClickListener{
            val intent = Intent(this,RegisterFragment::class.java)
            intent.putExtra("toReg", logReg_reg)
            startActivity(intent)
            overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
        }

        val backArrow: ImageView = findViewById(R.id.toolBarBackIcon)
        backArrow.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }
    }
}