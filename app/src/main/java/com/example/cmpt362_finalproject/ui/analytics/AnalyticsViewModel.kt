package com.example.cmpt362_finalproject.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import java.util.Calendar
import javax.inject.Inject

class AnalyticsViewModel @Inject constructor(
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {
    
    val allPurchasesLiveData = purchaseRepository.allPurchases.asLiveData()

    fun getDailySpending(): Map<Int, Double> {
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