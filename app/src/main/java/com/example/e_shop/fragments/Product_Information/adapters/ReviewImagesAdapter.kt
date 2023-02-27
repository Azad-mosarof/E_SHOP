package com.example.e_shop.fragments.Product_Information.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_shop.databinding.ReviewImagesBinding
import com.example.e_shop.fragments.Product_Information.activity.ImageReview

class ReviewImagesAdapter(val context: Context, val productId: String): RecyclerView.Adapter<ReviewImagesAdapter.ReviewImageHolder>() {
    inner class ReviewImageHolder(private var binding: ReviewImagesBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(img: String){
            binding.apply {
                Glide.with(itemView).load(img).into(prodImage)
            }
            binding.wholeView.setOnClickListener{
                val intent = Intent(context, ImageReview::class.java)
                intent.putExtra("reviewImg",img)
                intent.putExtra("pId", productId)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageHolder {
        return ReviewImageHolder(
            ReviewImagesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
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

    override fun onBindViewHolder(holder: ReviewImageHolder, position: Int) {
        val img = differ.currentList[position]
        holder.bind(img)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}