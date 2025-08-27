package com.example.zeni.reminders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zeni.core.data.model.Reminder
import com.example.zeni.core.data.repository.AuthRepository
import com.example.zeni.core.data.repository.RemindersRepository
import com.example.zeni.transactions.SaveState
import kotlinx.coroutines.launch
import java.util.Date

class AddReminderViewModel : ViewModel() {

    private val remindersRepo = RemindersRepository()
    private val authRepo = AuthRepository()

    private val _saveState = MutableLiveData<SaveState>(SaveState.IDLE)
    val saveState: LiveData<SaveState> = _saveState

    fun saveNewReminder(title: String, amount: Double, date: Date) {
        _saveState.value = SaveState.SAVING
        val userId = authRepo.getCurrentUser()?.uid

        if (userId == null) {
            _saveState.value = SaveState.FAILED
            return
        }

        val newReminder = Reminder(
            userId = userId,
            title = title,
            amount = amount,
            reminderDate = date
        )

        viewModelScope.launch {
            try {
                remindersRepo.addReminder(newReminder)
                _saveState.postValue(SaveState.SUCCESS)
            } catch (e: Exception) {
                _saveState.postValue(SaveState.FAILED)
            }
        }
    }
}