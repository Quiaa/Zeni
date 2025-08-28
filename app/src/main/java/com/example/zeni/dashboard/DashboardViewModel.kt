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

    // Now it will hold a Pair of the pie entries and the total expense for the center text
    val pieChartData: LiveData<Pair<List<PieEntry>, Double>> = transactions.map { transactionList ->
        val expenses = transactionList.filter { it.type == "expense" }
        val totalExpense = expenses.sumOf { it.amount }

        val entries = expenses
            .groupBy { it.category }
            .map { (category, transactions) ->
                PieEntry(transactions.sumOf { it.amount }.toFloat(), category)
            }

        // Return both the entries and the total expense
        Pair(entries, totalExpense)
    }
    // LiveData to hold the currently selected pie chart slice.
    // Null means nothing is selected.
    private val _selectedSlice = MutableLiveData<PieEntry?>(null)
    val selectedSlice: LiveData<PieEntry?> = _selectedSlice

    init {
        _user.value = authRepo.getCurrentUser()
    }

    // Called from the Fragment when a user selects or deselects a slice.
    fun onSliceSelected(entry: PieEntry?) {
        _selectedSlice.value = entry
    }

    fun signOut() {
        authRepo.signOut()
    }
}