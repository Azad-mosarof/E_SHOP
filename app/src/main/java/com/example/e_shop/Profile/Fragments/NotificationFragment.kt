package com.example.e_shop.Profile.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_shop.adapters.NotificationAdapter
import com.example.e_shop.databinding.FragmentNotificationBinding
import com.example.e_shop.fragments.shopping.UserProfile
import com.example.e_shop.fragments.shopping.notificationViewmodel
import com.example.e_shop.util.Resource
import kotlinx.coroutines.flow.collectLatest

class NotificationFragment(val parent: UserProfile) : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private val notificationAdapter: NotificationAdapter by lazy { NotificationAdapter() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()

        lifecycleScope.launchWhenStarted {
            notificationViewmodel.notificationList.collectLatest {
                when(it){
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        notificationAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }
    }

    private fun setUpAdapter(){
        binding.notificationRecyclerView.apply {
            adapter = notificationAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }
}