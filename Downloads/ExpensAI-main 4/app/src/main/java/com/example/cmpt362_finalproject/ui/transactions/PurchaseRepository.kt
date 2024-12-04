package com.example.cmpt362_finalproject.ui.transactions

import com.example.cmpt362_finalproject.manager.FirestoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PurchaseRepository(
    private val purchaseDatabaseDao: PurchaseDatabaseDAO,
    private val firestoreManager: FirestoreManager
) {
    val allPurchases: Flow<List<Entry>> = purchaseDatabaseDao.getAll()

    fun insert(entry: Entry) {
        CoroutineScope(IO).launch {
            purchaseDatabaseDao.insertEntry(entry)
            // Sync to Firestore
            firestoreManager.addTransaction(entry)
        }
    }

    fun delete(id: Long) {
        CoroutineScope(IO).launch {
            purchaseDatabaseDao.deleteID(id)
            // Sync deletion to Firestore
            firestoreManager.deleteTransaction(id)
        }
    }

    fun deleteAll() {
        CoroutineScope(IO).launch {
            purchaseDatabaseDao.deleteAll()
            // You might want to add a method to delete all in Firestore too
        }
    }

    // Initialize sync with Firestore
    fun startSync() {
        firestoreManager.listenToTransactions { entries ->
            CoroutineScope(IO).launch {
                entries.forEach { entry ->
                    purchaseDatabaseDao.insertEntry(entry)
                }
            }
        }
    }
}