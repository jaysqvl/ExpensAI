package com.example.cmpt362_finalproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "user_preferences")
@TypeConverters(CategoryConverter::class)
data class UserPreference(
    @PrimaryKey val id: Int = 1,
    val monthlyLimit: Double = 1000.0,
    val savingsGoal: Double = 0.0,
    val spendingChallenge: Double = 0.0,
    val userName: String = "",
    val userEmail: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val categories: List<String> = emptyList()
)

class CategoryConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun toString(categories: List<String>): String {
        return categories.joinToString(",")
    }
} 