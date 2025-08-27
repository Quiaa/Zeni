// File: app/src/main/java/com/example/zeni/savings/SavingsViewModel.kt
package com.example.zeni.savings

import androidx.lifecycle.*
import com.example.zeni.core.data.model.SavingsGoal
import com.example.zeni.core.data.model.Transaction
import com.example.zeni.core.data.repository.AuthRepository
import com.example.zeni.core.data.repository.SavingsRepository
import com.example.zeni.core.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class SavingsViewModel : ViewModel() {

    private val savingsRepo = SavingsRepository()
    private val transactionRepo = TransactionRepository()
    private val authRepo = AuthRepository()

    val savingsGoals: LiveData<List<SavingsGoal>> = savingsRepo.getSavingsGoals().asLiveData()

    val totalSavedAmount: LiveData<Double> = savingsGoals.map { goals ->
        goals.sumOf { it.currentAmount }
    }

    // LiveData to hold the goal selected for adding funds
    private val _selectedGoal = MutableLiveData<SavingsGoal?>()
    val selectedGoal: LiveData<SavingsGoal?> = _selectedGoal

    // The amount to add, represented as text for the EditText
    private val _amountToAddText = MutableLiveData<String>()
    val amountToAddText: LiveData<String> = _amountToAddText

    // The progress for the SeekBar
    private val _seekBarProgress = MutableLiveData<Int>()
    val seekBarProgress: LiveData<Int> = _seekBarProgress

    // Calculated remaining amount for the selected goal
    val remainingAmount: LiveData<Double> = _selectedGoal.map { goal ->
        goal?.let { it.targetAmount - it.currentAmount } ?: 0.0
    }

    // Function to be called when the user clicks the "Add Funds" button on an item
    fun onAddFundsClicked(goal: SavingsGoal) {
        _selectedGoal.value = goal
        _amountToAddText.value = "0.00" // Reset to 0 when dialog opens
        _seekBarProgress.value = 0
    }

    // Called when the user types in the EditText
    fun onAmountTextChanged(text: String) {
        if (_amountToAddText.value == text) return // Avoid infinite loops

        _amountToAddText.value = text
        val amount = text.toDoubleOrNull() ?: 0.0
        val maxAmount = remainingAmount.value ?: 0.0

        val progress = if (amount > maxAmount) {
            // If user types more than max, correct the text
            _amountToAddText.value = String.format("%.2f", maxAmount)
            (maxAmount * 100).toInt()
        } else {
            (amount * 100).toInt()
        }
        _seekBarProgress.value = progress
    }

    // Called when the user moves the SeekBar
    fun onSeekBarProgressChanged(progress: Int) {
        if (_seekBarProgress.value == progress) return // Avoid infinite loops

        _seekBarProgress.value = progress
        val amount = progress / 100.0
        _amountToAddText.value = String.format("%.2f", amount)
    }

    fun addFundsToGoal(amountToAdd: Double) {
        val goal = _selectedGoal.value ?: return // Get the currently selected goal

        viewModelScope.launch {
            try {
                val newCurrentAmount = goal.currentAmount + amountToAdd
                savingsRepo.updateSavingsGoalAmount(goal.id, newCurrentAmount)

                val transaction = Transaction(
                    userId = authRepo.getCurrentUser()?.uid ?: "",
                    title = "Contribution to: ${goal.title}",
                    amount = amountToAdd,
                    type = "expense",
                    category = "Savings"
                )
                transactionRepo.addTransaction(transaction)

            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }
}