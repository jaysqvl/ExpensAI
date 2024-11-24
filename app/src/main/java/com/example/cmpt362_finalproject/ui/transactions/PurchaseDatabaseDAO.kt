package com.example.cmpt362_finalproject.ui.transactions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface PurchaseDatabaseDAO {

    @Insert
    suspend fun insertEntry(entry: Entry)

    @Query("SELECT * FROM purchase_table")
    fun getAll(): Flow<List<Entry>>

    @Query("DELETE FROM purchase_table")
    suspend fun deleteAll()

    @Query("DELETE FROM purchase_table WHERE id = :key")
    suspend fun deleteID(key: Long)
}