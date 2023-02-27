package com.example.e_shop.fragments.Product_Information.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.e_shop.data.Review
import com.example.e_shop.databinding.ReviewLayoutModelBinding
import java.text.SimpleDateFormat
import java.util.*


class UserReviewsAdapter: RecyclerView.Adapter<UserReviewsAdapter.UserReviewsHolder>() {

    inner class UserReviewsHolder(val binding: ReviewLayoutModelBinding) :
        RecyclerView.ViewHolder(binding.root) {
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

    private val differCallBack = object : DiffUtil.ItemCallback<Review>(){
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return (oldItem.time == newItem.time && oldItem.review == newItem.review)
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReviewsHolder {
        return UserReviewsHolder(
            ReviewLayoutModelBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: UserReviewsHolder, position: Int) {
        val review = differ.currentList[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}