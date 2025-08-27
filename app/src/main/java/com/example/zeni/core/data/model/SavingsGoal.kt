package com.example.zeni.core.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class SavingsGoal(
    @DocumentId
    var id: String = "",
    @get:Exclude @set:Exclude // This annotation is crucial
    var isSelected: Boolean = false,
    val userId: String = "",
    val title: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    @ServerTimestamp
    val createdAt: Date? = null,
    val deadline: Date? = null
)