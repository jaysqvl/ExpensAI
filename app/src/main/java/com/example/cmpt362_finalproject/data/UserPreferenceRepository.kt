package com.example.cmpt362_finalproject.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Suppress("unused")
class UserPreferenceRepository @Inject constructor(
    private val userPreferenceDao: UserPreferenceDao
) {
    val userPreferences: Flow<UserPreference> = userPreferenceDao.getUserPreferences()
    
    fun getMonthlyLimit(): Flow<Double> = userPreferenceDao.getMonthlyLimit()
    
    fun getSavingsGoal(): Flow<Double> = userPreferenceDao.getSavingsGoal()
    
    fun getSpendingChallenge(): Flow<Double> = userPreferenceDao.getSpendingChallenge()
    
    suspend fun updatePreferences(preferences: UserPreference) {
        userPreferenceDao.updatePreferences(
            preferences.copy(lastUpdated = System.currentTimeMillis())
        )
    }

    suspend fun updateCategories(categories: List<String>) {
        val currentPrefs = userPreferenceDao.getUserPreferencesSync()
        currentPrefs?.let {
            userPreferenceDao.updatePreferences(
                it.copy(
                    categories = categories,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }
    
    suspend fun initializePreferences(
        monthlyLimit: Double,
        savingsGoal: Double,
        spendingChallenge: Double,
        email: String,
        userName: String,
        categories: List<String> = emptyList()
    ) {
        val preferences = UserPreference(
            monthlyLimit = monthlyLimit,
            savingsGoal = savingsGoal,
            spendingChallenge = spendingChallenge,
            userEmail = email,
            userName = userName,
            categories = categories,
            lastUpdated = System.currentTimeMillis()
        )
        userPreferenceDao.updatePreferences(preferences)
    }
} 