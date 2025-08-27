package com.example.zeni.core.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// This data class represents a single payment reminder.
data class Reminder(
    @DocumentId
    var id: String = "",
    val userId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val reminderDate: Date? = null, // The date for the reminder notification
    @ServerTimestamp
    val createdAt: Date? = null // When the reminder was created
)