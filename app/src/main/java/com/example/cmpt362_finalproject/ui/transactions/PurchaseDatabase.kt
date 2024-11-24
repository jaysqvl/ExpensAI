package com.example.cmpt362_finalproject.ui.transactions

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Entry::class], version = 1)
abstract class PurchaseDatabase: RoomDatabase() {

    abstract val commentDatabaseDao: PurchaseDatabaseDAO //Every database has a DAO
    companion object{
        //The Volatile keyword guarantees visibility of changes to the INSTANCE variable across threads
        @Volatile
        private var INSTANCE: PurchaseDatabase? = null

        fun getInstance(context: Context) : PurchaseDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        PurchaseDatabase::class.java, "purchase_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

