package com.example.e_shop.Profile.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_shop.adapters.ReviewAdapter
import com.example.e_shop.databinding.FragmentReviews2Binding
import com.example.e_shop.fragments.shopping.userReviewViewmodel
import com.example.e_shop.util.Resource
import kotlinx.coroutines.flow.collectLatest


class ReviewsFragment() : Fragment() {

    private lateinit var binding: FragmentReviews2Binding
    private val reviewAdapter: ReviewAdapter by lazy { ReviewAdapter() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviews2Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            userReviewViewmodel.reviewList.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        setUpAdapter()
                        reviewAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                    }
                }
            }
        }

    }

    private fun setUpAdapter(){
        binding.reviewRecyclerView.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun showLoading(){
        binding.userReviewProgressBar.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.userReviewProgressBar.visibility = View.GONE
    }

}