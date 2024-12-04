package com.example.cmpt362_finalproject.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cmpt362_finalproject.api.ApiClient
import com.example.cmpt362_finalproject.api.TextRequest
import com.example.cmpt362_finalproject.data.UserPreferenceRepository
import com.example.cmpt362_finalproject.ui.transactions.Entry
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

class DashboardViewModel @Inject constructor(
    purchaseRepository: PurchaseRepository,
    userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    val allPurchasesLiveData = purchaseRepository.allPurchases.map { purchases ->
        purchases.sortedByDescending { it.dateTime }
    }.asLiveData()
    private val userPreferences = userPreferenceRepository.userPreferences.asLiveData()

    private val _totalBalance = MutableLiveData<String>()
    val totalBalance: LiveData<String> = _totalBalance

    private val _monthlyCredit = MutableLiveData<String>()
    val monthlyCredit: LiveData<String> = _monthlyCredit

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

    private val _monthlyProgressDetails = MutableLiveData<String>()
    val monthlyProgressDetails: LiveData<String> = _monthlyProgressDetails

    init {
        loadDashboardData()

        userPreferences.observeForever { preferences ->
            preferences?.let {
                _monthlyCredit.value = "$${String.format("%.2f", it.monthlyLimit)}"
                _monthlyLimit.value = "$${String.format("%.2f", it.monthlyLimit)}"
                updateTotalBalance()
                updateMonthlyProgress()
            } ?: run {
                _monthlyCredit.value = "$0.00"
                _monthlyLimit.value = "$0.00"
                updateTotalBalance()
                updateMonthlyProgress()
            }
        }
        
        allPurchasesLiveData.observeForever { transactions ->
            val totalSpent = transactions.sumOf { it.paid } / 100.0
            _spend.value = "$${String.format("%.2f", totalSpent)}"
            updateTotalBalance()
            updateMonthlyProgress()
        }
    }

    private fun updateMonthlyProgress() {
        val spent = _spend.value?.replace("$", "")?.toDoubleOrNull() ?: 0.0
        val limit = _monthlyLimit.value?.replace("$", "")?.toDoubleOrNull() ?: 1.0
        _monthlyProgress.value = ((spent / limit) * 100).toInt().coerceIn(0, 100)
        _monthlyProgressDetails.value = "${String.format("%.2f", spent)} / ${String.format("%.2f", limit)}"
    }

    private fun updateTotalBalance() {
        val credit = _monthlyCredit.value?.replace("$", "")?.toDoubleOrNull() ?: 0.0
        val spent = _spend.value?.replace("$", "")?.toDoubleOrNull() ?: 0.0
        val balance = credit - spent
        _totalBalance.value = "$${String.format("%.2f", balance)}"
    }

    private fun loadDashboardData() {
        _totalBalance.value = "$0.00"
        _monthlyCredit.value = "$0.00"
        _spend.value = "$0.00"
        _monthlyLimit.value = "$0.00"
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