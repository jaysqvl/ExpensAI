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
    companion object {
        private const val ENABLE_FIRESTORE_SYNC = false
    }

    val allPurchases: Flow<List<Entry>> = purchaseDatabaseDao.getAll()

    fun insert(entry: Entry) {
        CoroutineScope(IO).launch {
            purchaseDatabaseDao.insertEntry(entry)
            if (ENABLE_FIRESTORE_SYNC) {
                firestoreManager.addTransaction(entry)
            }
        }
    }

    fun delete(id: Long) {
        CoroutineScope(IO).launch {
            purchaseDatabaseDao.deleteID(id)
            if (ENABLE_FIRESTORE_SYNC) {
                firestoreManager.deleteTransaction(id)
            }
        }
    }

    fun deleteAll() {
        CoroutineScope(IO).launch {
            purchaseDatabaseDao.deleteAll()
        }
    }

    fun startSync() {
        if (!ENABLE_FIRESTORE_SYNC) return
        firestoreManager.listenToTransactions { entries ->
            CoroutineScope(IO).launch {
                entries.forEach { entry ->
                    purchaseDatabaseDao.insertEntry(entry)
                }
            }
        }
    }
}