package com.example.e_shop.fragments.Product_Information.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.e_shop.R
import com.example.e_shop.data.Review
import com.example.e_shop.databinding.ActivityImageReviewBinding
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageReview : AppCompatActivity() {

    private lateinit var binding: ActivityImageReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val backIcon: ImageView = findViewById(R.id.toolBarBackIcon)
        val toolTitle: TextView = findViewById(R.id.tootlBarTitle)

        toolTitle.text = ""

        backIcon.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                binding.reviewImageProgrssBar.visibility = View.VISIBLE
            }
            fireStore.collection(constant.ProductsCollections).document(intent.getStringExtra("pId")!!).collection(constant.Reviews).get()
                .addOnSuccessListener {
                    val reviews = it.toObjects(Review::class.java)
                    for(review in reviews){
                        val images = review.images
                        if(intent.getStringExtra("reviewImg") in images){
                            binding.rating.text = review.star.toString()
                            binding.review.text = review.review
                            Glide.with(this@ImageReview).load(intent.getStringExtra("reviewImg")).into(binding.reviewImage)
                        }
                    }
                    binding.reviewImageProgrssBar.visibility = View.GONE
                }
                .addOnFailureListener{
                    Toast.makeText(this@ImageReview, it.message.toString(), Toast.LENGTH_SHORT).show()
                    binding.reviewImageProgrssBar.visibility = View.GONE
                }
        }
    }
}