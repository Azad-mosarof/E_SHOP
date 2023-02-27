package com.example.e_shop.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_shop.data.Notification
import com.example.e_shop.databinding.NotificationModelLayoutBinding
import com.example.e_shop.databinding.NotificationRvBinding

class NotificationAdapter: RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val binding: NotificationModelLayoutBinding):
        RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(notification: Notification){
            binding.apply {
                Glide.with(itemView).load(notification.imgUrl).into(notificationImg)
                notificationTitle.text = notification.title
                message.text = notification.message
                arrivalTime.text = "${notification.arrivalTime} day ago"
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Notification>(){
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.NotificationViewHolder {
        return NotificationViewHolder(
            NotificationModelLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = differ.currentList[position]
        holder.bind(notification)
    }
}