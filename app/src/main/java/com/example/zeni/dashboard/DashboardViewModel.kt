package com.example.zeni.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeni.core.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser

class DashboardViewModel : ViewModel() {

    // Create an instance of the repository
    private val repository = AuthRepository()

    // LiveData to hold the current user's information
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    init {
        // When the ViewModel is created, get the current user
        _user.value = repository.getCurrentUser()
    }

    // Function to handle user sign out
    fun signOut() {
        repository.signOut()
    }
}