package com.example.zeni.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.zeni.core.data.model.Category
import com.example.zeni.core.data.model.Transaction
import com.example.zeni.core.data.repository.AuthRepository
import com.example.zeni.core.data.repository.CategoryRepository
import com.example.zeni.core.data.repository.TransactionRepository
import kotlinx.coroutines.launch

// Enum to represent the state of saving a transaction
enum class SaveState {
    IDLE, SAVING, SUCCESS, FAILED
}

class AddTransactionViewModel : ViewModel() {

    private val transactionRepo = TransactionRepository()
    private val authRepo = AuthRepository()
    private val categoryRepo = CategoryRepository()

    private val _saveState = MutableLiveData<SaveState>(SaveState.IDLE)
    val saveState: LiveData<SaveState> = _saveState

    // LiveData to hold the transaction being edited. Null if in "Add" mode.
    private val _editingTransaction = MutableLiveData<Transaction?>(null)
    val editingTransaction: LiveData<Transaction?> = _editingTransaction

    // LiveData to hold the list of all available categories
    val categories: LiveData<List<Category>> = categoryRepo.getCategories().asLiveData()
    // LiveData to hold the currently selected category name
    private val _selectedCategory = MutableLiveData<String?>()
    val selectedCategory: LiveData<String?> = _selectedCategory

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

    fun selectCategory(categoryName: String) {
        _selectedCategory.value = categoryName
    }

    fun addNewCategory(categoryName: String) {
        val userId = authRepo.getCurrentUser()?.uid ?: return
        val newCategory = Category(userId = userId, name = categoryName)
        viewModelScope.launch {
            try {
                categoryRepo.addCategory(newCategory)
                // The new category will appear automatically because of the live data stream.
                // We can also automatically select it for the user.
                _selectedCategory.postValue(categoryName)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}