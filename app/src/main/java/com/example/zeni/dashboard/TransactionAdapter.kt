package com.example.zeni.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zeni.R
import com.example.zeni.core.data.model.Transaction
import com.example.zeni.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.Locale

// Adapter for the RecyclerView in DashboardFragment. It uses ListAdapter for efficiency.
class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    // Creates a new ViewHolder instance when the RecyclerView needs one.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    // Binds the data from a Transaction object to the views in a ViewHolder.
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    // Inner class representing a single row in the RecyclerView.
    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.textViewTransactionTitle.text = transaction.title
            binding.textViewTransactionCategory.text = transaction.category

            // Format the amount as currency
            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            val formattedAmount = format.format(transaction.amount)

            // Set the amount and color based on the transaction type
            if (transaction.type == "income") {
                binding.textViewTransactionAmount.text = "+$formattedAmount"
                binding.textViewTransactionAmount.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.green) // You'll need to add this color
                )
            } else { // "expense"
                binding.textViewTransactionAmount.text = "-$formattedAmount"
                binding.textViewTransactionAmount.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.red) // You'll need to add this color
                )
            }
        }
    }
}

// DiffUtil callback to efficiently update the list.
// It calculates the difference between two lists and only updates the changed items.
class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }
}