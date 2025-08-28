package com.example.zeni.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.zeni.core.data.model.Transaction
import com.example.zeni.core.data.repository.TransactionRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AllTransactionsViewModel : ViewModel() {

    private val transactionRepo = TransactionRepository()

    // This LiveData holds the FULL list of transactions
    val allTransactions: LiveData<List<Transaction>> = transactionRepo.getTransactions().asLiveData()

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepo.deleteTransaction(transaction.id)
                // Deletion is successful. The live data will update automatically.
            } catch (e: Exception) {
                // Handle exceptions, e.g., show an error message to the user
            }
        }
    }
}