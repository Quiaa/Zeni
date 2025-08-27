// File: app/src/main/java/com/example/zeni/savings/AddSavingsGoalViewModel.kt
package com.example.zeni.savings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeni.core.data.model.SavingsGoal
import com.example.zeni.core.data.repository.AuthRepository
import com.example.zeni.core.data.repository.SavingsRepository
import com.example.zeni.transactions.SaveState
import kotlinx.coroutines.launch

class AddSavingsGoalViewModel : ViewModel() {

    private val savingsRepo = SavingsRepository()
    private val authRepo = AuthRepository()

    private val _saveState = MutableLiveData<SaveState>(SaveState.IDLE)
    val saveState: LiveData<SaveState> = _saveState

    fun saveNewGoal(title: String, targetAmount: Double) {
        _saveState.value = SaveState.SAVING
        val userId = authRepo.getCurrentUser()?.uid

        if (userId == null) {
            _saveState.value = SaveState.FAILED
            return
        }

        val newGoal = SavingsGoal(
            userId = userId,
            title = title,
            targetAmount = targetAmount,
            currentAmount = 0.0 // A new goal starts with 0 saved
        )

        viewModelScope.launch {
            try {
                savingsRepo.addSavingsGoal(newGoal)
                _saveState.postValue(SaveState.SUCCESS)
            } catch (e: Exception) {
                _saveState.postValue(SaveState.FAILED)
            }
        }
    }
}