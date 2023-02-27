package com.example.e_shop.Profile.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_shop.R
import com.example.e_shop.databinding.FragmentHelpCenterBinding
import com.example.e_shop.fragments.shopping.UserProfile

class HelpCenter(val parent: UserProfile) : Fragment() {

    private lateinit var binding: FragmentHelpCenterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpCenterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}