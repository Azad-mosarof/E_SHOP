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
import com.example.e_shop.data.CartProduct
import com.example.e_shop.data.Product
import com.example.e_shop.data.store_info
import com.example.e_shop.databinding.FavouriteProductViewBinding
import com.example.e_shop.fragments.Product_Information.activity.Product_Description
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore

class FavouriteProductsAdapter(private val context: Context): RecyclerView.Adapter<FavouriteProductsAdapter.FavouriteProductViewHolder>() {

    var state: Boolean = false
    inner class FavouriteProductViewHolder(private var binding: FavouriteProductViewBinding):
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

                addToCart.setOnClickListener{
                    fireStore.collection(constant.StoresCollection).document(product.storeId).get()
                        .addOnSuccessListener {
                            val store = it.toObject(store_info::class.java)!!
                            val cartProduct = CartProduct(product.id, product.name,product.price, product.offerPercentage,
                                product.images[0], 0, 0f,product.storeId, store.s_o_uid)
                            fireStore.collection(constant.UserCollection).document(auth.currentUser?.uid!!)
                                .collection(constant.Cart).document(product.id).set(cartProduct)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Product added to your cart", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{
                                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                                }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteProductViewHolder {
        return FavouriteProductViewHolder(
            FavouriteProductViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavouriteProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

}