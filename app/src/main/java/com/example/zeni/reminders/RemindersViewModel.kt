package com.example.zeni.reminders

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.zeni.core.data.model.Reminder
import com.example.zeni.core.data.repository.RemindersRepository
import kotlinx.coroutines.launch

class RemindersViewModel : ViewModel() {

    private val repository = RemindersRepository()

    // Convert the Flow of reminders from the repository into LiveData
    val reminders: LiveData<List<Reminder>> = repository.getReminders().asLiveData()

    fun deleteReminder(reminder: Reminder, context: Context) {
        viewModelScope.launch {
            try {
                repository.deleteReminder(reminder, context)
            } catch (e: Exception) {
                // For now, just log the exception
                e.printStackTrace()
            }
        }
    }
}