package com.example.e_shop.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.example.e_shop.Digital_Store.Store
import com.example.e_shop.R
import com.example.e_shop.adapters.SearchListAdapter
import com.example.e_shop.data.Product
import com.example.e_shop.data.store_info
import com.example.e_shop.databinding.ActivitySearchBinding
import com.example.e_shop.util.constant
import com.example.e_shop.util.fireStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class search: Fragment(R.layout.activity_search) {

    private lateinit var binding: ActivitySearchBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displaySearchResults()

    }

    fun search(query: String) {

        val searchItems = mutableListOf<String>()
        // Search for store names
        fireStore.collection(constant.StoresCollection).whereGreaterThanOrEqualTo("s_name", query)
            .whereLessThanOrEqualTo("s_name", query + "\uf8ff")
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val store = document.toObject(store_info::class.java)
                    searchItems.add(store.s_name)
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }

        // Search for product names
        fireStore.collection(constant.ProductsCollections).whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff")
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    searchItems.add(document.toObject(Product::class.java).name)
                }
                val searchAdapter = SearchListAdapter(requireContext(), searchItems)
                searchAdapter.filter.filter(query)
                //empty the adapter
                binding.searchResults.adapter = searchAdapter
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    fun displaySearchResults() {

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                val updatedQuery = query!!.split(" ").joinToString(" ") { it.capitalize() }
                search(updatedQuery)
                binding.searchResults.visibility = View.VISIBLE
                if(query =="")
                    binding.searchResults.visibility = View.GONE

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val updatedText = newText!!.split(" ").joinToString(" ") { it.capitalize() }
                search(updatedText)
                binding.searchResults.visibility = View.VISIBLE
                if(newText =="")
                    binding.searchResults.visibility = View.GONE
                return false
            }

        })
    }
}