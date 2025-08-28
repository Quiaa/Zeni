package com.example.zeni.transactions

import com.example.zeni.core.data.model.Transaction
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeni.dashboard.TransactionAdapter
import com.example.zeni.databinding.FragmentAllTransactionsBinding
import com.example.zeni.R

class AllTransactionsFragment : Fragment() {

    private var _binding: FragmentAllTransactionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllTransactionsViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Pass the long click handler lambda to the adapter's constructor
        transactionAdapter = TransactionAdapter { transaction ->
            showOptionsDialog(transaction)
        }
        binding.recyclerViewAllTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
        }
    }

    // Shows a dialog with "Edit" and "Delete" options
    private fun showOptionsDialog(transaction: Transaction) {
        // First, check if the transaction category is "Savings"
        if (transaction.category == "Savings") {
            // If it is, show an informative message and do not show the options dialog.
            AlertDialog.Builder(requireContext())
                .setTitle("Action Not Allowed")
                .setMessage("Contributions to savings goals cannot be edited or deleted from the transaction list. Please manage your savings from the 'Savings' tab.")
                .setPositiveButton("OK", null)
                .show()
            return // Stop the function here
        }

        // If the category is not "Savings", show the regular options dialog.
        val options = arrayOf("Edit", "Delete")

        AlertDialog.Builder(requireContext())
            .setTitle("Choose an action")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Handle "Edit" action
                        val bundle = bundleOf("transaction_to_edit" to transaction)
                        findNavController().navigate(R.id.action_allTransactionsFragment_to_addTransactionFragment, bundle)
                    }
                    1 -> {
                        // Handle "Delete" action
                        showDeleteConfirmationDialog(transaction)
                    }
                }
            }
            .show()
    }
    // Shows a confirmation dialog before deleting a transaction
    private fun showDeleteConfirmationDialog(transaction: Transaction) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete \"${transaction.title}\"? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTransaction(transaction)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}