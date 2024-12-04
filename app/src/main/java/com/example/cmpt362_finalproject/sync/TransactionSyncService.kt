package com.example.cmpt362_finalproject.sync

import com.example.cmpt362_finalproject.ui.transactions.PurchaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.cmpt362_finalproject.ui.transactions.Entry
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import android.util.Log
import kotlinx.coroutines.SupervisorJob

class TransactionSyncService @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private var syncJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    companion object {
        private const val ENABLE_FIRESTORE_SYNC = false // Feature flag
    }
    
    fun startSync() {
        if (!ENABLE_FIRESTORE_SYNC) {
            Log.d("TransactionSyncService", "Firestore sync is disabled")
            return
        }

        syncJob?.cancel()
        
        auth.currentUser?.let { user ->
            syncJob = scope.launch {
                try {
                    launch(Dispatchers.IO + SupervisorJob()) {
                        try {
                            purchaseRepository.allPurchases.collect { entries ->
                                try {
                                    syncToFirestore(entries, user.uid)
                                } catch (e: Exception) {
                                    Log.e("TransactionSyncService", "Error in local sync", e)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("TransactionSyncService", "Error collecting local changes", e)
                        }
                    }
                    
                    launch(Dispatchers.IO + SupervisorJob()) {
                        try {
                            listenToFirestore(user.uid)
                        } catch (e: Exception) {
                            Log.e("TransactionSyncService", "Error in remote sync", e)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("TransactionSyncService", "Error in sync initialization", e)
                }
            }
        } ?: run {
            Log.e("TransactionSyncService", "No authenticated user found")
        }
    }

    fun stopSync() {
        if (!ENABLE_FIRESTORE_SYNC) return
        
        try {
            syncJob?.cancel()
            syncJob = null
        } catch (e: Exception) {
            Log.e("TransactionSyncService", "Error stopping sync", e)
        }
    }

    private suspend fun syncToFirestore(entries: List<Entry>, userId: String) {
        if (!ENABLE_FIRESTORE_SYNC) return
        try {
            coroutineScope {
                entries.map { entry ->
                    async(Dispatchers.IO + SupervisorJob()) {
                        try {
                            firestore.collection("users")
                                .document(userId)
                                .collection("transactions")
                                .document(entry.id.toString())
                                .set(entry)
                                .await()
                        } catch (e: Exception) {
                            Log.e("TransactionSyncService", "Error syncing entry ${entry.id}", e)
                        }
                    }
                }.awaitAll()
            }
        } catch (e: Exception) {
            Log.e("TransactionSyncService", "Error in syncToFirestore", e)
        }
    }

    private fun listenToFirestore(userId: String) {
        if (!ENABLE_FIRESTORE_SYNC) return
        try {
            firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("TransactionSyncService", "Listen failed", e)
                        return@addSnapshotListener
                    }

                    snapshot?.documentChanges?.forEach { change ->
                        try {
                            when (change.type) {
                                DocumentChange.Type.ADDED,
                                DocumentChange.Type.MODIFIED -> {
                                    val entry = change.document.toObject(Entry::class.java)
                                    scope.launch(Dispatchers.IO + SupervisorJob()) {
                                        purchaseRepository.insert(entry)
                                    }
                                }
                                DocumentChange.Type.REMOVED -> {
                                    val entry = change.document.toObject(Entry::class.java)
                                    scope.launch(Dispatchers.IO + SupervisorJob()) {
                                        purchaseRepository.delete(entry.id)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("TransactionSyncService", "Error processing change", e)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("TransactionSyncService", "Error setting up Firestore listener", e)
        }
    }
}