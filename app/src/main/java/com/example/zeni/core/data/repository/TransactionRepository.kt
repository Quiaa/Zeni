package com.example.zeni.core.data.repository

import com.example.zeni.core.data.model.Transaction
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class TransactionRepository {

    private val transactionCollection = Firebase.firestore.collection("transactions")
    private val authRepo = AuthRepository()

    suspend fun addTransaction(transaction: Transaction) {
        transactionCollection.add(transaction).await()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTransactions(): Flow<List<Transaction>> {
        return authRepo.getAuthState().flatMapLatest { user ->
            val query = transactionCollection
                .whereEqualTo("userId", user?.uid ?: "")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            query.snapshots().map { snapshot ->
                snapshot.toObjects()
            }
        }
    }
    // Function to delete a transaction document from Firestore
    suspend fun deleteTransaction(transactionId: String) {
        // We only need the ID of the document to delete it
        transactionCollection.document(transactionId).delete().await()
    }

    // Function to update an existing transaction document in Firestore
    suspend fun updateTransaction(transaction: Transaction) {
        // We use the transaction's ID to find the document and set() to overwrite it
        transactionCollection.document(transaction.id).set(transaction).await()
    }
}