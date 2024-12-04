package com.example.cmpt362_finalproject.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_finalproject.manager.TransactionManager

class CameraViewModelFactory(
    private val transactionManager: TransactionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(transactionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 