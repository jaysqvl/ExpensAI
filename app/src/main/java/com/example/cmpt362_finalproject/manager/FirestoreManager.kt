package com.example.cmpt362_finalproject.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.cmpt362_finalproject.ui.transactions.Entry
import kotlinx.coroutines.tasks.await

class FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Add a single transaction
    suspend fun addTransaction(entry: Entry) {
        auth.currentUser?.let { user ->
            try {
                db.collection("users")
                    .document(user.uid)
                    .collection("transactions")
                    .document(entry.id.toString())
                    .set(entry)
                    .await()
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    // Get all transactions for current user
    suspend fun getAllTransactions(): List<Entry> {
        return auth.currentUser?.let { user ->
            try {
                val snapshot = db.collection("users")
                    .document(user.uid)
                    .collection("transactions")
                    .get()
                    .await()
                
                snapshot.documents.mapNotNull { 
                    it.toObject(Entry::class.java) 
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        } ?: emptyList()
    }

    // Listen for real-time updates
    fun listenToTransactions(onUpdate: (List<Entry>) -> Unit) {
        auth.currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .collection("transactions")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        e.printStackTrace()
                        return@addSnapshotListener
                    }
                    
                    val entries = snapshot?.documents?.mapNotNull { 
                        it.toObject(Entry::class.java)
                    } ?: listOf()
                    
                    onUpdate(entries)
                }
        }
    }

    // Delete a transaction
    suspend fun deleteTransaction(entryId: Long) {
        auth.currentUser?.let { user ->
            try {
                db.collection("users")
                    .document(user.uid)
                    .collection("transactions")
                    .document(entryId.toString())
                    .delete()
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}