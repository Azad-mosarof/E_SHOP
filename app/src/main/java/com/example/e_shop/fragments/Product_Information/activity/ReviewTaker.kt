package com.example.e_shop.fragments.Product_Information.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.e_shop.R
import com.example.e_shop.data.Review
import com.example.e_shop.data.User
import com.example.e_shop.data.productId
import com.example.e_shop.databinding.ActivityReviewTakerBinding
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ReviewTaker : AppCompatActivity() {

    private lateinit var binding: ActivityReviewTakerBinding
    private var selectedImages = mutableListOf<Uri>()
    private var productStorage = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewTakerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val productId = intent.getStringExtra("productId")

        val toolbarTitle: TextView = findViewById(R.id.StoreToolBarTitle)
        val back: ImageView = findViewById(R.id.storeToolbarBackIcon)
        val menu: ImageView = findViewById(R.id.toolMenu)

        menu.setImageResource(R.drawable.updated)
        toolbarTitle.text = ""

        back.setOnClickListener {
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

        menu.setOnClickListener {
            try {
                saveReview(productId!!)
            } catch (e: Exception) {
                binding.reviewTakerProgressBar.visibility = View.GONE
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        val selectIMagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data

                    //Multiple images selected
                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount
                        (0 until count!!).forEach {
                            val imageUri = intent.clipData?.getItemAt(it)?.uri
                            imageUri?.let { imgUri ->
                                selectedImages.add(imgUri)
                            }
                        }
                    } else {
                        val imageUri = intent?.data
                        imageUri?.let { selectedImages.add(it) }
                    }
                }
            }

        binding.addImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectIMagesActivityResult.launch(intent)
        }

    }

    private fun saveReview(productId: String) {
        val imagesByteArray = getImagesByteArrays()
        val images = mutableListOf<String>()

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showLoading()
            }
            try {
                withContext(Dispatchers.Default) {
                    imagesByteArray.forEach { ByteArr ->
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imageStorage = productStorage.child("Products/images/reviewImg/$productId/$id")
                            val result = imageStorage.putBytes(ByteArr).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    hideLoading()
                }
            }

            val rating = binding.rating.text.toString().toInt()
            val title = binding.reviewTitle.text.toString()
            val userReview = binding.review.text.toString()
            fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!
                    val userName = user.firstName + " " + user.lastName

                    val reviewDateTime = Date()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val reviewDateTimeString = dateFormat.format(reviewDateTime)

                    val review = Review(
                        rating, title, userReview, productId, auth.currentUser!!.uid, userName,
                        "west bengal", true, reviewDateTimeString, 0, 0, images
                    )
                    fireStore.collection(constant.ProductsCollections).document(productId)
                        .collection(constant.Reviews).add(review)
                        .addOnSuccessListener {
                            fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid).collection(constant.Reviews).add(
                                productId(productId)
                            ).addOnSuccessListener {
                                Toast.makeText(this@ReviewTaker, "Review successfully added", Toast.LENGTH_SHORT)
                                    .show()
                                hideLoading()
                            }
                                .addOnFailureListener{
                                    hideLoading()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@ReviewTaker, it.message.toString(), Toast.LENGTH_SHORT).show()
                            hideLoading()
                        }
                }
        }

    }

    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
            val stream = ByteArrayOutputStream()
            val imgBmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
            if (imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                imagesByteArray.add(stream.toByteArray())
            }
        }
        return imagesByteArray
    }

    private fun showLoading() {
        binding.reviewTakerProgressBar.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.reviewTakerProgressBar.visibility = View.GONE
    }

}
