package com.example.cmpt362_finalproject.ui.transactions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PurchaseRepository(private val purchaseDatabaseDao: PurchaseDatabaseDAO) {
    val allPurchases: Flow<List<Entry>> = purchaseDatabaseDao.getAll()

    fun insert(entry: Entry){
        CoroutineScope(IO).launch{
            purchaseDatabaseDao.insertEntry(entry)
        }
    }

    fun delete(id: Long){
        CoroutineScope(IO).launch {
            purchaseDatabaseDao.deleteID(id)
        }
    }

    fun deleteAll(){
        CoroutineScope(IO).launch {
            purchaseDatabaseDao.deleteAll()
        }
    }
}