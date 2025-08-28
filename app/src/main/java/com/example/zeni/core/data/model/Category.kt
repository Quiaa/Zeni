package com.example.zeni.core.data.model

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId
    var id: String = "",
    val userId: String = "",
    val name: String = ""
)