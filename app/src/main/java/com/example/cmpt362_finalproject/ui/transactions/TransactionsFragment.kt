package com.example.cmpt362_finalproject.ui.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import java.util.ArrayList
import com.example.cmpt362_finalproject.R


class TransactionsFragment : Fragment() {
    private lateinit var listadapter: SegmentedListAdapter

    private lateinit var listView: ListView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        listView = view.findViewById(R.id.transaction_list)

        listadapter = SegmentedListAdapter(requireActivity(), ArrayList())
        listView.adapter = listadapter

        val purchaseList1 = listOf(
            Purchase("Purchase", "Store A", -26.29),
            Purchase("Purchase", "Store B", -12.51),
            Purchase("Purchase", "Store C", -5.75)
        )

        val purchaseList2 = listOf(
            Purchase("Purchase", "Store D", -16.29),
            Purchase("Purchase", "Store E", -22.52),
            Purchase("Purchase", "Store F", -8.75)
        )

        // Get the ListView from the layout
        val listView: ListView = view.findViewById(R.id.transaction_list)

        // Create and set the adapter
        val adapter = SegmentedListAdapter(requireActivity(), purchaseList1)
        listView.adapter = adapter

        // Get the ListView from the layout
        val listView2: ListView = view.findViewById(R.id.transaction_listdemo2)

        // Create and set the adapter
        val adapter2 = SegmentedListAdapter(requireActivity(), purchaseList2)
        listView2.adapter = adapter2

        return view
    }
}

