package com.example.e_shop.activities.OrderActivities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.e_shop.Digital_Store.Store
import com.example.e_shop.R
import com.example.e_shop.data.*
import com.example.e_shop.databinding.ActivityPaymentBinding
import com.example.e_shop.firebase.FirebaseService
import com.example.e_shop.util.auth
import com.example.e_shop.util.buyOverCart
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import dev.shreyaspatil.easyupipayment.model.TransactionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentActivity : AppCompatActivity(), PaymentStatusListener {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var titleView: TextView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        titleView = findViewById(R.id.tootlBarTitle)
        titleView.text = "Payments"
        val backIcon: ImageView = findViewById(R.id.toolBarBackIcon)

        backIcon.setOnClickListener{
            finish()
            overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_right)
        }

        val actualPrice: Int = intent.getIntExtra("actualPrice", 0)
        val discount: Int = intent.getIntExtra("discount", 0)


        binding.actualPrice.text = "₹$actualPrice"
        binding.discount.text = "- ₹$discount"
        binding.totalCost.text = "₹"+(actualPrice - discount).toString()
        binding.totalAmount.text = "₹"+(actualPrice - discount).toString()

        binding.pay.setOnClickListener{
            try{
//                val transactionId = "TID" + System.currentTimeMillis()
//
//                val easyUpiPayment = EasyUpiPayment(this) {
//                    this.payeeVpa = "7908589984@paytm"
//                    this.payeeName = "AZAD MOSAROF"
//                    this.payeeMerchantCode = "SRjpsI09001157068950"
//                    this.transactionId = transactionId
//                    this.transactionRefId = transactionId
//                    this.description = "Lesso Shopping"
//                    this.amount = "1.00"
////                this.amount = (actualPrice - discount).toFloat().toString()
//                }
//                easyUpiPayment.startPayment()
//                easyUpiPayment.setPaymentStatusListener(this)
                lifecycleScope.launch(Dispatchers.IO){
                    withContext(Dispatchers.Main){

                    }
                    try {
                        if(!buyOverCart){
                            val productsList: ArrayList<CartProduct> = intent.getSerializableExtra("product_list") as ArrayList<CartProduct>
                            takeOrder(productsList)
                        }
                        else{
                            val order = intent.getSerializableExtra("Order") as Order
                            fireStore.collection(constant.StoresCollection).document(order.storeId).get()
                                .addOnSuccessListener {
                                    val store = it.toObject(store_info::class.java)!!
                                    saveOrder(order, store_owner_id = store.s_o_uid)
                                }
                        }
                    }
                    catch (e: Exception){
                        Toast.makeText(this@PaymentActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }
            catch(e: Exception){
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onTransactionCancelled() {
        Toast.makeText(this, "Transaction is cancelled by user", Toast.LENGTH_SHORT).show()
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails) {
        Toast.makeText(this, transactionDetails.toString(), Toast.LENGTH_SHORT).show()

        when (transactionDetails.transactionStatus) {
            TransactionStatus.SUCCESS -> onTransactionSuccess()
            TransactionStatus.FAILURE -> onTransactionFailed()
            TransactionStatus.SUBMITTED -> onTransactionSubmitted()
        }
    }
//upi://pay?pa=paytmqr2810050501011ooqggb29a01@paytm&pn=Paytm%20Merchant&mc=5499&mode=02&orgid=000000&paytmqr=2810050501011OOQGGB29A01&am=11&sign=MEYCIQDq96qhUnqvyLsdgxtfdZ11SQP//6F7f7VGJ0qr//lF/gIhAPgTMsopbn4Y9DiE7AwkQEPPnb2Obx5Fcr0HJghd4gzo"
    private fun onTransactionSuccess() {
        Toast.makeText(this, "Transaction is complete",Toast.LENGTH_SHORT).show()
    }

    private fun onTransactionSubmitted() {
        Toast.makeText(this, "Transaction is submitted", Toast.LENGTH_SHORT).show()
    }

    private fun onTransactionFailed() {
        Toast.makeText(this, "Transaction is failed", Toast.LENGTH_SHORT).show()
    }

    private fun sendNotification(store_owner_id: String, title: String, content: String){
        fireStore.collection(constant.UserCollection).document(store_owner_id).get()
            .addOnSuccessListener {
                val owner = it.toObject(User::class.java)!!
                FirebaseService.sendMessage(title, content, owner.reg_token)
            }
            .addOnFailureListener{
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveOrder(order: Order, num_orders: Int = 1, store_owner_id: String){
        fireStore.collection(constant.StoresCollection).document(order.storeId).get()
            .addOnSuccessListener {
                val store = it.toObject(store_info::class.java)!!
                it.reference.collection(constant.Orders).document(order.order_id).set(order)
                it.reference.update("new_orders", (store.new_orders + num_orders))
                val title = "You have received a new order"
                val content = "Order id is #${order.order_id}\n${order.name}"
                sendNotification(store_owner_id,title, content)
            }
            .addOnFailureListener{
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun takeOrder(productList: ArrayList<CartProduct>){
        for(product in productList){
            fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!
                    val random = java.util.Random()
                    val orderId = "OID${random.nextInt(100000000) + 10000000}"

                    val order = Order(orderId,product.id, product.name, product.price.toInt(),
                        product.offerPercentage?.toInt(), product.images, product.store_id,
                        product.quantity, auth.currentUser!!.uid, user.Phone, user.address,
                        "23 min", 30, "delivered")

                    saveOrder(order, productList.size, product.store_owner_id)
                }
        }
    }

}