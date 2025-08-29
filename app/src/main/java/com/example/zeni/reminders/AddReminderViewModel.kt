package com.example.zeni.reminders

import android.content.Context
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

    private val _reminder = MutableLiveData<Reminder?>()
    val reminder: LiveData<Reminder?> = _reminder

    private var currentReminderId: String? = null

    fun loadReminder(reminderId: String) {
        currentReminderId = reminderId
        viewModelScope.launch {
            _reminder.value = remindersRepo.getReminder(reminderId)
        }
    }

    fun saveReminder(title: String, amount: Double, date: Date, context: Context) {
        _saveState.value = SaveState.SAVING
        val userId = authRepo.getCurrentUser()?.uid

        if (userId == null) {
            _saveState.value = SaveState.FAILED
            return
        }

        val reminderToSave = if (currentReminderId != null) {
            // This is an update
            Reminder(
                id = currentReminderId!!,
                userId = userId,
                title = title,
                amount = amount,
                reminderDate = date
            )
        } else {
            // This is a new reminder
            Reminder(
                userId = userId,
                title = title,
                amount = amount,
                reminderDate = date
            )
        }

        viewModelScope.launch {
            try {
                if (currentReminderId != null) {
                    remindersRepo.updateReminder(reminderToSave, context)
                } else {
                    remindersRepo.addReminder(reminderToSave, context)
                }
                _saveState.postValue(SaveState.SUCCESS)
            } catch (e: Exception) {
                _saveState.postValue(SaveState.FAILED)
            }
        }
    }
}