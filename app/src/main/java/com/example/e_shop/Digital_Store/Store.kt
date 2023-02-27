package com.example.e_shop.Digital_Store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_shop.Digital_Store.activities.Orders_Activity
import com.example.e_shop.R
import com.example.e_shop.activities.ProductAdder
import com.example.e_shop.adapters.StoreProductsAdapter
import com.example.e_shop.data.Product
import com.example.e_shop.data.store_info
import com.example.e_shop.databinding.ActivityStoreBinding
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore

const val TAG: String = "StoreActivity"
var storeId = ""

class Store : AppCompatActivity() {

    private lateinit var binding: ActivityStoreBinding
    private lateinit var storeProductAdapter: StoreProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val backIcon: ImageView = findViewById(R.id.toolBarBackIcon)
        backIcon.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

        val toolBarTitle: TextView = findViewById(R.id.tootlBarTitle)
        val header = binding.nav.getHeaderView(0)
        val sId: TextView = header.findViewById(R.id.storeId)
        val ownerName: TextView = header.findViewById(R.id.ownerName)
        val new_orders: TextView = header.findViewById(R.id.newOrders)
        val ordersView: LinearLayout = header.findViewById(R.id.orders)

        storeId = intent.getStringExtra("storeId")!!

        ordersView.setOnClickListener{
            val intent = Intent(this, Orders_Activity::class.java)
            intent.putExtra("storeId", storeId)
            startActivity(intent)
            overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
        }

        fireStore.collection(constant.StoresCollection).document(storeId)
            .get().addOnSuccessListener {
                val storeInfo = it.toObject(store_info::class.java)!!
                sId.text = "SID: " + storeInfo.s_ID
                new_orders.text = storeInfo.new_orders.toString() + "+"

                val words = storeInfo.s_name.split(" ")
                val capitalizedWords = words.map {
                    val firstLetter = it.substring(0, 1).toUpperCase()
                    val restOfWord = it.substring(1)
                    firstLetter + restOfWord
                }
                val capitalizedText = capitalizedWords.joinToString(" ")
                toolBarTitle.text = capitalizedText

                ownerName.text = "Owner: " + storeInfo.s_o_name
            }
            .addOnFailureListener{
                Toast.makeText(this@Store,it.message,Toast.LENGTH_SHORT).show()
            }

        setUpStoreItems()
        if(storeId.isNotEmpty()){
            fetchStoreItems(storeId)
        }

        val headerView = binding.nav.getHeaderView(0)
        val productAdder: LinearLayout = headerView.findViewById(R.id.add_Product)
        productAdder.setOnClickListener{
            val adderIntent = Intent(this,ProductAdder::class.java)
            adderIntent.putExtra("storeId", storeId)
            startActivity(adderIntent)
        }
    }

    fun setUpStoreItems(){
        storeProductAdapter = StoreProductsAdapter(this)
        binding.storeItems.apply {
            layoutManager = LinearLayoutManager(this@Store,LinearLayoutManager.VERTICAL, false)
            adapter = storeProductAdapter
        }
    }

    private fun hideLoading() {
        binding.storeItemsProgressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.storeItemsProgressBar.visibility = View.VISIBLE
    }

    private fun fetchStoreItems(storeId: String){
        showLoading()
        try {
            fireStore.collection(constant.ProductsCollections).whereEqualTo("storeId", storeId)
                .get().addOnSuccessListener {result ->
                    val storeItemsList = result.toObjects(Product::class.java)
                    if(storeItemsList.isNotEmpty()){
                        storeProductAdapter.differ.submitList(storeItemsList)
                    }
                    hideLoading()
                }
                .addOnFailureListener{
                    hideLoading()
                    Toast.makeText(this@Store,it.message, Toast.LENGTH_SHORT).show()
                }
        }catch (e: Exception){
            Log.i(TAG,"Store have no products")
            hideLoading()
        }
    }

}