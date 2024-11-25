package com.example.cmpt362_finalproject.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cmpt362_finalproject.data.Transaction

class DashboardViewModel : ViewModel() {

    // LiveData for total balance, income, spend, and monthly limit
    private val _totalBalance = MutableLiveData<String>()
    val totalBalance: LiveData<String> get() = _totalBalance

    private val _income = MutableLiveData<String>()
    val income: LiveData<String> get() = _income

    private val _spend = MutableLiveData<String>()
    val spend: LiveData<String> get() = _spend

    private val _monthlyLimit = MutableLiveData<String>()
    val monthlyLimit: LiveData<String> get() = _monthlyLimit

    private val _monthlyProgress = MutableLiveData<Int>() // Progress as percentage
    val monthlyProgress: LiveData<Int> get() = _monthlyProgress

    private val _recentTransactions = MutableLiveData<List<Transaction>>()
    val recentTransactions: LiveData<List<Transaction>> get() = _recentTransactions

    init {
        // Mock data for testing
        _totalBalance.value = "$8,000.49"
        _income.value = "$101,080.00"
        _spend.value = "$56,510.34"
        _monthlyLimit.value = "$3,000.00"
        _monthlyProgress.value = 70 // Example progress value

        _recentTransactions.value = listOf(
            Transaction("Grocery Store", "-$45.00", "25 Nov 2024"),
            Transaction("Gym Membership", "-$50.00", "24 Nov 2024"),
            Transaction("Electricity Bill", "-$80.00", "23 Nov 2024"),
            Transaction("Dining Out", "-$25.00", "22 Nov 2024")
        )
    }
}