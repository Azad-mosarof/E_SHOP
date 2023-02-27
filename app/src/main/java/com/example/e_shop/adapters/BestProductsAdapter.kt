package com.example.e_shop.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_shop.R
import com.example.e_shop.data.Product
import com.example.e_shop.data.productId
import com.example.e_shop.databinding.ProductRvItemBinding
import com.example.e_shop.fragments.Product_Information.activity.Product_Description
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore

class BestProductsAdapter(private val context: Context): RecyclerView.Adapter<BestProductsAdapter.BestProductViewHolder>() {
    var state: Boolean = false
    inner class BestProductViewHolder(private var binding: ProductRvItemBinding):
        RecyclerView.ViewHolder(binding.root){
            @SuppressLint("SetTextI18n")
            fun bind(product: Product){
                binding.apply {
                    Glide.with(itemView).load(product.images[0]).into(imgProduct)
                    tvName.text = product.name
                    tvPrice.text = "₹"+product.price.toString()
                    tvNewPrice.text = "₹"+(product.offerPercentage?.let {
                        newPrice(product.price,
                            it
                        ).toString()
                    })
                    binding.imgFavourite.setOnClickListener{
                        state = if(!state){
                            binding.favourite.setImageResource(R.drawable.heart)
                            fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid)
                                .collection(constant.FavouriteProductsCollections).document(product.id)
                                .set(productId(product.id))
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Product added to your favourite list", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{
                                    //
                                }
                            true
                        } else{
                            binding.favourite.setImageResource(R.drawable.black_heart)
                            false
                        }
                    }
                    binding.offerPercentage.text = "(${product.offerPercentage}% off)"
                    binding.wholeView.setOnClickListener{
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
        return BestProductViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}