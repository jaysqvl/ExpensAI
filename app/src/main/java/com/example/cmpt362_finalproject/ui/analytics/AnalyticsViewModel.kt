package com.example.cmpt362_finalproject.ui.analytics

import android.util.Log
import androidx.lifecycle.*
import com.example.cmpt362_finalproject.data.UserPreferenceRepository
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import java.util.Calendar
import javax.inject.Inject

class AnalyticsViewModel @Inject constructor(
    purchaseRepository: PurchaseRepository,
    userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {
    
    val allPurchasesLiveData = purchaseRepository.allPurchases.asLiveData()
    val monthlyLimitLiveData = userPreferenceRepository.getMonthlyLimit().asLiveData()

    private val _spendingData = MediatorLiveData<Pair<Map<Int, Double>, Double>>()
    val spendingData: LiveData<Pair<Map<Int, Double>, Double>> = _spendingData

    init {
        _spendingData.addSource(allPurchasesLiveData) { updateSpendingData() }
        _spendingData.addSource(monthlyLimitLiveData) { updateSpendingData() }
    }

    private fun updateSpendingData() {
        val dailySpending = getDailySpending()
        val monthlyLimit = monthlyLimitLiveData.value ?: 0.0
        Log.d("AnalyticsViewModel", "Daily spending: $dailySpending")
        Log.d("AnalyticsViewModel", "Monthly limit: $monthlyLimit")
        _spendingData.value = Pair(dailySpending, monthlyLimit)
    }

    private fun getDailySpending(): Map<Int, Double> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        return allPurchasesLiveData.value?.filter { entry ->
            calendar.timeInMillis = entry.dateTime * 1000
            calendar.get(Calendar.MONTH) == currentMonth &&
            calendar.get(Calendar.YEAR) == currentYear
        }?.groupBy { entry ->
            calendar.timeInMillis = entry.dateTime * 1000
            calendar.get(Calendar.DAY_OF_MONTH)
        }?.mapValues { (_, entries) ->
            entries.sumOf { it.paid / 100.0 }
        } ?: emptyMap()
    }
} 