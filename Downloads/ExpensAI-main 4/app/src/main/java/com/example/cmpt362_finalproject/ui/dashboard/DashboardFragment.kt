package com.example.cmpt362_finalproject.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.manager.FirestoreManager
import com.example.cmpt362_finalproject.ui.adapters.TransactionAdapter
import com.example.cmpt362_finalproject.ui.transactions.PurchaseDatabase
import com.example.cmpt362_finalproject.ui.transactions.PurchaseDatabaseDAO
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository

class DashboardFragment : Fragment() {

    private lateinit var database: PurchaseDatabase
    private lateinit var databaseDao: PurchaseDatabaseDAO
    private lateinit var repository: PurchaseRepository
    private lateinit var viewModelFactory: DashboardViewModelFactory
    private lateinit var dashboardViewModel: DashboardViewModel

    // Correct variable names for UI components
    private lateinit var totalBalanceTextView: TextView
    private lateinit var incomeTextView: TextView
    private lateinit var spendTextView: TextView
    private lateinit var monthlyLimitTextView: TextView
    private lateinit var monthlyProgressBar: ProgressBar
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize database components
        database = PurchaseDatabase.getInstance(requireActivity())
        databaseDao = database.commentDatabaseDao
        repository = PurchaseRepository(databaseDao, FirestoreManager())
        viewModelFactory = DashboardViewModelFactory(repository)
        dashboardViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[DashboardViewModel::class.java]

        // Link the views to their IDs in the layout
        totalBalanceTextView = view.findViewById(R.id.totalBalanceValue)
        incomeTextView = view.findViewById(R.id.incomeValue)
        spendTextView = view.findViewById(R.id.spendValue)
        monthlyLimitTextView = view.findViewById(R.id.monthlyLimitValue)
        monthlyProgressBar = view.findViewById(R.id.monthlyProgress)
        transactionsRecyclerView = view.findViewById(R.id.recyclerViewTransactions)

        setupRecyclerView()
        observeViewModel()

        return view
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList()) // Initialize adapter with empty data
        transactionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        transactionsRecyclerView.adapter = transactionAdapter
    }

    private fun observeViewModel() {
        // Observe and update the total balance
        dashboardViewModel.totalBalance.observe(viewLifecycleOwner, Observer { balance ->
            totalBalanceTextView.text = balance
        })

        // Observe and update the income
        dashboardViewModel.income.observe(viewLifecycleOwner, Observer { income ->
            incomeTextView.text = income
        })

        // Observe and update the spend
        dashboardViewModel.spend.observe(viewLifecycleOwner, Observer { spend ->
            spendTextView.text = spend
        })

        // Observe and update the monthly limit
        dashboardViewModel.monthlyLimit.observe(viewLifecycleOwner, Observer { limit ->
            monthlyLimitTextView.text = limit
        })

        // Observe and update the progress bar
        dashboardViewModel.monthlyProgress.observe(viewLifecycleOwner, Observer { progress ->
            monthlyProgressBar.progress = progress
        })

        // Observe and update the transactions list
        dashboardViewModel.allPurchasesLiveData.observe(viewLifecycleOwner) { entries ->
            transactionAdapter.updateData(entries)
        }
    }
}