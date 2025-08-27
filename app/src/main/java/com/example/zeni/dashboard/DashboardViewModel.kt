package com.example.zeni.dashboard

import androidx.lifecycle.*
import com.example.zeni.core.data.model.Transaction
import com.example.zeni.core.data.repository.AuthRepository
import com.example.zeni.core.data.repository.TransactionRepository
import com.google.firebase.auth.FirebaseUser

class DashboardViewModel : ViewModel() {

    private val authRepo = AuthRepository()
    private val transactionRepo = TransactionRepository()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    val transactions: LiveData<List<Transaction>> = transactionRepo.getTransactions().asLiveData()

    // LiveData to hold the calculated monthly balance
    val balance: LiveData<Double> = transactions.map { transactionList ->
        var totalIncome = 0.0
        var totalExpense = 0.0
        for (transaction in transactionList) {
            if (transaction.type == "income") {
                totalIncome += transaction.amount
            } else {
                totalExpense += transaction.amount
            }
        }
        totalIncome - totalExpense
    }

    init {
        _user.value = authRepo.getCurrentUser()
    }

    fun signOut() {
        authRepo.signOut()
    }
}