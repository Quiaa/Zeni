package com.example.zeni.core.data.repository

import android.content.Context
import com.example.zeni.core.data.model.Reminder
import com.example.zeni.reminders.AlarmScheduler
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

class RemindersRepository {

    private val remindersCollection = Firebase.firestore.collection("reminders")
    private val authRepo = AuthRepository()

    // Function to add a new reminder
    suspend fun addReminder(reminder: Reminder, context: Context) {
        remindersCollection.add(reminder).await()
        AlarmScheduler(context).schedule(reminder)
    }

    // Function to delete a reminder
    suspend fun deleteReminder(reminder: Reminder, context: Context) {
        AlarmScheduler(context).cancel(reminder)
        remindersCollection.document(reminder.id).delete().await()
    }

    // Function to get a single reminder by its ID
    suspend fun getReminder(reminderId: String): Reminder? {
        return remindersCollection.document(reminderId).get().await().toObject(Reminder::class.java)
    }

    // Function to update a reminder
    suspend fun updateReminder(reminder: Reminder, context: Context) {
        remindersCollection.document(reminder.id).set(reminder).await()
        AlarmScheduler(context).schedule(reminder)
    }

    // Function to get a real-time flow of reminders for the current user
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getReminders(): Flow<List<Reminder>> {
        return authRepo.getAuthState().flatMapLatest { user ->
            val query = remindersCollection
                .whereEqualTo("userId", user?.uid ?: "")
                // Order by the upcoming reminder date
                .orderBy("reminderDate", Query.Direction.ASCENDING)

            query.snapshots().map { snapshot ->
                snapshot.toObjects()
            }
        }
    }
}