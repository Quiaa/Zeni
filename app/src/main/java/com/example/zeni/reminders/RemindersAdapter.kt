package com.example.zeni.reminders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zeni.core.data.model.Reminder
import com.example.zeni.databinding.ItemReminderBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class RemindersAdapter(
    private val onItemClicked: (Reminder) -> Unit
) : ListAdapter<Reminder, RemindersAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.bind(reminder)
    }

    inner class ReminderViewHolder(private val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: Reminder) {
            binding.textViewReminderTitle.text = reminder.title

            // Format the amount as currency
            val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            binding.textViewReminderAmount.text = currencyFormat.format(reminder.amount)

            // Format the date
            reminder.reminderDate?.let {
                val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
                binding.textViewReminderDate.text = "Next on: ${dateFormat.format(it)}"
            }

            itemView.setOnClickListener { onItemClicked(reminder) }
        }
    }
}

class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }
}