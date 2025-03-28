package com.example.cmpt362_finalproject.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.cmpt362_finalproject.data.UserPreferenceDatabase
import com.example.cmpt362_finalproject.data.UserPreferenceRepository
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.card.MaterialCardView
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.example.cmpt362_finalproject.ui.transactions.ManualTransactionActivity
import android.content.Intent
import android.app.Activity

class DashboardFragment : Fragment() {

    private lateinit var database: PurchaseDatabase
    private lateinit var databaseDao: PurchaseDatabaseDAO
    private lateinit var repository: PurchaseRepository
    private lateinit var viewModelFactory: DashboardViewModelFactory
    private lateinit var dashboardViewModel: DashboardViewModel

    // New UI components
    private lateinit var totalSpendingTextView: TextView
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var monthlySpendingChart: LineChart
    private lateinit var categoryBreakdownChart: PieChart
    
    // Added dynamically for balance/credits
    private lateinit var totalBalanceTextView: TextView
    private lateinit var monthlyCreditTextView: TextView
    private lateinit var spendTextView: TextView
    private lateinit var monthlyLimitTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize database components
        database = PurchaseDatabase.getInstance(requireActivity())
        databaseDao = database.commentDatabaseDao
        repository = PurchaseRepository(databaseDao, FirestoreManager())
        val userPrefDatabase = UserPreferenceDatabase.getInstance(requireActivity())
        val userPrefRepository = UserPreferenceRepository(userPrefDatabase.userPreferenceDao)
        viewModelFactory = DashboardViewModelFactory(repository, userPrefRepository)
        dashboardViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[DashboardViewModel::class.java]

        // Link the views to their IDs in the new layout
        transactionsRecyclerView = view.findViewById(R.id.recyclerViewTransactions)
        totalSpendingTextView = view.findViewById(R.id.textViewTotalSpending)
        monthlySpendingChart = view.findViewById(R.id.chartMonthlySpending)
        categoryBreakdownChart = view.findViewById(R.id.chartCategoryBreakdown)
        
        // Create a new card for balance information
        addBalanceCard(view)
        
        // Setup FAB for adding transactions
        val fabAddTransaction = view.findViewById<ExtendedFloatingActionButton>(R.id.fabAddTransaction)
        fabAddTransaction.setOnClickListener {
            val intent = Intent(requireContext(), ManualTransactionActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_TRANSACTION)
        }

        setupRecyclerView()
        observeViewModel()

        return view
    }
    
    private fun addBalanceCard(view: View) {
        // Find the root layout to add our card at the beginning
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewTransactions)
        val parent1 = recyclerView.parent as View
        val parent2 = parent1.parent as View
        val rootLayout = parent2.parent as ViewGroup
            
        // Create a new card with balance information
        val balanceCard = MaterialCardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 30)
            }
            elevation = 2f // Use a hardcoded value instead of undefined dimen
            radius = 16f // Use a hardcoded value instead of undefined dimen
        }
        
        // Create the card content
        val contentLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            setPadding(30, 30, 30, 30)
        }
        
        // Add title to the card
        val titleText = TextView(requireContext()).apply {
            text = "Account Balance"
            textSize = 20f
            setTextColor(resources.getColor(R.color.text_primary, null))
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 20)
            }
        }
        contentLayout.addView(titleText)
        
        // Add balance info
        totalBalanceTextView = TextView(requireContext()).apply {
            text = "$0.00"
            textSize = 24f
            setTextColor(resources.getColor(R.color.primary, null))
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 20)
            }
        }
        contentLayout.addView(totalBalanceTextView)
        
        // Create rows for other financial info
        val infoLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            weightSum = 3f
        }
        
        // Monthly credit column
        val creditLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }
        
        creditLayout.addView(TextView(requireContext()).apply {
            text = "Monthly Credit"
            textSize = 14f
            setTextColor(resources.getColor(R.color.text_secondary, null))
        })
        
        monthlyCreditTextView = TextView(requireContext()).apply {
            text = "$0.00"
            textSize = 16f
            setTextColor(resources.getColor(R.color.success, null))
        }
        creditLayout.addView(monthlyCreditTextView)
        infoLayout.addView(creditLayout)
        
        // Spending column
        val spendLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }
        
        spendLayout.addView(TextView(requireContext()).apply {
            text = "Spending"
            textSize = 14f
            setTextColor(resources.getColor(R.color.text_secondary, null))
        })
        
        spendTextView = TextView(requireContext()).apply {
            text = "$0.00"
            textSize = 16f
            setTextColor(resources.getColor(R.color.error, null))
        }
        spendLayout.addView(spendTextView)
        infoLayout.addView(spendLayout)
        
        // Limit column
        val limitLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }
        
        limitLayout.addView(TextView(requireContext()).apply {
            text = "Monthly Limit"
            textSize = 14f
            setTextColor(resources.getColor(R.color.text_secondary, null))
        })
        
        monthlyLimitTextView = TextView(requireContext()).apply {
            text = "$0.00"
            textSize = 16f
            setTextColor(resources.getColor(R.color.primary, null))
        }
        limitLayout.addView(monthlyLimitTextView)
        infoLayout.addView(limitLayout)
        
        contentLayout.addView(infoLayout)
        balanceCard.addView(contentLayout)
        
        // Add the card to the beginning of the layout
        (rootLayout as LinearLayout).addView(balanceCard, 0)
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

        // Observe and update the monthly credit
        dashboardViewModel.monthlyCredit.observe(viewLifecycleOwner) { credit ->
            monthlyCreditTextView.text = credit
        }

        // Observe and update the spend
        dashboardViewModel.spend.observe(viewLifecycleOwner, Observer { spend ->
            spendTextView.text = spend
            totalSpendingTextView.text = spend // Also update the spending in the Monthly Overview card
        })

        // Observe and update the monthly limit
        dashboardViewModel.monthlyLimit.observe(viewLifecycleOwner, Observer { limit ->
            monthlyLimitTextView.text = limit
        })

        // Observe and update the transactions list
        dashboardViewModel.allPurchasesLiveData.observe(viewLifecycleOwner) { entries ->
            transactionAdapter.updateData(entries)
        }

        // Set up the monthly spending chart
        dashboardViewModel.allPurchasesLiveData.observe(viewLifecycleOwner) { entries ->
            // Here you would process the data and update the chart
            // For example: setupMonthlySpendingChart(entries)
        }

        // Set up the category breakdown chart
        dashboardViewModel.allPurchasesLiveData.observe(viewLifecycleOwner) { entries ->
            // Here you would process the data and update the chart
            // For example: setupCategoryBreakdownChart(entries)
        }
    }
    
    // Chart setup methods would go here

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_TRANSACTION && resultCode == Activity.RESULT_OK) {
            // Transaction has been added directly to the database
            // Refresh data if needed
            refreshData()
        }
    }
    
    private fun refreshData() {
        // Request latest data from view model if needed
        dashboardViewModel.refreshData()
    }

    companion object {
        private const val REQUEST_CODE_ADD_TRANSACTION = 1
    }
}