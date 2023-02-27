package com.example.e_shop.Digital_Store.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_shop.Digital_Store.Adapters.NewOrdersAdapter
import com.example.e_shop.Digital_Store.Viewmodels.NewOrdersViewModel
import com.example.e_shop.Digital_Store.factory.NewOrdersFactory
import com.example.e_shop.Digital_Store.storeId
import com.example.e_shop.R
import com.example.e_shop.databinding.FragmentNewOrdersBinding
import com.example.e_shop.util.Resource
import com.example.e_shop.util.Utility
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class New_Orders : Fragment() {

    private lateinit var binding: FragmentNewOrdersBinding
    private lateinit var newOrdersAdapter: NewOrdersAdapter
    val newOrderViewModel by viewModels<NewOrdersViewModel> {
        NewOrdersFactory(storeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newOrdersAdapter = NewOrdersAdapter()
        binding.newOrdersRv.apply {
            adapter = newOrdersAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        lifecycleScope.launch {
            newOrderViewModel.orders.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        Utility().showLoading(binding.newOrdersProgressBar)
                    }
                    is Resource.Success -> {
                        newOrdersAdapter.differ.submitList(it.data)
                        Utility().hideLoading(binding.newOrdersProgressBar)
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        Utility().hideLoading(binding.newOrdersProgressBar)
                    }
                }
            }
        }
    }

}