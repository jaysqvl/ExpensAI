package com.example.cmpt362_finalproject.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferenceRepository @Inject constructor(
    private val userPreferenceDao: UserPreferenceDao
) {
    val userPreferences: Flow<UserPreference> = userPreferenceDao.getUserPreferences()
    
    fun getMonthlyLimit(): Flow<Double> = userPreferenceDao.getMonthlyLimit()
    
    fun getSavingsGoal(): Flow<Double> = userPreferenceDao.getSavingsGoal()
    
    fun getSpendingChallenge(): Flow<Double> = userPreferenceDao.getSpendingChallenge()
    
    suspend fun updatePreferences(preferences: UserPreference) {
        userPreferenceDao.updatePreferences(preferences)
    }
    
    suspend fun initializePreferences(
        monthlyLimit: Double,
        savingsGoal: Double,
        spendingChallenge: Double,
        email: String,
        userName: String
    ) {
        val preferences = UserPreference(
            monthlyLimit = monthlyLimit,
            savingsGoal = savingsGoal,
            spendingChallenge = spendingChallenge,
            userEmail = email,
            userName = userName
        )
        userPreferenceDao.updatePreferences(preferences)
    }
} 