package com.example.e_shop.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_shop.R
import com.example.e_shop.adapters.BestDealsAdapter
import com.example.e_shop.adapters.BestProductsAdapter
import com.example.e_shop.databinding.BaseCategoryFragmentBinding

open class BaseCategory: Fragment(R.layout.base_category_fragment) {

    private lateinit var binding: BaseCategoryFragmentBinding
    protected val bestDealsAdapter: BestDealsAdapter by lazy { BestDealsAdapter(requireContext()) }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BaseCategoryFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBestDealsRv()
        setUpBestProductRv()

        binding.bestDeals.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(!recyclerView.canScrollVertically(1) && dx != 0){
                    onOfferPagingRequest()
                }
            }
        })

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{v, _, scrollY, _, _ ->
            if(v.getChildAt(0).bottom <= v.height + scrollY){
                onBestProductPagingRequest()
            }
        })
    }

    open fun onOfferPagingRequest(){

    }

    open fun onBestProductPagingRequest(){

    }

    private fun setUpBestProductRv() {
        binding.bestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2,LinearLayoutManager.VERTICAL,false)
            adapter = bestProductsAdapter
        }
    }

    private fun setUpBestDealsRv() {
        binding.bestDeals.apply {
            layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,false)
            adapter = bestDealsAdapter
        }
    }
}