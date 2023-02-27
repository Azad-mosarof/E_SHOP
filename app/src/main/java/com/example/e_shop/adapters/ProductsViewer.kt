package com.example.e_shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_shop.R
import com.example.e_shop.data.Product
import com.example.e_shop.databinding.ProductViewerBinding
import com.example.e_shop.fragments.Product_Information.informationFragments.Details
import com.example.e_shop.util.bigProductViewUrl
import com.example.e_shop.util.url

class ProductsViewer(
    private val context: Context,
    private val fragment: Details
): RecyclerView.Adapter<ProductsViewer.ProductViewerHolder>() {

    inner class ProductViewerHolder(private val binding:ProductViewerBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bind(img: String){
                    binding.apply {
                        Glide.with(itemView).load(img).into(prodImage)
                        prodImage.setOnClickListener {
                            fragment.updateImageView(img)
                            url = img
                        }
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewerHolder {
        return ProductViewerHolder(ProductViewerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        )
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.length == newItem.length
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onBindViewHolder(holder: ProductViewerHolder, position: Int) {
        val img = differ.currentList[position]
        holder.bind(img)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}