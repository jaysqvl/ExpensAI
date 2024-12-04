package com.example.cmpt362_finalproject.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY dueDate ASC")
    fun getAllBills(): LiveData<List<BillEntity>> // Make sure the return type matches Room's expectations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillEntity)

    @Delete
    suspend fun deleteBill(bill: BillEntity)
}