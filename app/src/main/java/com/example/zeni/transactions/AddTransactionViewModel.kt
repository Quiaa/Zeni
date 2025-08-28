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

    private val _saveState = MutableLiveData<SaveState>(SaveState.IDLE)
    val saveState: LiveData<SaveState> = _saveState

    // LiveData to hold the transaction being edited. Null if in "Add" mode.
    private val _editingTransaction = MutableLiveData<Transaction?>(null)
    val editingTransaction: LiveData<Transaction?> = _editingTransaction

    // Function to set the ViewModel into "Edit Mode"
    fun loadTransaction(transaction: Transaction) {
        _editingTransaction.value = transaction
    }

    // This function now handles both saving a new transaction and updating an existing one
    fun saveTransaction(title: String, amount: Double, type: String, category: String) {
        _saveState.value = SaveState.SAVING
        val userId = authRepo.getCurrentUser()?.uid

        if (userId == null) {
            _saveState.value = SaveState.FAILED
            return
        }

        viewModelScope.launch {
            try {
                // Check if we are in "Edit Mode"
                val transactionToSave = _editingTransaction.value
                if (transactionToSave != null) {
                    // Update existing transaction
                    val updatedTransaction = transactionToSave.copy(
                        title = title,
                        amount = amount,
                        type = type,
                        category = category
                    )
                    transactionRepo.updateTransaction(updatedTransaction)
                } else {
                    // Create a new transaction
                    val newTransaction = Transaction(
                        userId = userId,
                        title = title,
                        amount = amount,
                        type = type,
                        category = category
                    )
                    transactionRepo.addTransaction(newTransaction)
                }
                _saveState.postValue(SaveState.SUCCESS)
            } catch (e: Exception) {
                _saveState.postValue(SaveState.FAILED)
            }
        }
    }
}