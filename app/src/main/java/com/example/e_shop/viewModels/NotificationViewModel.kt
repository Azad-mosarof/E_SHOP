package com.example.e_shop.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_shop.data.Notification
import com.example.e_shop.util.Resource
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel: ViewModel() {

    private val _notificationList = MutableStateFlow<Resource<List<Notification>>>(Resource.Unspecified())
    val notificationList = _notificationList.asStateFlow()

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            _notificationList.emit(Resource.Loading())
        }
        fireStore.collection(constant.UserCollection).document(auth.currentUser?.uid!!)
            .collection(constant.Notifications).get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _notificationList.emit(Resource.Success(it.toObjects(Notification::class.java)))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _notificationList.emit(Resource.Error(it.message))
                }
            }
    }
}