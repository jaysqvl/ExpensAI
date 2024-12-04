package com.example.cmpt362_finalproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserPreference::class], version = 1, exportSchema = false)
abstract class UserPreferenceDatabase : RoomDatabase() {
    abstract val userPreferenceDao: UserPreferenceDao

    companion object {
        @Volatile
        private var INSTANCE: UserPreferenceDatabase? = null

        fun getInstance(context: Context): UserPreferenceDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserPreferenceDatabase::class.java,
                        "user_preferences_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
} 