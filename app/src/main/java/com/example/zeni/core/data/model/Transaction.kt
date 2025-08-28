package com.example.zeni.core.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Transaction(
    @DocumentId
    var id: String = "",
    val userId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val type: String = "",
    val category: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
) : Parcelable