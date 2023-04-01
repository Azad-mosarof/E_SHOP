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
import com.example.e_shop.data.Product
import com.example.e_shop.databinding.BestDealsRvItemBinding
import com.example.e_shop.fragments.Product_Information.activity.Product_Description

class BestDealsAdapter(val context: Context): RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding):
        RecyclerView.ViewHolder(binding.root){
            @SuppressLint("SetTextI18n")
            fun bind(product: Product){
                binding.apply {
                    Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                    tvDealProductName.text = product.name
                    tvOldPrice.text = "M.R.P: ₹"+product.price.toString()
                    tvNewPrice.text = "₹"+(product.offerPercentage?.let {
                        newPrice(product.price,
                            it
                        ).toString()
                    })
                    binding.offerPercentage.text = "(${product.offerPercentage}% off)"

                    btnSeeProduct.setOnClickListener{
                        val intent = Intent(context, Product_Description::class.java)
                        intent.putExtra("storeId",product.storeId)
                        intent.putExtra("productId",product.id)
                        context.startActivity(intent)
                    }
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

    fun newPrice(oldPrice:Float,offer:Float): Int {
        return  (oldPrice - (oldPrice * offer)/100).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}