package com.example.e_shop.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_shop.R
import com.example.e_shop.activities.OrderActivities.OrderSummary
import com.example.e_shop.adapters.OrderSummaryAdapter.OrderSummaryHolder
import com.example.e_shop.data.CartProduct
import com.example.e_shop.databinding.SummaryRvBinding
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore

class OrderSummaryAdapter(val context: Context) : RecyclerView.Adapter<OrderSummaryHolder>() {

    inner class OrderSummaryHolder(var binding: SummaryRvBinding):
        RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(product: CartProduct){
            binding.apply {
                productOldPrice.text = "₹"+product.price.toString()
                productNewPrice.text = "₹"+ product.price.let { newPrice(it, product.offerPercentage!!).toString() }
                offerPersentage.text = product.offerPercentage.toString()+"% off"
                Glide.with(itemView).load(product.images).into(productImage)

                val defaultValue = product.quantity
                val quantityArray = listOf("1", "2", "3", "4", "5")
                val spinerAdapter = ArrayAdapter(context, R.layout.custom_spiner_selector, quantityArray)
                spinerAdapter.setDropDownViewResource(R.layout.my_drop_down_items)
                spinner.adapter = spinerAdapter

                val position = spinerAdapter.getPosition(defaultValue.toString())
                spinner.setSelection(position)

                spinner.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedItem = spinner.selectedItem as String
                            val value = selectedItem.toInt()
                            fireStore.collection(constant.UserCollection).document(auth.currentUser?.uid!!)
                                .collection(constant.Cart).document(product.id).update("quantity", value)
                                .addOnSuccessListener {
                                    (context as OrderSummary).updateAllCost()
                                }
                                .addOnFailureListener{
                                    //
                                }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Do nothing
                        }
                    }
            }
        }
    }


    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

    fun newPrice(oldPrice:Float,offer:Float): Int {
        return  (oldPrice - (oldPrice * offer)/100).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryHolder {
        return OrderSummaryHolder(
            SummaryRvBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderSummaryHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}