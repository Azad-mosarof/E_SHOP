package com.example.e_shop.activities

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.e_shop.R
import com.example.e_shop.data.Product
import com.example.e_shop.data.productId
import com.example.e_shop.databinding.ActivityProductAdderBinding
import com.example.e_shop.util.constant
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*


class ProductAdder : AppCompatActivity() {

    private lateinit var binding: ActivityProductAdderBinding
    private var selectedImages = mutableListOf<Uri>()
    private val selectedColors = mutableListOf<Int>()
    private var productStorage = Firebase.storage.reference
    private val fireStore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductAdderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val textView: TextView = findViewById(R.id.StoreToolBarTitle)
        val backIcon: ImageView = findViewById(R.id.storeToolbarBackIcon)
        val uploadBtn: ImageView = findViewById(R.id.toolMenu)

        textView.text = "Add Product"
        textView.setTextColor(Color.parseColor("#FF97AABD"))
        backIcon.setColorFilter(Color.parseColor("#FF97AABD"), PorterDuff.Mode.SRC_IN)
        uploadBtn.setImageResource(R.drawable.upload)

        backIcon.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

        binding.buttonColorPicker.setOnClickListener{
            ColorPickerDialog.Builder(this)
                .setTitle("Product color")
                .setPositiveButton("Select",object : ColorEnvelopeListener{
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColors.add(it.color)
                            updateColors()
                        }
                    }
                })
                .setNegativeButton("Cancel"){colorPicker, _ ->
                    colorPicker.dismiss()
                }.show()
        }

        val selectIMagesActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode == RESULT_OK){
                val intent = result.data

                //Multiple images selected
                if(intent?.clipData != null){
                    val count = intent.clipData?.itemCount
                    (0 until count!!).forEach{
                        val imageUri = intent.clipData?.getItemAt(it)?.uri
                        imageUri?.let { imgUri ->
                            selectedImages.add(imgUri)
                        }
                    }
                }else{
                    val imageUri = intent?.data
                    imageUri?.let { selectedImages.add(it) }
                }
                updateImages()
            }
        }

        binding.buttonImagesPicker.setOnClickListener{
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.type = "image/*"
            selectIMagesActivityResult.launch(intent)
        }

        uploadBtn.setOnClickListener{
            val productValidation = validateInformation()
            if(!productValidation){
                Toast.makeText(this,"Please check your inputs",Toast.LENGTH_SHORT).show()
            }
            else{
                saveProduct()
            }
        }
    }

    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.offerPercentage.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val sizes = getSizesList(binding.edSizes.text.toString().trim())
        val imagesByteArray = getImagesByteArrays()
        val images = mutableListOf<String>()

//        uploadImageToFirebase(imagesByteArray)

        lifecycleScope.launch(Dispatchers.IO){
            withContext(Dispatchers.Main){
                showLoading()
            }
            try {
                withContext(Dispatchers.Default) {
                    imagesByteArray.forEach {ByteArr ->
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imageStorage = productStorage.child("Products/images/$id")
                            val result = imageStorage.putBytes(ByteArr).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    hideLoading()
                }
            }
            val storeId: String? = intent.getStringExtra("storeId")
            val product = Product(
                UUID.randomUUID().toString(),
                name,
                category,
                price.toFloat(),
                if(offerPercentage.isEmpty()) null else  offerPercentage.toFloat(),
                description.ifEmpty { null },
                if(selectedColors.isEmpty()) null else selectedColors,
                sizes,
                images,
                storeId!!,
                0
            )
            fireStore.collection(constant.ProductsCollections).document(product.id).set(product)
                .addOnSuccessListener {

                }.addOnFailureListener{
                    Log.e("Error",it.message.toString())
                }
            fireStore.collection(constant.StoresCollection).document(storeId)
                .collection(constant.ProductsCollections).add(productId(product.id))
                .addOnSuccessListener {
                    Toast.makeText(this@ProductAdder,"Product Successfully added", Toast.LENGTH_SHORT).show()
                    hideLoading()
                }.addOnFailureListener{
                    hideLoading()
                    Log.e("Error",it.message.toString())
                }
        }
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach{
            val stream = ByteArrayOutputStream()
            val imgBmp = getBitmap(contentResolver,it)
            if(imgBmp.compress(Bitmap.CompressFormat.JPEG,100,stream)){
                imagesByteArray.add(stream.toByteArray())
            }
        }
        return imagesByteArray
    }

    //S, M, L, XL
    private fun getSizesList(sizes: String): List<String>? {
        if (sizes.isEmpty()) {
            return null
        }
        return sizes.split(",")
    }

    private fun validateInformation(): Boolean {
        if(binding.edPrice.text.toString().trim().isEmpty())
            return false
        if(binding.edName.text.toString().trim().isEmpty())
            return false
        if(binding.edCategory.text.toString().trim().isEmpty())
            return false
        if(selectedImages.isEmpty())
            return false

        return true
    }

    private fun updateColors(){
        var colors = ""
        selectedColors.forEach{
            colors = "$colors ${Integer.toHexString(it)}"
        }
        binding.tvSelectedColors.text = colors
    }

//    private fun uploadImageToFirebase(bytes: List<ByteArray>) {
//        val refStorage = Firebase.storage.reference
//        Toast.makeText(this,bytes.size.toString(),Toast.LENGTH_SHORT).show()
//        if (bytes.isNotEmpty()) {
//            bytes.forEach{byte ->
//                val fileName = UUID.randomUUID().toString()+".jpg"
//
//                refStorage.child("product/images/$fileName").putBytes(byte)
//                    .addOnSuccessListener { taskSnapshot ->
//                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
//                            val imageUrl = it.toString()
//                            images.add(imageUrl)
//                            Log.i("Image Url", imageUrl)
//                            Toast.makeText(this,"okk",Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    .addOnFailureListener { e ->
//                        Log.e("Error",e.toString())
//                    }
//            }
//        }
//    }
}