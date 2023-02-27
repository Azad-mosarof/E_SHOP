package com.example.e_shop.Profile.Fragments.personalInfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.e_shop.R
import com.example.e_shop.data.User
import com.example.e_shop.databinding.ActivityEditInfoBinding
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore

class EditInfo : AppCompatActivity() {

    private lateinit var binding: ActivityEditInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val toolBarTitle: TextView = findViewById(R.id.StoreToolBarTitle)
        toolBarTitle.text = "Edit Personal Info"

        val toolIcon: ImageView = findViewById(R.id.toolMenu)
        toolIcon.setImageResource(R.drawable.updated)

        val back: ImageView = findViewById(R.id.storeToolbarBackIcon)
        back.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

        fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)!!
                binding.firstName.setText(user.firstName)
                binding.lastName.setText(user.lastName)
                binding.email.setText(user.email)
                binding.phone.setText(user.Phone)
                binding.address.setText(user.address)
            }

        toolIcon.setOnClickListener{
            binding.editorProgressBar.visibility = View.VISIBLE
            fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid).update(
                mapOf(
                    "firstName" to binding.firstName.text.toString(),
                    "lastName" to binding.lastName.text.toString(),
                    "email" to binding.email.text.toString(),
                    "Phone" to binding.phone.text.toString(),
                    "address" to binding.address.text.toString()
                ))
                .addOnSuccessListener {
                    binding.editorProgressBar.visibility = View.GONE
                    Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }
}