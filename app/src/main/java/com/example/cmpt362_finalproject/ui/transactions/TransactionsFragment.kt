package com.example.cmpt362_finalproject.ui.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.util.ArrayList
import com.example.cmpt362_finalproject.R
import java.text.SimpleDateFormat
import java.util.Date


class TransactionsFragment : Fragment() {
    private lateinit var listadapter: SegmentedListAdapter

    //Database variables
    private lateinit var database: PurchaseDatabase
    private lateinit var databaseDao: PurchaseDatabaseDAO
    private lateinit var repository: PurchaseRepository
    private lateinit var viewModelFactory: PurchaseViewModelFactory
    private lateinit var purchaseViewModel: PurchaseViewModel

    private lateinit var listView: ListView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        database = PurchaseDatabase.getInstance(requireActivity())
        databaseDao = database.commentDatabaseDao
        repository = PurchaseRepository(databaseDao)
        viewModelFactory = PurchaseViewModelFactory(repository)
        purchaseViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[PurchaseViewModel::class.java]

        listView = view.findViewById(R.id.transaction_listview)

        listadapter = SegmentedListAdapter(requireActivity(), ArrayList())
        listView.adapter = listadapter

        purchaseViewModel.allCommentsLiveData.observe(requireActivity(), Observer { it ->
            listadapter.replace(it)
            listadapter.notifyDataSetChanged()
        })

        val purchaseList1 = listOf(
            Purchase("Purchase", "Store A", -26.29, 1731787055),
            Purchase("Purchase", "Store B", -12.51, 1731787055),
            Purchase("Purchase", "Store C", -5.75, 1731687055)
        )



        // Get the ListView from the layout
        val listView: ListView = view.findViewById(R.id.transaction_listview)

        // Create and set the adapter
        //val adapter = SegmentedListAdapter(requireActivity(), purchaseList1)
        listView.adapter = listadapter

        // Get the ListView from the layout
        //val listView2: ListView = view.findViewById(R.id.transaction_listdemo2)

        // Create and set the adapter
        //val adapter2 = SegmentedListAdapter(requireActivity(), purchaseList2)
        //listView2.adapter = adapter2

        val tempButton = view.findViewById<Button>(R.id.temp_addEntriesButton)

        tempButton.setOnClickListener {
            // Handle the button click (e.g., show a toast message)
            Toast.makeText(requireActivity(), "Temp Entries added", Toast.LENGTH_SHORT).show()

            val entryList1 = listOf(
                Entry(143, "Store D", -1629, 1731687055),
                Entry(144, "Store E", -2252, 1731687055),
                Entry(145, "Store F", -875, 1731687055)
            )
            for (element in entryList1) {
                purchaseViewModel.insert(element)
            }
        }

        return view
    }

    inner class dateHolder {
        var dates = ArrayList<DatedList>()

        fun addEntry(entry: Entry) {
            //Date determining
            val date = Date(entry.dateTime * 1000)
            val year = SimpleDateFormat("yyyy").format(date).toInt()
            val month = SimpleDateFormat("MM").format(date).toInt()


        }


        inner class DatedList(private val set_date: Pair<Int, Int>, private val set_entry: Entry) {
            val date : Pair<Int, Int> = set_date //Year and month
            val entry : Entry = set_entry

        }

        //fun

    }
}

