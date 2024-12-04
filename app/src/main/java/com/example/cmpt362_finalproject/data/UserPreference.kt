package com.example.cmpt362_finalproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreference(
    @PrimaryKey val id: Int = 1,
    val monthlyLimit: Double = 1000.0,
    val savingsGoal: Double = 0.0,
    val spendingChallenge: Double = 0.0,
    val userName: String = "",
    val userEmail: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
) 