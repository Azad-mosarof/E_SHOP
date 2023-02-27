package com.example.e_shop.Digital_Store.activities


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_shop.Digital_Store.Adapters.ProductEditorAdapter
import com.example.e_shop.R
import com.example.e_shop.data.Product
import com.example.e_shop.databinding.ActivityProductEditorBinding
import com.example.e_shop.fragments.Product_Information.informationViewmodels.ProductDescriptionViewModel
import com.example.e_shop.util.Resource
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import com.example.e_shop.util.storageRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*

class ProductEditor : AppCompatActivity() {

    private lateinit var binding: ActivityProductEditorBinding
    private lateinit var editorAdapter: ProductEditorAdapter
    private lateinit var editorViewModel: ProductDescriptionViewModel
    var selectedImages = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val textView: TextView = findViewById(R.id.StoreToolBarTitle)
        val backIcon: ImageView = findViewById(R.id.storeToolbarBackIcon)
        val update: ImageView = findViewById(R.id.toolMenu)

        textView.text = "Edit Product Details"
        textView.setTextColor(Color.parseColor("#FF97AABD"))
        backIcon.setColorFilter(Color.parseColor("#FF97AABD"), PorterDuff.Mode.SRC_IN)
        update.setImageResource(R.drawable.updated)
//        update.setColorFilter(Color.parseColor("#FF97AABD"), PorterDuff.Mode.SRC_IN)

        backIcon.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

        val productId = intent.getStringExtra("productId")
        editorViewModel = ViewModelProvider(this, ProductDescriptionViewModel.ViewModelFactory(productId!!)).get(
            ProductDescriptionViewModel::class.java)

        fillEditText(productId)

        editorAdapter = ProductEditorAdapter(this, productId)
        binding.recyclerView.apply {
            adapter = editorAdapter
            layoutManager = LinearLayoutManager(this@ProductEditor, LinearLayoutManager.HORIZONTAL, false)
        }

        lifecycleScope.launchWhenStarted {
            editorViewModel.clickedProduct.collectLatest {
                when(it){
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        editorAdapter.differ.submitList(it.data?.images)
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }

        val selectImageActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode == RESULT_OK){
                val intent = result.data

                if(intent?.clipData != null){
                    val count = intent.clipData?.itemCount
                    (0 until count!!).forEach{
                        val imageUri = intent.clipData?.getItemAt(it)?.uri
                        selectedImages.add(imageUri!!)
                    }
                }
                else{
                    val imageUri = intent?.data
                    selectedImages.add(imageUri!!)
                }
            }
        }

        binding.addImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImageActivityResult.launch(intent)
        }

        update.setOnClickListener{
            try {
                updateProduct(productId)
            }catch (e: Exception){
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
                Log.i("err", e.message.toString())
            }
        }

    }

    private fun fillEditText(id: String){
        fireStore.collection(constant.ProductsCollections).document(id).get()
            .addOnSuccessListener {
                val product = it.toObject(Product::class.java)!!
                binding.title.setText(product.name)
                binding.description.setText(product.description)
                binding.price.setText(product.price.toString())
                binding.offer.setText(product.offerPercentage.toString())
                binding.stock.setText(product.stock.toString())
            }
    }

    private fun getImageByteArrays(): List<ByteArray>{
        val imageByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach{
            val stream = ByteArrayOutputStream()
            val imgBmp = getBitmap(contentResolver, it)
            if(imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)){
                imageByteArray.add(stream.toByteArray())
            }
        }
        return imageByteArray
    }

    private fun updateProduct(id: String){
        val images = mutableListOf<String>()
        lifecycleScope.launch(Dispatchers.IO){
            withContext(Dispatchers.Main){
                showLoading()
            }
            try {
                withContext(Dispatchers.Default){
                    getImageByteArrays().forEach{
                        val id = UUID.randomUUID().toString()
                        launch {
                            val result = storageRef.child("Products/images/$id").putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            Log.i("xxxx", downloadUrl)
                            images.add(downloadUrl)
                        }
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

            val updatedImages = editorViewModel.totalImages
            updatedImages.removeAll(editorAdapter.delImgs)
            updatedImages.addAll(images)

            fireStore.collection(constant.ProductsCollections).document(id).update(
                mutableMapOf(
                    "description" to binding.description.text.toString(),
                    "name" to binding.title.text.toString(),
                    "images" to updatedImages,
                    "price" to binding.price.text.toString().toFloat(),
                    "offerPercentage" to binding.offer.text.toString().toFloat(),
                    "stock" to binding.stock.text.toString().toInt()
                ) as Map<String, Any>
            ).addOnSuccessListener {
                selectedImages = mutableListOf()
                deleteImages()
                hideLoading()
                Toast.makeText(this@ProductEditor, "Product successfully updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Log.i("exrr", it.message.toString())
                hideLoading()
            }
        }
    }

    private fun showLoading() {
        binding.editorProgressBar.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.editorProgressBar.visibility = View.GONE
    }

    private fun deleteImages(){
        for(img in editorAdapter.delImgs){
            val regex = "([a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12})"
            val match = regex.toRegex().find(img)
            val id = match?.value.toString()
            storageRef.child("Products/images/$id").delete()
        }
    }

}