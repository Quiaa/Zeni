// File: app/src/main/java/com/example/zeni/savings/SavingsFragment.kt
package com.example.zeni.savings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeni.R
import com.example.zeni.core.data.model.SavingsGoal
import com.example.zeni.databinding.FragmentSavingsBinding
import java.text.NumberFormat
import java.util.*

class SavingsFragment : Fragment() {

    private var _binding: FragmentSavingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SavingsViewModel by viewModels()
    private lateinit var savingsAdapter: SavingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.buttonAddGoal.setOnClickListener {
            findNavController().navigate(R.id.action_savingsFragment_to_addSavingsGoalFragment)
        }
    }

    private fun setupRecyclerView() {
        savingsAdapter = SavingsAdapter { goal ->
            viewModel.onAddFundsClicked(goal)
            showAddFundsDialog()
        }
        binding.recyclerViewSavingsGoals.apply {
            adapter = savingsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.savingsGoals.observe(viewLifecycleOwner) { goals ->
            savingsAdapter.submitList(goals)
        }

        viewModel.totalSavedAmount.observe(viewLifecycleOwner) { totalAmount ->
            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            binding.textViewTotalSavingsAmount.text = format.format(totalAmount)
        }
    }

    private fun showAddFundsDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_funds, null)
        val editTextAmount = dialogView.findViewById<EditText>(R.id.editTextAmount)
        val seekBarAmount = dialogView.findViewById<SeekBar>(R.id.seekBarAmount)
        val textViewDialogTitle = dialogView.findViewById<TextView>(R.id.textViewDialogTitle)
        val textViewRemainingAmount = dialogView.findViewById<TextView>(R.id.textViewRemainingAmount)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val amountToAdd = viewModel.amountToAddText.value?.toDoubleOrNull() ?: 0.0
                if (amountToAdd > 0) {
                    viewModel.addFundsToGoal(amountToAdd)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()


        var currentGoal: SavingsGoal? = null
        var remainingAmount = 0.0

        viewModel.selectedGoal.observe(viewLifecycleOwner) { goal ->
            goal?.let {
                currentGoal = it
                textViewDialogTitle.text = "Add Funds to \"${it.title}\""
            }
        }

        viewModel.remainingAmount.observe(viewLifecycleOwner) { remaining ->
            remainingAmount = remaining
            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            textViewRemainingAmount.text = "Remaining: ${format.format(remaining)}"
            seekBarAmount.max = (remaining * 100).toInt()
        }

        viewModel.amountToAddText.observe(viewLifecycleOwner) { text ->
            if (editTextAmount.text.toString() != text) {
                editTextAmount.setText(text)
                editTextAmount.setSelection(editTextAmount.length())
            }
        }

        viewModel.seekBarProgress.observe(viewLifecycleOwner) { progress ->
            if (seekBarAmount.progress != progress) {
                seekBarAmount.progress = progress
            }
        }

        seekBarAmount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.onSeekBarProgressChanged(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onAmountTextChanged(s.toString().replace(',', '.'))
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}