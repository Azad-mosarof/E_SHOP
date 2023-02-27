package com.example.e_shop.Digital_Store.Viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_shop.data.Order
import com.example.e_shop.util.Resource
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewOrdersViewModel(private val storeId: String): ViewModel() {

    private val _orders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val orders = _orders.asStateFlow()

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            _orders.emit(Resource.Loading())
        }
        fireStore.collection(constant.StoresCollection).document(storeId).collection(constant.Orders).get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _orders.emit(Resource.Success(orders))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _orders.emit(Resource.Error(it.message.toString()))
                }
            }

    }

}