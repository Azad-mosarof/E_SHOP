package com.example.e_shop.Profile.Fragments.personalInfo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.e_shop.R
import com.example.e_shop.data.User
import com.example.e_shop.databinding.ActivityPersonalInfoBinding
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore

class PersonalInfo : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalInfoBinding
    private lateinit var toolBarTitle: TextView
    private lateinit var back: ImageView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        toolBarTitle = findViewById(R.id.tootlBarTitle)
        back = findViewById(R.id.toolBarBackIcon)
        toolBarTitle.text = "Personal Info"

        back.setOnClickListener{
//            val intent = Intent(this, ShoppingActivity::class.java)
//            intent.putExtra("key",1)
//            startActivity(intent)
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

        fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)!!
                binding.userName.text = user.firstName + " " + user.lastName
                binding.userMail.text = user.email
                binding.firstName.text = "Hi, ${user.firstName}"
                binding.userAddress.text = user.address
            }

        binding.editInfoBtn.setOnClickListener{
            startActivity(Intent(this, EditInfo::class.java))
            overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
        }
    }
}