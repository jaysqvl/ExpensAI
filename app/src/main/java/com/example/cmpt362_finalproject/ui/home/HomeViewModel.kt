package com.example.cmpt362_finalproject.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.cmpt362_finalproject.data.UserPreferenceRepository

class HomeViewModel(
    repository: UserPreferenceRepository
) : ViewModel() {
    
    val userPreferences = repository.userPreferences.asLiveData()
}

class HomeViewModelFactory(
    private val repository: UserPreferenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}