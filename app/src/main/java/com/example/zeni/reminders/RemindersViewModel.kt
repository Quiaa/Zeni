package com.example.zeni.reminders

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.zeni.core.data.model.Reminder
import com.example.zeni.core.data.repository.RemindersRepository

class RemindersViewModel : ViewModel() {

    private val repository = RemindersRepository()

    // Convert the Flow of reminders from the repository into LiveData
    val reminders: LiveData<List<Reminder>> = repository.getReminders().asLiveData()
}