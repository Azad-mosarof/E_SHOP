package com.example.e_shop.Digital_Store.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_shop.data.Product
import com.example.e_shop.databinding.EditorViewerBinding
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore


class ProductEditorAdapter(val context: Context, val productId: String): RecyclerView.Adapter<ProductEditorAdapter.ProductEditorViewHolder>() {

    var delImgs = mutableListOf<String>()

    inner class ProductEditorViewHolder(val binding: EditorViewerBinding):
        RecyclerView.ViewHolder(binding.root){
            fun bind(img: String){
                binding.apply {
                    Glide.with(itemView).load(img).into(prodImage)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductEditorViewHolder {
        return ProductEditorViewHolder(
            EditorViewerBinding.inflate(
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

    override fun onBindViewHolder(holder: ProductEditorViewHolder, position: Int) {
        val img = differ.currentList[position]
        holder.bind(img)

        holder.binding.delete.setOnClickListener{
            fireStore.collection(constant.ProductsCollections).document(productId).get()
                .addOnSuccessListener {
                    val product: Product = it.toObject(Product::class.java)!!
                    val newImages = (product.images).filter { it != img }
                    if(newImages.isNotEmpty()){
                        delImgs.add(img)
                        removeItem(position)
                    }
                    else{
                        Toast.makeText(context, "At least one image of the product is required", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun removeItem(position: Int) {
        if (position >= 0 && position < differ.currentList.size) {
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