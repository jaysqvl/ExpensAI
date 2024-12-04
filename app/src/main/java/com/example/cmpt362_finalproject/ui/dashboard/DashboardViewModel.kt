package com.example.cmpt362_finalproject.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cmpt362_finalproject.api.ApiClient
import com.example.cmpt362_finalproject.api.TextRequest
import com.example.cmpt362_finalproject.ui.transactions.Entry
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.launch

class DashboardViewModel @Inject constructor(
    purchaseRepository: PurchaseRepository
) : ViewModel() {

    val allPurchasesLiveData: LiveData<List<Entry>> = purchaseRepository.allPurchases.asLiveData()

    private val _totalBalance = MutableLiveData<String>()
    val totalBalance: LiveData<String> = _totalBalance

    private val _income = MutableLiveData<String>()
    val income: LiveData<String> = _income

    private val _spend = MutableLiveData<String>()
    val spend: LiveData<String> = _spend

    private val _monthlyLimit = MutableLiveData<String>()
    val monthlyLimit: LiveData<String> = _monthlyLimit

    private val _monthlyProgress = MutableLiveData<Int>()
    val monthlyProgress: LiveData<Int> = _monthlyProgress

    private val _summary = MutableLiveData<String>()
    val summary: LiveData<String> = _summary

    private val _lastRefreshTime = MutableLiveData<Long>()
    private var cachedSummary: String? = null

    init {
        loadDashboardData()
        checkAndRefreshSummary()
        
        // Observe transactions for updates
        allPurchasesLiveData.observeForever { transactions ->
            val totalSpent = transactions.sumOf { it.paid } / 100.0
            _spend.value = "$${String.format("%.2f", totalSpent)}"
            
            val monthlyCredits = _income.value?.replace("$", "")?.toDoubleOrNull() ?: 0.0
            val balance = monthlyCredits - totalSpent
            _totalBalance.value = "$${String.format("%.2f", balance)}"
        }
    }

    private fun loadDashboardData() {
        _totalBalance.value = "$0.00"
        _income.value = "$0.00"
        _spend.value = "$0.00"
        _monthlyLimit.value = "$1000.00"
        _monthlyProgress.value = 0
    }

    private fun checkAndRefreshSummary() {
        val lastRefresh = _lastRefreshTime.value ?: 0
        val currentTime = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000

        if (cachedSummary != null && currentTime - lastRefresh < oneDayInMillis) {
            _summary.value = cachedSummary
        } else {
            allPurchasesLiveData.value?.let { transactions ->
                if (transactions.isNotEmpty()) {
                    refreshSummary(transactions)
                }
            }
        }
    }

    fun refreshSummary(transactions: List<Entry>) {
        if (transactions.isEmpty()) {
            _summary.value = "No transactions to summarize"
            return
        }

        viewModelScope.launch {
            try {
                val transactionText = transactions.joinToString("\n") { entry ->
                    "Store: ${entry.storeName}, Amount: $${entry.paid / 100.0}, " +
                    "Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entry.dateTime * 1000))}"
                }

                val response = ApiClient.textService.getTextResponse(
                    TextRequest("summary", transactionText)
                )
                
                response.output.let { summary ->
                    _summary.value = summary
                    cachedSummary = summary
                    _lastRefreshTime.value = System.currentTimeMillis()
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error refreshing summary", e)
                _summary.value = "Error generating summary. Please try again later."
            }
        }
    }
}