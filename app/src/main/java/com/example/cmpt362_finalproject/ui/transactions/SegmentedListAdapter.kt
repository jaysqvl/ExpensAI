package com.example.cmpt362_finalproject.ui.transactions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cmpt362_finalproject.R

class SegmentedListAdapter(private val context: Context, private var currentEntryList: List<Purchase>) :
    BaseAdapter() {



    override fun getCount(): Int = currentEntryList.size

    override fun getItem(position: Int): Any = currentEntryList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.transaction_list_item, parent, false)

        val purchase = currentEntryList[position]

        val storeNameTextView: TextView = view.findViewById(R.id.store_name)
        val amountTextView: TextView = view.findViewById(R.id.entry_item_price)

        storeNameTextView.text = purchase.storeName
        amountTextView.text = purchase.amount.toString()

        return view
    }
}