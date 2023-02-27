package com.example.e_shop.Digital_Store.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.e_shop.Digital_Store.Store_Dp
import com.example.e_shop.data.store_info
import com.example.e_shop.databinding.ActivityCreateStoreBinding
import com.example.e_shop.util.RegisterValidation
import com.example.e_shop.util.constant
import com.example.e_shop.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Create_Store : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoreBinding
    private var fireStore = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    var randArr: Array<Int> = arrayOf(0,1,2,3,4,5,6,7,8,9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        auth = FirebaseAuth.getInstance()

        binding.createStore.setOnClickListener{
            binding.createStore.startAnimation()
            val s_name = binding.storeName.text.toString()
            val s_owner = binding.ownerName.text.toString()
            val s_category = binding.storeCategory.text.toString()
            val s_email = binding.storeOfficialMail.text.toString()
            val s_location = binding.storeLocation.text.toString()
            val customer_service = binding.customerService.text.toString()
            val store_ID = genStoreId()
            val store = store_info(store_ID,s_name, s_owner, s_category, s_email, s_location,
                customer_service, auth.currentUser!!.uid, 0)
            checkValidityAndCreateStore(store)
        }
    }

    fun genStoreId(): String{
        var SID: String = "SID"
        for(i in 0..5){
            SID += randArr[(Math.random() * 10).toInt()]
        }
        return SID
    }

    fun createStore(storeInfo: store_info){
        fireStore.collection(constant.StoresCollection).document(storeInfo.s_ID).set(storeInfo)
            .addOnCompleteListener() {
                Toast.makeText(this,"Your store successfully created and your store id is ${storeInfo.s_ID}", Toast.LENGTH_SHORT).show()
                binding.createStore.revertAnimation()
                fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid).update("haveStore",true)
                fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid).update("storeId",storeInfo.s_ID)
                val intent = Intent(this, Store_Dp::class.java)
                intent.putExtra("storeId", storeInfo.s_ID)
                startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                binding.createStore.revertAnimation()
            }
    }

    fun checkValidityAndCreateStore(storeInfo: store_info){
        val emailValidation = validateEmail(storeInfo.s_email)

        val notValid = emailValidation !is RegisterValidation.Success ||
                binding.storeName.text.isEmpty() ||
                binding.ownerName.text.isEmpty() ||
                binding.storeCategory.text.isEmpty() ||
                binding.storeLocation.text.isEmpty() ||
                binding.customerService.text.isEmpty()

        if (!notValid)
            createStore(storeInfo)
        if (emailValidation is RegisterValidation.Failed) {
            binding.storeOfficialMail.apply {
                binding.createStore.revertAnimation()
                requestFocus()
                error = emailValidation.message
            }
        }
        if(binding.storeName.text.isEmpty()){
            binding.storeName.apply {
                binding.createStore.revertAnimation()
                requestFocus()
                error = "Store name can't be empty"
            }
        }
        if(binding.ownerName.text.isEmpty()){
            binding.ownerName.apply {
                binding.createStore.revertAnimation()
                requestFocus()
                error = "Store owner name can't be empty"
            }
        }
        if(binding.storeCategory.text.isEmpty()){
            binding.storeCategory.apply {
                binding.createStore.revertAnimation()
                requestFocus()
                error = "Store category can't be empty"
            }
        }
        if(binding.storeLocation.text.isEmpty()){
            binding.storeLocation.apply {
                binding.createStore.revertAnimation()
                requestFocus()
                error = "Store location can't be empty"
            }
        }
        if(binding.customerService.text.isEmpty()){
            binding.customerService.apply {
                binding.createStore.revertAnimation()
                requestFocus()
                error = "Customer service can't be empty"
            }
        }
    }
}