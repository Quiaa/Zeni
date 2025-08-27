package com.example.zeni.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Function to get the current user's state as a Flow
    fun getAuthState() = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }


    suspend fun signIn(email: String, pass: String) = auth.signInWithEmailAndPassword(email, pass).await()
    suspend fun signUp(email: String, pass: String) = auth.createUserWithEmailAndPassword(email, pass).await()
    fun signOut() = auth.signOut()
}