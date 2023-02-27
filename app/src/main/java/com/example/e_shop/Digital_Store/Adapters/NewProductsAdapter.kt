package com.example.e_shop.Digital_Store.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_shop.data.Order
import com.example.e_shop.databinding.NewOrdersModelLayoutBinding
import com.example.e_shop.util.Utility

class NewOrdersAdapter: RecyclerView.Adapter<NewOrdersAdapter.NewOrdersViewHolder>() {

    inner class NewOrdersViewHolder(private val binding: NewOrdersModelLayoutBinding):
        RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(order: Order){
            binding.apply {
                Glide.with(itemView).load(order.image).into(productImg)
                productName.text = order.name
                val newPrice = Utility().newPrice(order.price.toFloat(), order.offerPercentage!!.toFloat())
                perPrice.text = "₹$newPrice"
                totalPrice.text = "₹${(newPrice * order.quantity)}"
                quantity.text = order.quantity.toString()
                orderId.text = "Order ID: ${order.order_id}"
            }
        }
    }

    val diffCallBAck = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.order_id == newItem.order_id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBAck)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewOrdersViewHolder {
        return NewOrdersViewHolder(
            NewOrdersModelLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NewOrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}