package com.example.e_shop

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.e_shop.activities.ShoppingActivity
import com.example.e_shop.databinding.ActivityLoginBinding
import com.example.e_shop.fragments.RegisterFragment
import com.example.e_shop.util.*
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val SHARED_PREFS = "sharedPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val toolBar: ConstraintLayout = findViewById(R.id.toolBar)
        toolBar.setBackgroundColor(resources.getColor(R.color.light_background))

        auth = FirebaseAuth.getInstance()

        binding.loginBt.setOnClickListener{
            showLoading()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            checkValidityAndLogin(email,password)
        }

        binding.register.setOnClickListener{
            val intent = Intent(this,RegisterFragment::class.java)
            intent.putExtra("toReg", login_reg)
            startActivity(intent)
            overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
            finish()
        }

        val backArrow: ImageView = findViewById(R.id.toolBarBackIcon)
        backArrow.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

    }


    private  fun login(email:String,password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("name", "true")
                    editor.apply()
                    hideLoading()
                    val intent = Intent(this,ShoppingActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    hideLoading()
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun checkValidityAndLogin(email: String, password: String){
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)

        val validity = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success

        if (validity)
            login(email, password)
        if (emailValidation is RegisterValidation.Failed) {
            binding.email.apply {
                hideLoading()
                requestFocus()
                error = emailValidation?.message
            }
        }
        if(passwordValidation is RegisterValidation.Failed){
            binding.password.apply {
                hideLoading()
                requestFocus()
                error = passwordValidation?.message
            }
        }
    }

    private fun showLoading(){
        binding.loginProgressBar.visibility = View.VISIBLE
        binding.loginBt.visibility = View.GONE
    }

    private fun hideLoading(){
        binding.loginProgressBar.visibility = View.GONE
        binding.loginBt.visibility = View.VISIBLE
    }
}