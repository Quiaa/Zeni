package com.example.zeni.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeni.core.data.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    // Create an instance of the repository
    private val repository = AuthRepository()

    // LiveData to hold the authentication result
    private val _authResult = MutableLiveData<Result<AuthResult>>()
    val authResult: LiveData<Result<AuthResult>> = _authResult

    // Function to handle user login
    fun loginUser(email: String, pass: String) {
        // Use viewModelScope to launch a coroutine
        viewModelScope.launch {
            try {
                val result = repository.signIn(email, pass)
                _authResult.postValue(Result.success(result))
            } catch (e: Exception) {
                _authResult.postValue(Result.failure(e))
            }
        }
    }

    // Function to handle user registration
    fun registerUser(email: String, pass: String) {
        viewModelScope.launch {
            try {
                val result = repository.signUp(email, pass)
                _authResult.postValue(Result.success(result))
            } catch (e: Exception) {
                _authResult.postValue(Result.failure(e))
            }
        }
    }
}