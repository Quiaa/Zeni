package com.example.zeni.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.zeni.R
import com.example.zeni.databinding.FragmentAddTransactionBinding

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    // Get a reference to the AddTransactionViewModel
    private val viewModel: AddTransactionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeSaveState()
    }

    private fun setupClickListeners() {
        // Set a default selection for the toggle group
        binding.toggleButtonType.check(R.id.buttonExpense)

        binding.buttonSave.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()
            val amountStr = binding.editTextAmount.text.toString().trim()
            val selectedTypeId = binding.toggleButtonType.checkedButtonId

            if (title.isEmpty() || amountStr.isEmpty() || selectedTypeId == View.NO_ID) {
                Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(context, "Please enter a valid amount.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = if (selectedTypeId == R.id.buttonIncome) "income" else "expense"

            // Call the ViewModel to save the transaction
            viewModel.saveTransaction(title, amount, type, "Other") // Using "Other" as default category
        }
    }

    private fun observeSaveState() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                SaveState.SAVING -> {
                    // Optionally: show a progress bar
                    binding.buttonSave.isEnabled = false
                }
                SaveState.SUCCESS -> {
                    Toast.makeText(context, "Transaction saved!", Toast.LENGTH_SHORT).show()
                    // Navigate back to the dashboard
                    findNavController().popBackStack()
                }
                SaveState.FAILED -> {
                    Toast.makeText(context, "Error: Could not save transaction.", Toast.LENGTH_SHORT).show()
                    binding.buttonSave.isEnabled = true
                }
                else -> { // IDLE
                    binding.buttonSave.isEnabled = true
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}