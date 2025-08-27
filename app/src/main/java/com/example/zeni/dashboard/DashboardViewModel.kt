// File: app/src/main/java/com/example/zeni/dashboard/DashboardViewModel.kt
package com.example.zeni.dashboard

import androidx.lifecycle.*
import com.example.zeni.core.data.model.Transaction
import com.example.zeni.core.data.repository.AuthRepository
import com.example.zeni.core.data.repository.TransactionRepository
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseUser

class DashboardViewModel : ViewModel() {

    private val authRepo = AuthRepository()
    private val transactionRepo = TransactionRepository()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    // This remains the source of truth for all calculations
    val transactions: LiveData<List<Transaction>> = transactionRepo.getTransactions().asLiveData()

    val recentTransactions: LiveData<List<Transaction>> = transactions.map { fullList ->
        fullList.take(5) // Take the first 5 items from the full list
    }

    val totalIncome: LiveData<Double> = transactions.map { transactionList ->
        transactionList.filter { it.type == "income" }.sumOf { it.amount }
    }

    val totalExpense: LiveData<Double> = transactions.map { transactionList ->
        transactionList.filter { it.type == "expense" }.sumOf { it.amount }
    }

    val balance: LiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(totalIncome) { income ->
            val expense = totalExpense.value ?: 0.0
            value = income - expense
        }
        addSource(totalExpense) { expense ->
            val income = totalIncome.value ?: 0.0
            value = income - expense
        }
    }

    val expenseByCategory: LiveData<List<PieEntry>> = transactions.map { transactionList ->
        transactionList
            .filter { it.type == "expense" } // Only consider expenses
            .groupBy { it.category } // Group by category
            .map { (category, transactions) ->
                // For each category, create a PieEntry with the category name and total amount
                PieEntry(transactions.sumOf { it.amount }.toFloat(), category)
            }
    }

    init {
        _user.value = authRepo.getCurrentUser()
    }

    fun signOut() {
        authRepo.signOut()
    }
}