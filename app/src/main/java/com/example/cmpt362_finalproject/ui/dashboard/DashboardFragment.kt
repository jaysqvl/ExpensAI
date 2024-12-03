package com.example.cmpt362_finalproject.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_finalproject.R
import com.example.cmpt362_finalproject.data.Transaction
import com.example.cmpt362_finalproject.ui.adapters.TransactionAdapter

class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModels()

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
        dashboardViewModel.recentTransactions.observe(viewLifecycleOwner, Observer { transactions ->
            transactionAdapter.updateData(transactions)
        })
    }
}