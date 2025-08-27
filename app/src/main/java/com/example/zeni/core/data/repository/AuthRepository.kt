package com.example.zeni.core.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {

    // Get the FirebaseAuth instance
    private val auth: FirebaseAuth = Firebase.auth

    // Function to get the current user
    fun getCurrentUser() = auth.currentUser

    // Suspend function to sign in a user.
    // It will return an AuthResult or throw an exception.
    suspend fun signIn(email: String, pass: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, pass).await()
    }

    // Suspend function to create a new user.
    // It will return an AuthResult or throw an exception.
    suspend fun signUp(email: String, pass: String): AuthResult {
        return auth.createUserWithEmailAndPassword(email, pass).await()
    }

    // Function to sign out the current user
    fun signOut() {
        auth.signOut()
    }
}