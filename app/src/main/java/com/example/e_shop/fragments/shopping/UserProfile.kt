package com.example.e_shop.fragments.shopping

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.e_shop.Digital_Store.activities.Create_Store
import com.example.e_shop.Digital_Store.Store
import com.example.e_shop.Profile.Fragments.*
import com.example.e_shop.Profile.Fragments.personalInfo.PersonalInfo
import com.example.e_shop.Profile.fragmentToActivity
import com.example.e_shop.R
import com.example.e_shop.data.User
import com.example.e_shop.databinding.ActivityUserProfileBinding
import com.example.e_shop.fragments.IntroductionFragment
import com.example.e_shop.util.auth
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import com.example.e_shop.util.userProfileCreateState
import com.example.e_shop.viewModels.NotificationViewModel
import com.example.e_shop.viewModels.ReviewViewModel

lateinit var userReviewViewmodel: ReviewViewModel
lateinit var notificationViewmodel: NotificationViewModel

class UserProfile : Fragment() {

    private lateinit var binding: ActivityUserProfileBinding
    private val SHARED_PREFS = "sharedPrefs"

    init {
        fragmentToActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityUserProfileBinding.inflate(inflater)
        userProfileCreateState = true
        userReviewViewmodel = ViewModelProvider(this)[ReviewViewModel::class.java]
        notificationViewmodel = ViewModelProvider(this)[NotificationViewModel::class.java]
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsIcon.setOnClickListener{
            val drawer = binding.profileDrawer
            drawer.openDrawer(GravityCompat.END)
        }

        showFragment(FavouriteProducts(this))

        val defaultBackground = binding.orders.background
        val selectedBackground = binding.favouriteProducts.background
        viewFragment(defaultBackground, selectedBackground)

        val headerView = binding.nav.getHeaderView(0)
        val clPersonalInfo: ConstraintLayout = headerView.findViewById(R.id.cl_personalInfo)
        var clPayment: ConstraintLayout = headerView.findViewById(R.id.cl_payments)
        val clStore: ConstraintLayout = headerView.findViewById(R.id.cl_store)
        var clLanguage: ConstraintLayout = headerView.findViewById(R.id.cl_language)
        var clPrivacy: ConstraintLayout = headerView.findViewById(R.id.cl_privacy)
        val clLogout: ConstraintLayout = headerView.findViewById(R.id.cl_logout)
        val storeText:TextView = headerView.findViewById(R.id.storeText)

        fireStore.collection(constant.UserCollection).document(auth.currentUser!!.uid)
            .get().addOnSuccessListener {result ->
                val user: User = result.toObject(User::class.java)!!

                binding.fullName.text = user.firstName + " " + user.lastName
                binding.userEmail.text = user.email

                clPersonalInfo.setOnClickListener{
                    val intent = Intent(activity, PersonalInfo::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
                }

                if(user.haveStore){
                    storeText.text = "Go To Your Store"
                    clStore.setOnClickListener{
                        val intent = Intent(activity, Store::class.java)
                        intent.putExtra("storeId",user.storeId)
                        startActivity(intent)
                        activity?.overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
                    }
                }
                else{
                    storeText.text = "Create Your Store"
                    clStore.setOnClickListener{
                        val intent = Intent(activity, Create_Store::class.java)
                        startActivity(intent)
                        activity?.overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
                    }
                }
            }
            .addOnFailureListener{
                //
            }

        clLogout.setOnClickListener{
            val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS,
                AppCompatActivity.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("name", "")
            editor.apply()

            startActivity(Intent(activity, IntroductionFragment::class.java))
            activity?.finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun showFragment(newFragment: Fragment) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.user_frame_layout, newFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun viewFragment(defaultBackground: Drawable, selectedBackground: Drawable){
        val views = listOf(binding.orders, binding.favouriteProducts, binding.notification, binding.reviews, binding.feedback, binding.helpCenter)
        val viewsMap = mutableMapOf<View, Fragment>(binding.orders to Orders(this),
            binding.favouriteProducts to FavouriteProducts(this),
            binding.notification to NotificationFragment(this),
            binding.reviews to ReviewsFragment(),
            binding.feedback to Feedback(this),
            binding.helpCenter to HelpCenter(this))

        for (view in viewsMap) {
            viewsMap.forEach{ (view, fragment) ->
                view.setOnClickListener {
                    showFragment(fragment)
                    for (v in views) {
                        v.background = defaultBackground
                    }
                    view.background = selectedBackground
                }
            }
        }
    }

}