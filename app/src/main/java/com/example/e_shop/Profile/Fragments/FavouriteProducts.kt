package com.example.e_shop.Profile.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_shop.adapters.FavouriteProductsAdapter
import com.example.e_shop.databinding.FragmentFavouriteProductsBinding
import com.example.e_shop.fragments.shopping.UserProfile
import com.example.e_shop.util.userProfileCreateState
import com.example.e_shop.viewModels.FavouriteProductsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


class FavouriteProducts(val parentFragment: UserProfile) : Fragment() {

    private lateinit var binding: FragmentFavouriteProductsBinding
    private lateinit var favouriteProductAdapter: FavouriteProductsAdapter
    private lateinit var favViewModel: FavouriteProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteProductsBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favViewModel = ViewModelProvider(parentFragment)[FavouriteProductsViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouriteProductAdapter = FavouriteProductsAdapter(requireContext())
        binding.favouriteProductRecyclerView.apply {
            adapter = favouriteProductAdapter
            layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
        }

        if(userProfileCreateState){
            lifecycleScope.launchWhenStarted {
                binding.favProgressBar.visibility = View.VISIBLE
                delay(2000).apply {
                    favViewModel.favouriteProducts.collectLatest {
                        when(it){
                            is com.example.e_shop.util.Resource.Loading -> {
                                binding.favProgressBar.visibility = View.VISIBLE
                            }
                            is com.example.e_shop.util.Resource.Success -> {
                                favouriteProductAdapter.differ.submitList(it.data)
                                binding.favProgressBar.visibility = View.GONE
                            }
                            is com.example.e_shop.util.Resource.Error -> {
                                binding.favProgressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            userProfileCreateState = false
        }else{
            lifecycleScope.launchWhenStarted {
                favViewModel.favouriteProducts.collectLatest {
                    when(it){
                        is com.example.e_shop.util.Resource.Loading -> {
                            binding.favProgressBar.visibility = View.VISIBLE
                        }
                        is com.example.e_shop.util.Resource.Success -> {
                            favouriteProductAdapter.differ.submitList(it.data)
                            binding.favProgressBar.visibility = View.GONE
                        }
                        is com.example.e_shop.util.Resource.Error -> {
                            binding.favProgressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

}