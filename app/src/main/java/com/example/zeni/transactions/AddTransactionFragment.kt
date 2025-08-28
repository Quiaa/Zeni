package com.example.zeni.transactions

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.zeni.R
import com.example.zeni.core.data.model.Category
import com.example.zeni.core.data.model.Transaction
import com.example.zeni.databinding.FragmentAddTransactionBinding
import com.google.android.material.chip.Chip

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

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

        arguments?.getParcelable<Transaction>("transaction_to_edit")?.let { transaction ->
            viewModel.loadTransaction(transaction)
        }

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.toggleButtonType.check(R.id.buttonExpense)

        binding.buttonSave.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()
            val amountStr = binding.editTextAmount.text.toString().trim()
            val selectedTypeId = binding.toggleButtonType.checkedButtonId
            val amount = amountStr.toDoubleOrNull()
            val category = viewModel.selectedCategory.value

            if (title.isEmpty() || amountStr.isEmpty() || selectedTypeId == View.NO_ID || category == null) {
                Toast.makeText(context, "Please fill all fields and select a category.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amount == null || amount <= 0) {
                Toast.makeText(context, "Please enter a valid amount.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = if (selectedTypeId == R.id.buttonIncome) "income" else "expense"
            viewModel.saveTransaction(title, amount, type, category)
        }
    }

    private fun observeViewModel() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                SaveState.SAVING -> binding.buttonSave.isEnabled = false
                SaveState.SUCCESS -> findNavController().popBackStack()
                SaveState.FAILED -> {
                    Toast.makeText(context, "Error: Could not save transaction.", Toast.LENGTH_SHORT).show()
                    binding.buttonSave.isEnabled = true
                }
                else -> binding.buttonSave.isEnabled = true
            }
        }

        viewModel.editingTransaction.observe(viewLifecycleOwner) { transaction ->
            transaction?.let {
                binding.editTextTitle.setText(it.title)
                binding.editTextAmount.setText(it.amount.toString())
                if (it.type == "income") {
                    binding.toggleButtonType.check(R.id.buttonIncome)
                } else {
                    binding.toggleButtonType.check(R.id.buttonExpense)
                }
                viewModel.selectCategory(it.category)
                binding.textViewTitle.text = "Edit Transaction"
                binding.buttonSave.text = "Update"
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            updateCategoryChips(categories)
        }
    }

    private fun updateCategoryChips(categories: List<Category>) {
        binding.chipGroupCategory.removeAllViews()

        for (category in categories) {
            val chip = createCategoryChip(category.name)
            binding.chipGroupCategory.addView(chip)
        }

        // Add the special "Add New" chip at the end
        val addChip = Chip(context)
        addChip.text = "+"
        addChip.setOnClickListener { showAddCategoryDialog() }
        binding.chipGroupCategory.addView(addChip)

        // After recreating chips, re-check the selected one
        val selectedCategoryName = viewModel.selectedCategory.value
        for (i in 0 until binding.chipGroupCategory.childCount) {
            (binding.chipGroupCategory.getChildAt(i) as? Chip)?.let { chip ->
                if (chip.text == selectedCategoryName) {
                    chip.isChecked = true
                }
            }
        }
    }

    private fun createCategoryChip(categoryName: String): Chip {
        val chip = Chip(context)
        chip.text = categoryName
        chip.isCheckable = true
        chip.isClickable = true
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectCategory(categoryName)
            }
        }
        return chip
    }

    private fun showAddCategoryDialog() {
        val editText = EditText(context)
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Category")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val categoryName = editText.text.toString().trim()
                if (categoryName.isNotEmpty()) {
                    viewModel.addNewCategory(categoryName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}