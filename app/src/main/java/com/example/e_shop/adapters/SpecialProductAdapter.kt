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
import com.example.e_shop.databinding.SpecialRvItemBinding
import com.example.e_shop.fragments.Product_Information.activity.Product_Description
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore

class SpecialProductAdapter(private val context: Context): RecyclerView.Adapter<SpecialProductAdapter.SpecialProductViewHolder>() {

    var state: Boolean = false

    inner class SpecialProductViewHolder(private val binding: SpecialRvItemBinding) :
        RecyclerView.ViewHolder(binding.root){
            @SuppressLint("SetTextI18n")
            fun bind(product: Product){
                binding.apply {
                    Glide.with(itemView).load(product.images[0]).into(imageSpecialRvItem)
                    tvSpecialProductName.text = product.name
                    tvPrice.text = "₹"+product.price.toString()
                    tvSpecialProductNewPrice.text = "₹"+(product.offerPercentage?.let {
                        newPrice(product.price,
                            it
                        ).toString()
                    })
                    binding.offerPercentage.text = "(${product.offerPercentage}% off)"
                }
                binding.imgFavouriteIcon.setOnClickListener{
                    state = if(!state){
                        binding.imgFavouriteIcon.setImageResource(R.drawable.heart)
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
                        binding.imgFavouriteIcon.setImageResource(R.drawable.black_heart)
                        false
                    }
                }
                binding.wholeView.setOnClickListener{
                    val intent = Intent(context, Product_Description::class.java)
                    intent.putExtra("storeId",product.storeId)
                    intent.putExtra("productId",product.id)
                    context.startActivity(intent)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductViewHolder {
        return SpecialProductViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: SpecialProductViewHolder, position: Int) {
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