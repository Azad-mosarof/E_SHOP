package com.example.e_shop.activities.OrderActivities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.e_shop.R
import com.example.e_shop.adapters.OrderSummaryAdapter
import com.example.e_shop.data.CartProduct
import com.example.e_shop.data.Order
import com.example.e_shop.data.Product
import com.example.e_shop.data.User
import com.example.e_shop.databinding.ActivityOrderSummaryBinding
import com.example.e_shop.util.*
import com.example.e_shop.viewModels.CartViewModel
import com.example.e_shop.viewModels.factory.cartViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OrderSummary : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSummaryBinding
    private val summaryAdapter: OrderSummaryAdapter by lazy { OrderSummaryAdapter(this) }
    private lateinit var viewmodel: CartViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val backIcon: ImageView = findViewById(R.id.toolBarBackIcon)
        val toolTitle: TextView = findViewById(R.id.tootlBarTitle)

        toolTitle.text = "Order Summary"

        backIcon.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

        val productId = intent.getStringExtra("productId")

        if(productId == "null"){
            binding.buyingLayout.visibility = View.GONE
            viewmodel = ViewModelProvider(this, cartViewModelFactory(productId))[CartViewModel::class.java]
            binding.orderDetailsRv.apply {
                layoutManager = LinearLayoutManager(this@OrderSummary, LinearLayoutManager.VERTICAL, false)
                adapter = summaryAdapter
            }

            lifecycleScope.launchWhenStarted {
                viewmodel.cartProducts.collectLatest {
                    when(it){
                        is Resource.Loading -> {
                            Utility().showLoading(binding.summaryProgrssBar)
                        }
                        is Resource.Success -> {
                            summaryAdapter.differ.submitList(it.data)
                            updateAllCost()
                            if(viewmodel.totalCost == 0){
                                binding.priceDetailsLayout.visibility = View.GONE
                            }
                            binding.pay.setOnClickListener{view ->
                                buyOverCart = false
                                val intent = Intent(this@OrderSummary, PaymentActivity::class.java)
                                intent.putExtra("actualPrice", viewmodel.actualPrice)
                                intent.putExtra("discount", viewmodel.discount)
                                if(it.data!!.size == 1)
                                    intent.putExtra("product_list", ArrayList(it.data))
                                else
                                    intent.putExtra("product_list", it.data as ArrayList<CartProduct>)
                                startActivity(intent)
                                overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
                            }
                            Utility().hideLoading(binding.summaryProgrssBar)
                        }
                        is Resource.Error -> {
                            Utility().hideLoading(binding.summaryProgrssBar)
                        }
                    }
                }
            }
        }
        else{
            lifecycleScope.launch(Dispatchers.IO){
                withContext(Dispatchers.Main){
                    binding.orderDetailsRv.visibility = View.GONE
                    Utility().showLoading(binding.summaryProgrssBar)
                }
                try {
                    fireStore.collection(constant.ProductsCollections).document(productId!!).get()
                        .addOnSuccessListener {
                            val product = it.toObject(Product::class.java)!!
                            Glide.with(this@OrderSummary).load(product.images[0]).into(binding.productImage)
                            binding.productTitle.text = product.name
                            binding.productOldPrice.text = "₹${product.price}"
                            binding.productNewPrice.text = "₹${Utility().newPrice(product.price, product.offerPercentage!!)}"
                            binding.offerPersentage.text = "${product.offerPercentage}% off"

                            val defaultValue = 1
                            val quantityArray = listOf("1", "2", "3", "4", "5")
                            val spinerAdapter = ArrayAdapter(this@OrderSummary, R.layout.custom_spiner_selector, quantityArray)
                            spinerAdapter.setDropDownViewResource(R.layout.my_drop_down_items)
                            binding.spinner.adapter = spinerAdapter

                            val position = spinerAdapter.getPosition(defaultValue.toString())
                            binding.spinner.setSelection(position)

                            binding.spinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem = binding.spinner.selectedItem as String
                                        val quantity = selectedItem.toInt()
                                        val totalCost = Utility().newPrice(product.price, product.offerPercentage) * quantity
                                        val actualPrice = product.price * quantity
                                        updateBuyProductCost(actualPrice.toInt(), totalCost)

                                        binding.pay.setOnClickListener{
                                            val intent = Intent(this@OrderSummary, PaymentActivity::class.java)
                                            val random = java.util.Random()
                                            val orderId = "OID${random.nextInt(100000000) + 10000000}"
                                            fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid).get()
                                                .addOnSuccessListener {
                                                    buyOverCart = true
                                                    val user = it.toObject(User::class.java)!!
                                                    val order = Order(orderId, product.id, product.name, product.price.toInt(), product.offerPercentage.toInt(),
                                                        product.images[0], product.storeId, quantity, auth.currentUser!!.uid, user.Phone, user.address, "10:00", 20, "not delivered")
                                                    intent.putExtra("Order", order)
                                                    intent.putExtra("actualPrice", actualPrice.toInt())
                                                    intent.putExtra("discount", (actualPrice - totalCost).toInt())
                                                    startActivity(intent)
                                                    overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
                                                }
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        // Do nothing
                                    }
                                }

                            binding.buyingLayout.visibility = View.VISIBLE
                            Utility().hideLoading(binding.summaryProgrssBar)
                        }
                        .addOnFailureListener{
                            Utility().hideLoading(binding.summaryProgrssBar)
                        }

                }
                catch (e: Exception){
                    Toast.makeText(this@OrderSummary, e.message.toString(), Toast.LENGTH_SHORT).show()
                    Utility().hideLoading(binding.summaryProgrssBar)
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    fun updateAllCost(){
        binding.totalCost.text = "₹"+viewmodel.totalCost.toString()
        binding.actualPrice.text = "₹"+viewmodel.actualPrice.toString()
        binding.discount.text = "- ₹"+viewmodel.discount.toString()
        binding.totalAmount.text = "₹"+viewmodel.totalCost.toString()
        binding.saveAmount.text = "save ₹${viewmodel.discount}"
        if(viewmodel.totalCost == 0){
            binding.priceDetailsLayout.visibility = View.GONE
        }
        else{
            binding.priceDetailsLayout.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateBuyProductCost(actualPrice: Int, totalCost: Int){
        val discount = actualPrice - totalCost
        binding.totalCost.text = "₹$totalCost"
        binding.actualPrice.text = "₹$actualPrice"
        binding.totalAmount.text = "₹$totalCost"
        binding.discount.text = "- ₹$discount"
        binding.saveAmount.text = "save ₹$discount"
        if(totalCost == 0){
            binding.priceDetailsLayout.visibility = View.GONE
        }
        else{
            binding.priceDetailsLayout.visibility = View.VISIBLE
        }
    }

}