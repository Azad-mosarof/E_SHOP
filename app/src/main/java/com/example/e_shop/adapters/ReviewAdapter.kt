package com.example.e_shop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.e_shop.data.Review
import com.example.e_shop.databinding.ReviewLayoutModelBinding
import com.example.e_shop.databinding.ReviewRvBinding

class ReviewAdapter: RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(private val binding: ReviewLayoutModelBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bind(review: Review){
                    binding.apply {
                        reviewTitle.text = review.title
                        reviewContent.text = review.review
                        name.text = review.customerName.toLowerCase()+", "
                        place.text = review.place


                        val dateTimeString: String = review.time
                        val dateString = dateTimeString.substring(0, 10)

                        reviewTime.text = "Verified Purchase . $dateString"
                        likes.text = review.likes.toString()
                        disLikes.text = review.disLikes.toString()
                    }
                }
            }

    private val differCallback = object : DiffUtil.ItemCallback<Review>(){
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            ReviewLayoutModelBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = differ.currentList[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}