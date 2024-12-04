package com.example.cmpt362_finalproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmpt362_finalproject.data.BillEntity
import com.example.cmpt362_finalproject.data.BillRepository
import kotlinx.coroutines.launch

class BillViewModel(private val repository: BillRepository) : ViewModel() {
    val allBills: LiveData<List<BillEntity>> = repository.getAllBills()

    fun insert(bill: BillEntity) = viewModelScope.launch {
        repository.insertBill(bill)
    }

    fun delete(bill: BillEntity) = viewModelScope.launch {
        repository.deleteBill(bill)
    }
}

