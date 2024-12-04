package com.example.cmpt362_finalproject.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cmpt362_finalproject.data.UserPreferenceRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: UserPreferenceRepository
) : ViewModel() {
    
    val userPreferences = repository.userPreferences.asLiveData()
    
    fun updatePreferences(name: String, monthlyLimit: Double, savingsGoal: Double, spendingChallenge: Double) {
        viewModelScope.launch {
            val currentPrefs = userPreferences.value
            if (currentPrefs != null) {
                repository.updatePreferences(
                    currentPrefs.copy(
                        userName = name,
                        monthlyLimit = monthlyLimit,
                        savingsGoal = savingsGoal,
                        spendingChallenge = spendingChallenge
                    )
                )
            } else {
                // Initialize new preferences if none exist
                repository.initializePreferences(
                    monthlyLimit = monthlyLimit,
                    savingsGoal = savingsGoal,
                    spendingChallenge = spendingChallenge,
                    email = FirebaseAuth.getInstance().currentUser?.email ?: "",
                    userName = name
                )
            }
        }
    }
}

class SettingsViewModelFactory(
    private val repository: UserPreferenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 