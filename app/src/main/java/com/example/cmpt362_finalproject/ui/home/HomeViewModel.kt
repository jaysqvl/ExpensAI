package com.example.cmpt362_finalproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cmpt362_finalproject.data.UserPreferenceRepository
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    purchaseRepository: PurchaseRepository,
    userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {
    
    private val locale = Locale.getDefault()
    
    val userPreferences = userPreferenceRepository.userPreferences.asLiveData()
    private val allPurchases = purchaseRepository.allPurchases.asLiveData()

    private val _spendingChallengeProgress = MediatorLiveData<String>()
    val spendingChallengeProgress: LiveData<String> = _spendingChallengeProgress

    private val _dailySpending = MediatorLiveData<String>()
    val dailySpending: LiveData<String> = _dailySpending

    private val _weeklySpending = MediatorLiveData<String>()
    val weeklySpending: LiveData<String> = _weeklySpending

    private val _weeklyDateRange = MediatorLiveData<String>()
    val weeklyDateRange: LiveData<String> = _weeklyDateRange

    init {
        _spendingChallengeProgress.addSource(allPurchases) { updateSpendingChallenge() }
        _spendingChallengeProgress.addSource(userPreferences) { updateSpendingChallenge() }
        
        _dailySpending.addSource(allPurchases) { updateDailySpending() }
        _weeklySpending.addSource(allPurchases) { updateWeeklySpending() }
        _weeklyDateRange.addSource(allPurchases) { updateWeeklyDateRange() }
    }

    private fun updateSpendingChallenge() {
        val spent = allPurchases.value?.sumOf { it.paid } ?: 0
        val spentDouble = spent / 100.0
        val challenge = userPreferences.value?.spendingChallenge ?: 0.0
        
        _spendingChallengeProgress.value = "${String.format(locale, "%.2f", spentDouble)} / $${String.format(locale, "%.2f", challenge)}"
    }

    private fun updateDailySpending() {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val dailyTotal = allPurchases.value?.filter { entry ->
            calendar.timeInMillis = entry.dateTime * 1000
            calendar.get(Calendar.DAY_OF_MONTH) == today &&
            calendar.get(Calendar.MONTH) == currentMonth &&
            calendar.get(Calendar.YEAR) == currentYear
        }?.sumOf { it.paid } ?: 0

        val dailySpentDouble = dailyTotal / 100.0  // Convert cents to dollars
        _dailySpending.value = "$${String.format(locale, "%.2f", dailySpentDouble)}"
    }

    private fun updateWeeklySpending() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val currentWeek = calendar.get(Calendar.WEEK_OF_MONTH)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)

            val weeklyTotal = allPurchases.value?.filter { entry ->
                calendar.timeInMillis = entry.dateTime * 1000
                calendar.get(Calendar.WEEK_OF_MONTH) == currentWeek &&
                calendar.get(Calendar.MONTH) == currentMonth &&
                calendar.get(Calendar.YEAR) == currentYear
            }?.sumOf { it.paid } ?: 0

            _weeklySpending.value = "$${String.format(locale, "%.2f", weeklyTotal / 100.0)}"
        }
    }

    private fun updateWeeklyDateRange() {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_MONTH)
        
        // Get first day of current week
        calendar.set(Calendar.WEEK_OF_MONTH, currentWeek)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val weekStart = calendar.time

        // Get last day of current week
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val weekEnd = calendar.time

        val dateFormat = java.text.SimpleDateFormat("MMM dd", locale)
        _weeklyDateRange.value = "Weekly Spending (${dateFormat.format(weekStart)} - ${dateFormat.format(weekEnd)})"
    }
}

class HomeViewModelFactory(
    private val purchaseRepository: PurchaseRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(purchaseRepository, userPreferenceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}