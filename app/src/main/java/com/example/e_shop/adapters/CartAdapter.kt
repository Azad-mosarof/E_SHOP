package com.example.e_shop.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_shop.R
import com.example.e_shop.activities.OrderActivities.OrderSummary
import com.example.e_shop.data.CartProduct
import com.example.e_shop.databinding.CartRvBinding
import com.example.e_shop.fragments.shopping.cart
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore


class CartAdapter(private val context: Context, private val fragment: cart): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(var binding: CartRvBinding):
            RecyclerView.ViewHolder(binding.root){
                @SuppressLint("SetTextI18n")
                fun bind(product: CartProduct, position: Int){
                    binding.apply {
                        val newPrice = newPrice(product.price, product.offerPercentage!!)
                        productOldPrice.text = "₹"+product.price.toString()
                        productNewPrice.text = "₹$newPrice"
                        offerPersentage.text = product.offerPercentage.toString()+"% off"
                        Glide.with(itemView).load(product.images).into(productImage)

                        val defaultValue = product.quantity
                        val quantityArray = listOf("1", "2", "3", "4", "5")
                        val spinerAdapter = ArrayAdapter(context, R.layout.custom_spiner_selector, quantityArray)
                        spinerAdapter.setDropDownViewResource(R.layout.my_drop_down_items)
                        spinner.adapter = spinerAdapter

                        val position = spinerAdapter.getPosition(defaultValue.toString())
                        spinner.setSelection(position)

                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedItem = spinner.selectedItem as String
                                    val value = selectedItem.toInt()
                                    fireStore.collection(constant.UserCollection).document(auth.currentUser?.uid!!)
                                        .collection(constant.Cart).document(product.id).update("quantity", value)
                                        .addOnSuccessListener {
                                            fragment.totalCost()
                                        }
                                        .addOnFailureListener{
                                            //
                                        }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    // Do nothing
                                }
                            }

                        buyNow.setOnClickListener{
                            val intent = Intent(context, OrderSummary::class.java)
                            intent.putExtra("productId", product.id)
                            context.startActivity(intent)
                        }
                    }
                }
            }


    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

    fun newPrice(oldPrice:Float,offer:Float): Int {
        return  (oldPrice - (oldPrice * offer)/100).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.CartViewHolder {
        return CartViewHolder(
            CartRvBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product, position)
        if (position >= 0 && position < differ.currentList.size){
            holder.binding.remove.setOnClickListener{
                removeItem(position, product)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun removeItem(position: Int, product:CartProduct) {
        if (position >= 0 && position < differ.currentList.size) {
            fireStore.collection(constant.UserCollection).document(auth.currentUser?.uid!!)
                .collection(constant.Cart).document(product.id).delete()
                .addOnSuccessListener {
                    fragment.totalCost()
                }
                .addOnFailureListener{
                    //
                }
            val newList = differ.currentList.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
            differ.submitList(newList)
            notifyItemChanged(position)
        }else{
            notifyItemChanged(position)
            notifyDataSetChanged()
        }
    }

}