package com.example.cmpt362_finalproject.ui.transactions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cmpt362_finalproject.R


class SegmentedListAdapter(private val context: Context, private var currentEntryList: List<Any>) :
    BaseAdapter() {
    override fun getCount(): Int = currentEntryList.size

    override fun getItem(position: Int): Any = currentEntryList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentItem = getItem(position)


        //Header
        if (currentItem is Header) {
            val headerView = convertView ?: LayoutInflater.from(context).inflate(R.layout.transaction_list_header, parent, false)
            val textViewHeader = headerView.findViewById<TextView>(R.id.formatted_dateView) // Header ID
            textViewHeader.text = currentItem.title // Set the header title
            return headerView
        }

        //Regular item
        if (currentItem is Entry) {
            val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.transaction_list_item, parent, false)

            val storeName = itemView.findViewById<TextView>(R.id.store_name)
            val price = itemView.findViewById<TextView>(R.id.entry_item_price)

            storeName.text = currentItem.storeName
            price.text = currentItem.paid.toString().dropLast(2) + "." + currentItem.paid.toString().takeLast(2)


            return itemView
        }



        //Should not happen
        println("Something went wrong")
        return convertView ?: View(context)
    }

    fun replace(replacement: List<Any>){
        currentEntryList = replacement
    }


}
