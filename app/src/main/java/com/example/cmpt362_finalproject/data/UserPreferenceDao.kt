package com.example.cmpt362_finalproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getUserPreferences(): Flow<UserPreference>

    @Query("SELECT * FROM user_preferences WHERE id = 1")
    suspend fun getUserPreferencesSync(): UserPreference?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePreferences(preferences: UserPreference)

    @Query("SELECT monthlyLimit FROM user_preferences WHERE id = 1")
    fun getMonthlyLimit(): Flow<Double>

    @Query("SELECT savingsGoal FROM user_preferences WHERE id = 1")
    fun getSavingsGoal(): Flow<Double>

    @Query("SELECT spendingChallenge FROM user_preferences WHERE id = 1")
    fun getSpendingChallenge(): Flow<Double>
} 