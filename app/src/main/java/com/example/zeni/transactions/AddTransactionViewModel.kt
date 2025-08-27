package com.example.zeni.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeni.core.data.model.Transaction
import com.example.zeni.core.data.repository.AuthRepository
import com.example.zeni.core.data.repository.TransactionRepository
import kotlinx.coroutines.launch

// Enum to represent the state of saving a transaction
enum class SaveState {
    IDLE, SAVING, SUCCESS, FAILED
}

class AddTransactionViewModel : ViewModel() {

    private val transactionRepo = TransactionRepository()
    private val authRepo = AuthRepository()

    // LiveData to hold the current save state
    private val _saveState = MutableLiveData<SaveState>(SaveState.IDLE)
    val saveState: LiveData<SaveState> = _saveState

    fun saveTransaction(title: String, amount: Double, type: String, category: String) {
        // Set state to SAVING to show a loading indicator, for example
        _saveState.value = SaveState.SAVING

        // Get the current user's ID
        val userId = authRepo.getCurrentUser()?.uid

        if (userId == null) {
            _saveState.value = SaveState.FAILED // Can't save without a user
            return
        }

        // Create a new Transaction object
        val transaction = Transaction(
            userId = userId,
            title = title,
            amount = amount,
            type = type,
            category = category // We will use a default category for now
        )

        viewModelScope.launch {
            try {
                transactionRepo.addTransaction(transaction)
                _saveState.postValue(SaveState.SUCCESS)
            } catch (e: Exception) {
                _saveState.postValue(SaveState.FAILED)
            }
        }
    }
}