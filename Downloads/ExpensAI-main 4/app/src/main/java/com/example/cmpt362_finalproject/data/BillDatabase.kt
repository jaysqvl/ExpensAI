package com.example.cmpt362_finalproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BillEntity::class], version = 1, exportSchema = true)
abstract class BillDatabase : RoomDatabase() {
    abstract fun billDao(): BillDao

    companion object {
        @Volatile
        private var INSTANCE: BillDatabase? = null

        fun getDatabase(context: Context): BillDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BillDatabase::class.java,
                    "bill_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}