package com.example.zeni.savings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zeni.core.data.model.SavingsGoal
import com.example.zeni.databinding.ItemSavingsGoalBinding
import java.text.NumberFormat
import java.util.*

class SavingsAdapter(
    private val onAddFundsClicked: (SavingsGoal) -> Unit
) : ListAdapter<SavingsGoal, SavingsAdapter.SavingsViewHolder>(SavingsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavingsViewHolder {
        val binding = ItemSavingsGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavingsViewHolder, position: Int) {
        val goal = getItem(position)
        holder.bind(goal)
    }

    inner class SavingsViewHolder(private val binding: ItemSavingsGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: SavingsGoal) {
            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            val currentFormatted = format.format(goal.currentAmount)
            val targetFormatted = format.format(goal.targetAmount)

            binding.textViewGoalTitle.text = goal.title
            binding.textViewGoalAmount.text = "$currentFormatted / $targetFormatted"

            // Calculate and set progress
            val progress = if (goal.targetAmount > 0) {
                (goal.currentAmount / goal.targetAmount * 100).toInt()
            } else {
                0
            }
            binding.progressBarGoal.progress = progress

            binding.buttonAddFunds.setOnClickListener {
                onAddFundsClicked(goal)
            }
        }
    }
}

class SavingsDiffCallback : DiffUtil.ItemCallback<SavingsGoal>() {
    override fun areItemsTheSame(oldItem: SavingsGoal, newItem: SavingsGoal): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SavingsGoal, newItem: SavingsGoal): Boolean {
        return oldItem == newItem
    }
}