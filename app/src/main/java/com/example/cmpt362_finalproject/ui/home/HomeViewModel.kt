package com.example.cmpt362_finalproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.cmpt362_finalproject.data.HomeRepository
import com.example.cmpt362_finalproject.data.RecentActivity

class HomeViewModel : ViewModel() {

    private val repository = HomeRepository()

    val recentActivities: LiveData<List<RecentActivity>> = repository.getRecentActivities()
    val dailySpending: LiveData<String> = repository.getDailySpending()
    val weeklySpending: LiveData<String> = repository.getWeeklySpending()
    val savingsGoal: LiveData<String> = repository.getSavingsGoal()
    val challenge: LiveData<String> = repository.getChallenge()
}