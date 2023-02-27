package com.example.e_shop.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_shop.data.CartProduct
import com.example.e_shop.data.Product
import com.example.e_shop.util.Resource
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

//ord 1 -> buy
//ord 0 -> cart or order summary

class CartViewModel(private val productId: String?): ViewModel() {

    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts

    lateinit var buyProduct: Product

    var totalCost = 0
    var actualPrice = 0
    var discount = 0

    init {
        if(productId == "null"){
            fetchCartProducts()
        }
        else{
            fetchBuyProduct()
        }
    }

    private fun fetchBuyProduct() {
        fireStore.collection(constant.ProductsCollections).document(productId!!).get()
            .addOnSuccessListener {
                buyProduct = it.toObject(Product::class.java)!!
            }
            .addOnFailureListener{

            }
    }

    private fun fetchCartProducts() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }
        fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid)
            .collection(constant.Cart).get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Success(it.toObjects(CartProduct::class.java)))

                    for(product in it.toObjects(CartProduct::class.java)){
                        totalCost += newPrice(product!!.price, product.offerPercentage!!) * product.quantity
                        actualPrice += product.price.toInt() * product.quantity
                    }
                    discount = actualPrice - totalCost

                    fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid)
                        .collection(constant.Cart).addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            // Handle error
                            return@addSnapshotListener
                        }
                            totalCost = 0
                            actualPrice = 0
                            discount = 0
                        if (snapshot != null) {
                            for (doc in snapshot.documents) {
                                val product = doc.toObject(CartProduct::class.java)
                                totalCost += newPrice(product!!.price, product.offerPercentage!!) * product.quantity
                                actualPrice += product.price.toInt() * product.quantity
                            }
                            discount = actualPrice - totalCost
                        }
                    }
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun newPrice(oldPrice:Float,offer:Float): Int {
        return  (oldPrice - (oldPrice * offer)/100).toInt()
    }

}