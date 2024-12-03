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

class TransactionSyncService @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    
    fun startSync() {
        auth.currentUser?.let { user ->
            scope.launch {
                // Listen for local changes
                purchaseRepository.allPurchases.collect { entries ->
                    syncToFirestore(entries, user.uid)
                }
                
                // Listen for remote changes
                listenToFirestore(user.uid)
            }
        }
    }
    
    private suspend fun syncToFirestore(entries: List<Entry>, userId: String) {
        entries.forEach { entry ->
            firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .document(entry.id.toString())
                .set(entry)
        }
    }
    
    private fun listenToFirestore(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("transactions")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                
                snapshot?.documentChanges?.forEach { change ->
                    when (change.type) {
                        DocumentChange.Type.ADDED,
                        DocumentChange.Type.MODIFIED -> {
                            val entry = change.document.toObject(Entry::class.java)
                            scope.launch {
                                purchaseRepository.insert(entry)
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            val entry = change.document.toObject(Entry::class.java)
                            scope.launch {
                                purchaseRepository.delete(entry.id)
                            }
                        }
                    }
                }
            }
    }
}