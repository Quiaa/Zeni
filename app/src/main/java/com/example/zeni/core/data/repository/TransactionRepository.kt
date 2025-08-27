package com.example.zeni.core.data.repository

import com.example.zeni.core.data.model.Transaction
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class TransactionRepository {

    private val transactionCollection = Firebase.firestore.collection("transactions")
    private val auth = Firebase.auth

    suspend fun addTransaction(transaction: Transaction) {
        transactionCollection.add(transaction).await()
    }

    // Function to get a real-time flow of transactions for the current user
    fun getTransactions(): Flow<List<Transaction>> {
        val userId = auth.currentUser?.uid ?: "" // Get current user's ID

        // Create a query to get transactions for the current user, ordered by timestamp
        val query = transactionCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        // Use a snapshot listener to get real-time updates and convert it to a Flow
        return query.snapshots().map { snapshot ->
            snapshot.toObjects<Transaction>()
        }
    }
}