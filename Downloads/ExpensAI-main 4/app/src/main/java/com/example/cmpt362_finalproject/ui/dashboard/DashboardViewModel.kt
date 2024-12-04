package com.example.cmpt362_finalproject.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.cmpt362_finalproject.ui.transactions.Entry
import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import javax.inject.Inject

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

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        _totalBalance.value = "$0.00"
        _income.value = "$0.00"
        _spend.value = "$0.00"
        _monthlyLimit.value = "$1000.00"
        _monthlyProgress.value = 0
    }
}