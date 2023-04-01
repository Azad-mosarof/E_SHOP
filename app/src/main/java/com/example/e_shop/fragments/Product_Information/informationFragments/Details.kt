package com.example.e_shop.fragments.Product_Information.informationFragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.net.Uri.parse
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.e_shop.R
import com.example.e_shop.activities.OrderActivities.OrderSummary
import com.example.e_shop.adapters.ProductsViewer
import com.example.e_shop.data.CartProduct
import com.example.e_shop.data.Product
import com.example.e_shop.data.store_info
import com.example.e_shop.databinding.FragmentDetailsBinding
import com.example.e_shop.fragments.Product_Information.activity.viewModel
import com.example.e_shop.util.*
import kotlinx.coroutines.flow.collectLatest
import java.io.ByteArrayOutputStream
import java.io.File


val TAG: String = "Details"

class Details : Fragment(R.layout.fragment_details) {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var productViewerAdapter: ProductsViewer
    private var flag: Boolean = false
    private var product: Product? = null
    var store: store_info? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        setUpProductViewer()

        lifecycleScope.launchWhenStarted {
            viewModel.clickedProduct.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        Utility().showLoading(binding.detailsProgressBar)
                        binding.wholeView.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        product = it.data!!
                        productViewerAdapter.differ.submitList(product?.images)
                        Glide.with(requireContext())
                            .load(product!!.images[0]).into(binding.bigImg)
                        binding.description.text = product?.description
                        binding.oldPrice.text = product?.price?.toInt().toString()
                        binding.title.text = product?.name.toString()
                        binding.newPrice.text =
                            " ₹"+product?.offerPercentage?.let { it1 ->
                                product?.price?.let { it2 ->
                                    newPrice(
                                        it2,
                                        it1
                                    ).toString()
                                }
                            }

                        fireStore.collection(constant.StoresCollection).document(product?.storeId!!).get()
                            .addOnSuccessListener {
                                try{
                                    store = it.toObject(store_info::class.java)!!
                                    binding.ownerStore.text = "Visit the "+store!!.s_name
                                }catch (e: Exception){
                                    //
                                }
                            }
                            .addOnFailureListener{
                                Log.i("error", it.message.toString())
                            }
                        binding.offerPercentage.text = product?.offerPercentage?.toInt().toString()+"% off   "
                        url = product!!.images[0]
                        binding.share.setOnClickListener{
                            getUri(url, product!!.name)
                        }

                        if(store != null){
                            binding.addToCart.setOnClickListener{
                                val cartProduct = CartProduct(product!!.id, product!!.name,product!!.price, product!!.offerPercentage,
                                    product!!.images[0], 0, 0f,store!!.s_ID, store!!.s_o_uid)
                                fireStore.collection(constant.UserCollection).document(auth.currentUser?.uid!!)
                                    .collection(constant.Cart).document(product!!.id).set(cartProduct)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Product added to your cart", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener{
                                        Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }

                        binding.buyNow.setOnClickListener{
                            val intent = Intent(context, OrderSummary::class.java)
                            intent.putExtra("productId", product!!.id)
                            context?.startActivity(intent)
                        }

                        binding.wholeView.visibility = View.VISIBLE
                        Utility().hideLoading(binding.detailsProgressBar)
                    }
                    is Resource.Error -> {
                        Utility().hideLoading(binding.detailsProgressBar)
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.imgFavourite.setOnClickListener{
            if(flag){
                binding.imgFavourite.setImageResource(R.drawable.black_heart)
                binding.imgFavourite.setColorFilter(Color.parseColor("#FF97AABD"))
                flag = false
            }
            else{
                binding.imgFavourite.setImageResource(R.drawable.heart)
                binding.imgFavourite.setColorFilter(Color.RED)
                flag = true

                //add this favourite product to the firestore
                val favouriteProduct = CartProduct(product!!.id, product!!.name,product!!.price, product!!.offerPercentage,
                    product!!.images[0], 0, 0f,store!!.s_ID, store!!.s_o_uid)
                fireStore.collection(constant.UserCollection).document(auth.currentUser?.uid!!)
                    .collection(constant.FavouriteProductsCollections).document(product!!.id).set(favouriteProduct)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Product added to your favourite", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.addToCart.setOnClickListener{
            val cartProduct = CartProduct(product!!.id, product!!.name,product!!.price, product!!.offerPercentage,
                product!!.images[0], 0, 0f,store!!.s_ID, store!!.s_o_uid)
            fireStore.collection(constant.UserCollection).document(auth.currentUser?.uid!!)
                .collection(constant.Cart).document(product!!.id).set(cartProduct)
                .addOnSuccessListener {
                    Toast.makeText(context, "Product added to your cart", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun setUpProductViewer(){
        productViewerAdapter = ProductsViewer(requireContext(), this)
        binding.imgRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            adapter = productViewerAdapter
        }
    }

    private fun getUri(url: String, title: String) {
        Glide.with(requireContext())
            .load(url)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    // create an image URI from the downloaded image
                    val uri = resource.toUri()
                    // Create the share intent and set the type
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri) // imageUri is the URI of the image to be shared
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "$title\n$url") // link is the URL to be shared
                    startActivity(Intent.createChooser(shareIntent, "Share using"))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    fun Drawable.toUri(): Uri {
        val bytes = ByteArrayOutputStream()
        val bitmap = (this as BitmapDrawable).bitmap
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        Log.i("xxx", bitmap.toString())
        val sdcard: File? = Environment.getExternalStorageDirectory()
        if (sdcard != null) {
            val mediaDir = File(sdcard, "DCIM/Camera")
            if (!mediaDir.exists()) {
                mediaDir.mkdirs()
            }
        }
        var path = ""
        try{
            path = MediaStore.Images.Media.insertImage(context?.contentResolver, bitmap, "filename", null)
        }catch (e: Exception){
            if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            }
            Log.i("xxx", e.message.toString())
        }

        return parse(path)
    }

    fun newPrice(oldPrice:Float,offer:Float): Int {
        return  (oldPrice - (oldPrice * offer)/100).toInt()
    }

    fun updateImageView(imageUrl: String) {
        Glide.with(this).load(imageUrl).into(binding.bigImg)
    }

}