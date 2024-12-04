package com.example.cmpt362_finalproject.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cmpt362_finalproject.data.BillEntity
import com.example.cmpt362_finalproject.data.BillModel
import com.example.cmpt362_finalproject.data.BillRepository
import com.example.cmpt362_finalproject.utils.toBillEntity
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: BillRepository) : ViewModel() {

    val bills = repository.getAllBills()

    fun insertBill(bill: BillModel) {
        viewModelScope.launch {
            repository.insertBill(bill.toBillEntity())
        }
    }

    fun deleteBill(bill: BillEntity) {
        viewModelScope.launch {
            repository.deleteBill(bill)
        }
    }
}

class HomeViewModelFactory(private val repository: BillRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}