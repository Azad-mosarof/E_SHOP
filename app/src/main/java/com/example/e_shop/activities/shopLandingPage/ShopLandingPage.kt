package com.example.e_shop.activities.shopLandingPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_shop.R
import com.example.e_shop.adapters.BestProductsAdapter
import com.example.e_shop.databinding.ActivityShopLandingPageBinding
import com.example.e_shop.util.Utility
import com.example.e_shop.viewModels.CartViewModel
import com.example.e_shop.viewModels.factory.cartViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class ShopLandingPage : AppCompatActivity() {

    private lateinit var binding: ActivityShopLandingPageBinding
    private lateinit var shopProductsAdapter: BestProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val storeId = intent.getStringExtra("storeId")
        val storeName = intent.getStringExtra("storeName")

        Utility().toolBarSetup(storeName!!,this@ShopLandingPage, "#4377FB")

        val viewModel by viewModels<SLPViewModel>{
            SLPFactory(storeId!!)
        }

        shopProductsAdapter = BestProductsAdapter(this@ShopLandingPage)

        binding.shopItemsRV.apply {
            adapter = shopProductsAdapter
            layoutManager = GridLayoutManager(this@ShopLandingPage,2,LinearLayoutManager.VERTICAL,false)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.SLPProducts.collectLatest {
                when(it){
                    is com.example.e_shop.util.Resource.Loading -> {
                        showLoading()
                    }
                    is com.example.e_shop.util.Resource.Success -> {
                        shopProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is com.example.e_shop.util.Resource.Error -> {
                        hideLoading()
                    }
                }
            }
        }
    }

    private fun showLoading(){
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.progressBar.visibility = View.GONE
    }
}