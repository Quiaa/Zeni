package com.example.zeni.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.zeni.core.data.model.Transaction
import com.example.zeni.core.data.repository.TransactionRepository

class AllTransactionsViewModel : ViewModel() {

    private val transactionRepo = TransactionRepository()

    // This LiveData holds the FULL list of transactions
    val allTransactions: LiveData<List<Transaction>> = transactionRepo.getTransactions().asLiveData()
}