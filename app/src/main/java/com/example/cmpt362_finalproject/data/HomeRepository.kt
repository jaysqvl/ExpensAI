package com.example.cmpt362_finalproject.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HomeRepository {

    // Mock data sources
    private val recentActivities = listOf(
        RecentActivity("Grocery Store", "-$45.00", "25 Nov 2024"),
        RecentActivity("Gym Membership", "-$50.00", "24 Nov 2024"),
        RecentActivity("Electricity Bill", "-$80.00", "23 Nov 2024"),
        RecentActivity("Dining Out", "-$25.00", "22 Nov 2024")
    )

    private val dailySpending = "$50.00"
    private val weeklySpending = "$250.00"
    private val savingsGoal = "$1,500 / $5,000"
    private val challenge = "$200 Left"

    // Functions to fetch data
    fun getRecentActivities(): LiveData<List<RecentActivity>> {
        val liveData = MutableLiveData<List<RecentActivity>>()
        liveData.value = recentActivities
        return liveData
    }

    fun getDailySpending(): LiveData<String> {
        val liveData = MutableLiveData<String>()
        liveData.value = dailySpending
        return liveData
    }

    fun getWeeklySpending(): LiveData<String> {
        val liveData = MutableLiveData<String>()
        liveData.value = weeklySpending
        return liveData
    }

    fun getSavingsGoal(): LiveData<String> {
        val liveData = MutableLiveData<String>()
        liveData.value = savingsGoal
        return liveData
    }

    fun getChallenge(): LiveData<String> {
        val liveData = MutableLiveData<String>()
        liveData.value = challenge
        return liveData
    }
}