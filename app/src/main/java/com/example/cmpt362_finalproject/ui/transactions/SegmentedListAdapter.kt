package com.example.cmpt362_finalproject.ui.transactions

import android.R.attr.type
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_finalproject.R
import java.text.SimpleDateFormat
import java.util.Date


class SegmentedListAdapter(private val context: Context, private var currentEntryList: List<Purchase>) :
    BaseAdapter() {



    override fun getCount(): Int = currentEntryList.size

    override fun getItem(position: Int): Any = currentEntryList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.transaction_list_item, parent, false)

        var dates = ArrayList<Pair<Int, Int>>()

        val sorted_list = currentEntryList.sortedBy { it.date }

        //val adapter: RecyclerView.Adapter = sections.get(section)
        //val size: Int = adapter.getCount() + 1


        // check if position inside this section
        //if (position === 0) return TYPE_SECTION_HEADER
        //if (position < size) return type + adapter.getItemViewType(position - 1)


        for (entry in currentEntryList) {
            //entry.date is a unix time
            val date = Date(entry.date * 1000)
            val year = SimpleDateFormat("yyyy").format(date).toInt()
            val month = SimpleDateFormat("MM").format(date).toInt()

            //check if its already in
            if (Pair(year, month) !in dates) {
                dates.add(Pair(year, month))
            }
        }



        val purchase = currentEntryList[position]

        val storeNameTextView: TextView = view.findViewById(R.id.store_name)
        //val amountTextView: TextView = view.findViewById(R.id.entry_item_price)

        //storeNameTextView.text = purchase.storeName
        //amountTextView.text = purchase.amount.toString()

        //return view
        return view
    }

    inner class dateHolder {
        var dates = ArrayList<DatedList>()


        inner class DatedList(val set_date: Pair<Int, Int>) {
            val date : Pair<Int, Int> = set_date
            //val  : a

        }

        //fun

    }

}