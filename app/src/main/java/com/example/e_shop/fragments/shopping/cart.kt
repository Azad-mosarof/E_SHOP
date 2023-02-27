package com.example.e_shop.fragments.shopping

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_shop.R
import com.example.e_shop.activities.OrderActivities.OrderSummary
import com.example.e_shop.adapters.CartAdapter
import com.example.e_shop.databinding.ActivityShoppingCartBinding
import com.example.e_shop.viewModels.CartViewModel
import com.example.e_shop.viewModels.factory.cartViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class cart: Fragment() {

    private lateinit var binding: ActivityShoppingCartBinding
    val viewModel by viewModels<CartViewModel>{
        cartViewModelFactory("null")
    }
    private val cartAdapter: CartAdapter by lazy { CartAdapter(requireContext(), this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityShoppingCartBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbarTitle = view.findViewById<TextView>(R.id.tootlBarTitle)
        toolbarTitle?.text = "My Cart"

        setAdapter()
        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is com.example.e_shop.util.Resource.Loading -> {
                        showLoading()
                    }
                    is com.example.e_shop.util.Resource.Success -> {
                        cartAdapter.differ.submitList(it.data)
                        totalCost()
                        hideLoading()
                        if(viewModel.totalCost == 0){
                            binding.priceDetailsLayout.visibility = View.GONE
                        }
                    }
                    is com.example.e_shop.util.Resource.Error -> {
                        hideLoading()
                    }
                }
            }
        }

        binding.placeOrder.setOnClickListener{
            val intent = Intent(requireContext(), OrderSummary::class.java)
            intent.putExtra("productId", "null")
            startActivity(intent)
        }

    }

    private fun setAdapter(){
        binding.cartRecyclerView.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun hideLoading() {
        binding.cartProgressbar.visibility = View.GONE
        binding.priceDetailsLayout.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.cartProgressbar.visibility = View.VISIBLE
        binding.priceDetailsLayout.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    fun totalCost(){
        binding.totalCost.text = "₹"+viewModel.totalCost.toString()
        binding.actualPrice.text = "₹"+viewModel.actualPrice.toString()
        binding.discount.text = "- ₹"+viewModel.discount.toString()
        binding.totalAmount.text = "₹"+viewModel.totalCost.toString()
        binding.saveAmount.text = "You will save ₹${viewModel.discount} on this order"
        if(viewModel.totalCost == 0){
            binding.priceDetailsLayout.visibility = View.GONE
        }
        else{
            binding.priceDetailsLayout.visibility = View.VISIBLE
        }
    }

    fun newPrice(oldPrice:Float,offer:Float): Int {
        return  (oldPrice - (oldPrice * offer)/100).toInt()
    }

}