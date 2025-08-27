// File: app/src/main/java/com/example/zeni/core/data/repository/SavingsRepository.kt
package com.example.zeni.core.data.repository

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

class SavingsRepository {

    private val goalsCollection = Firebase.firestore.collection("savings_goals")
    private val authRepo = AuthRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSavingsGoals(): Flow<List<com.example.zeni.core.data.model.SavingsGoal>> {
        // Use flatMapLatest to switch to a new Firestore query whenever the user changes
        return authRepo.getAuthState().flatMapLatest { user ->
            val query = goalsCollection
                .whereEqualTo("userId", user?.uid ?: "") // Use the latest user's ID
                .orderBy("createdAt", Query.Direction.DESCENDING)

            query.snapshots().map { snapshot ->
                snapshot.toObjects()
            }
        }
    }

    suspend fun addSavingsGoal(goal: com.example.zeni.core.data.model.SavingsGoal) {
        goalsCollection.add(goal).await()
    }

    suspend fun updateSavingsGoalAmount(goalId: String, newAmount: Double) {
        goalsCollection.document(goalId).update("currentAmount", newAmount).await()
    }
}