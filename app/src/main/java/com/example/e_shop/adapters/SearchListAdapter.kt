package com.example.e_shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.e_shop.R
import java.util.*

class SearchListAdapter(private val context: Context, private val itemList: List<String>) :
    BaseAdapter(), Filterable {

    private var filteredList: List<String> = itemList

    override fun getCount(): Int {
        return filteredList.size
    }

    override fun getItem(position: Int): Any {
        return filteredList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.search_list_items, null)
        }

        val item = filteredList[position]

        val textView = view!!.findViewById<TextView>(R.id.searchItem)
        textView.text = item

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = mutableListOf<String>()
                if (!constraint.isNullOrEmpty()) {
                    for (item in itemList) {
                        if (item.toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
                            filteredResults.add(item)
                        }
                    }
                } else {
                    filteredResults.addAll(itemList)
                }
                val results = FilterResults()
                results.values = filteredResults
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<String>
                notifyDataSetChanged()
            }
        }
    }
}

