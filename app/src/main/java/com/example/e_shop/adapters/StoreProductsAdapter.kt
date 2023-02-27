package com.example.e_shop.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_shop.Digital_Store.activities.ProductEditor
import com.example.e_shop.Digital_Store.Store
import com.example.e_shop.R
import com.example.e_shop.data.Product
import com.example.e_shop.databinding.StoreItemsBinding

class StoreProductsAdapter(private val context: Context): RecyclerView.Adapter<StoreProductsAdapter.StoreItemsHolder>() {

    inner class StoreItemsHolder(private var binding: StoreItemsBinding):
        RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                tvName.text = product.name
                tvPrice.text = "₹"+product.price.toString()
                tvNewPrice.text = "MRP: ₹"+(product.offerPercentage?.let {
                    newPrice(product.price,
                        it
                    ).toString()
                })
                editor.setOnClickListener{
                    val intent = Intent(context, ProductEditor::class.java)
                    intent.putExtra("productId", product.id)
                    context.startActivity(intent)
                    (context as Store).overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
                }
                stock.text = product.stock.toString()
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemsHolder {
        return StoreItemsHolder(
            StoreItemsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: StoreItemsHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun newPrice(oldPrice:Float,offer:Float): Int {
        return  (oldPrice - (oldPrice * offer)/100).toInt()
    }
}