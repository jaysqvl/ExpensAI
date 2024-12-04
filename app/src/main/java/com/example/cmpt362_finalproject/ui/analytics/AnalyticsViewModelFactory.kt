package com.example.cmpt362_finalproject.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository

class AnalyticsViewModelFactory(
    private val purchaseRepository: PurchaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalyticsViewModel(purchaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 