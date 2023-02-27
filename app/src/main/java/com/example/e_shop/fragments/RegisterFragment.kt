package com.example.e_shop.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.e_shop.LoginActivity
import com.example.e_shop.R
import com.example.e_shop.data.User
import com.example.e_shop.databinding.ActivityRegisterFragmentBinding
import com.example.e_shop.firebase.FirebaseService
import com.example.e_shop.util.*
import com.example.e_shop.viewModels.RegisterViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val TAG = "fcm_token"

class RegisterFragment : AppCompatActivity() {

    private  lateinit var binding: ActivityRegisterFragmentBinding
    val viewModel by viewModels<RegisterViewModel>()
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val toolBar: ConstraintLayout = findViewById(R.id.toolBar)
        toolBar.setBackgroundColor(resources.getColor(R.color.light_background))

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        binding.registerBt.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO){
                withContext(Dispatchers.Main){
                    showLoading()
                }
                try {
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        val regToken = task.result
                        val user = User(
                            binding.firstName.text.toString().trim(),
                            binding.lastName.text.toString().trim(),
                            binding.email.text.toString().trim(),
                            haveStore = false,
                            storeId = "",
                            reg_token = regToken
                        )
                        val password = binding.password.text.toString()
                        checkValidityAndRegister(user, password)
                    })

                }catch (e: Exception){
                    //
                }
            }
        }

        binding.login.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.drawable.slide_in_left,R.drawable.slide_out_right)
            finish()
        }

        val backArrow: ImageView = findViewById(R.id.toolBarBackIcon)
        backArrow.setOnClickListener{
            val x: Int = intent.getIntExtra("toReg", 0)
            if( x == login_reg){
                startActivity(Intent(this, LoginActivity::class.java))
                overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
                finish()
            }
            if(x == logReg_reg) {
                finish()
                overridePendingTransition(R.drawable.slide_in_left,R.drawable.slide_out_right)
            }
        }

//
//        lifecycleScope.launchWhenStarted {
//            viewModel.register.collect{
//                when(it){
//                    is Resource.Loading -> {
//                        binding.registerBt.startAnimation()
//                    }
//                    is Resource.Success -> {
//                        Log.d("test", it.data.toString())
//                        binding.registerBt.revertAnimation()
//                    }
//                    is Resource.Error -> {
//                        Log.e("Error", it.message.toString())
//                        binding.registerBt.revertAnimation()
//                    }
//                    else -> Unit
//                }
//            }
//        }
    }

    private fun signing(user: User, password:String){
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUser(auth.currentUser?.uid!!, user)
                    val intent = Intent(this,LoginActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    hideLoading()
                    Toast.makeText(this,task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun checkValidityAndRegister(user: User, password: String){
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        val validity = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success

        if (validity)
            signing(user, password)
        if (emailValidation is RegisterValidation.Failed) {
            binding.email.apply {
                requestFocus()
                error = emailValidation.message
                hideLoading()
            }
        }
        if(passwordValidation is RegisterValidation.Failed){
            binding.password.apply {
                requestFocus()
                error = passwordValidation.message
                hideLoading()
            }
        }
    }

    private fun saveUser(userId: String, user: User) {
        db.collection(constant.UserCollection)
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                hideLoading()
                Toast.makeText(this,"User Successfully added", Toast.LENGTH_LONG).show()
            }.addOnFailureListener{e ->
                hideLoading()
                Toast.makeText(this,"$e", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    private fun showLoading(){
        binding.registrationProgressBar.visibility = View.VISIBLE
        binding.registerBt.visibility = View.GONE
    }

    private fun hideLoading(){
        binding.registrationProgressBar.visibility = View.GONE
        binding.registerBt.visibility = View.VISIBLE
    }
}