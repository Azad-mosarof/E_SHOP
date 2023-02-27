package com.example.e_shop.fragments.Product_Information.informationFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_shop.R
import com.example.e_shop.databinding.FragmentReviewsBinding
import com.example.e_shop.fragments.Product_Information.activity.ReviewTaker
import com.example.e_shop.fragments.Product_Information.activity.reviewViewModel
import com.example.e_shop.fragments.Product_Information.adapters.ReviewImagesAdapter
import com.example.e_shop.fragments.Product_Information.adapters.UserReviewsAdapter
import com.example.e_shop.util.Resource
import kotlinx.coroutines.flow.collectLatest

class Reviews : Fragment() {

    private lateinit var binding: FragmentReviewsBinding
    private lateinit var reviewAdapter: UserReviewsAdapter
    private lateinit var reviewImagesAdapter: ReviewImagesAdapter
    var max = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewsBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        reviewAdapter = UserReviewsAdapter()
        binding.userReviewRv.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        reviewImagesAdapter = ReviewImagesAdapter(requireContext(),reviewViewModel.pId)
        binding.reviewImageRv.apply {
            adapter = reviewImagesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        fillProgressBar()


        lifecycleScope.launchWhenStarted {
            reviewViewModel.userReviews.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.reviewProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        reviewAdapter.differ.submitList(it.data)
                        binding.reviewProgressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.reviewProgressBar.visibility = View.GONE
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            reviewViewModel.reviewImages.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.reviewImageProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        reviewImagesAdapter.differ.submitList(it.data)
                        binding.reviewImageProgressBar.visibility = View.GONE
                    }
                    is Error -> {
                        binding.reviewImageProgressBar.visibility = View.GONE
                    }
                }
            }
        }

        binding.rateProduct.setOnClickListener{
            val intent = Intent(requireContext(), ReviewTaker::class.java)
            intent.putExtra("productId",reviewViewModel.pId)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left)
        }
    }

    private fun fillProgressBar(){
        max = reviewViewModel.listOfRatings.max()
        val listOfProgressbar = listOf(binding.val5ProgressBar, binding.val4ProgressBar,
            binding.val3ProgressBar, binding.val2ProgressBar, binding.val1ProgressBar)
        val listTv = listOf(binding.tv5, binding.tv4, binding.tv3, binding.tv2, binding.tv1)

        for(x in 4 downTo 0){
            listOfProgressbar[x].max = max
            listOfProgressbar[x].progress = reviewViewModel.listOfRatings[x]
            listTv[x].text = reviewViewModel.listOfRatings[x].toString()
        }
        binding.totalRatings.text = reviewViewModel.listOfRatings.sum().toString()+" ratings \nand ${reviewViewModel._reviews} reviews"
    }

}