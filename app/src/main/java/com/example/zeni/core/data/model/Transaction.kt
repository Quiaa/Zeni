package com.example.zeni.core.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// This data class represents a single income or expense transaction.
data class Transaction(
    var id: String = "", // Unique ID for the transaction, will be set by Firestore
    val userId: String = "", // ID of the user who owns this transaction
    val title: String = "",
    val amount: Double = 0.0,
    val type: String = "", // "income" or "expense"
    val category: String = "",
    @ServerTimestamp
    val timestamp: Date? = null // The date and time of the transaction
)