package com.example.zeni.core.data.repository

import com.example.zeni.core.data.model.Category
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

class CategoryRepository {

    private val categoryCollection = Firebase.firestore.collection("categories")
    private val authRepo = AuthRepository()

    // Function to add a new category
    suspend fun addCategory(category: Category) {
        categoryCollection.add(category).await()
    }

    // Function to get a real-time flow of categories for the current user
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCategories(): Flow<List<Category>> {
        return authRepo.getAuthState().flatMapLatest { user ->
            // Query for categories belonging to the current user, ordered by name
            val query = categoryCollection
                .whereEqualTo("userId", user?.uid ?: "")
                .orderBy("name", Query.Direction.ASCENDING)

            query.snapshots().map { snapshot ->
                snapshot.toObjects()
            }
        }
    }
}