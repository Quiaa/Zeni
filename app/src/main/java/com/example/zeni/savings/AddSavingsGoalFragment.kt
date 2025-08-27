// File: app/src/main/java/com/example/zeni/savings/AddSavingsGoalFragment.kt
package com.example.zeni.savings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.zeni.databinding.FragmentAddSavingsGoalBinding
import com.example.zeni.transactions.SaveState

class AddSavingsGoalFragment : Fragment() {

    private var _binding: FragmentAddSavingsGoalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddSavingsGoalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSavingsGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListener()
        observeSaveState()
    }

    private fun setupClickListener() {
        binding.buttonSaveGoal.setOnClickListener {
            val title = binding.editTextGoalTitle.text.toString().trim()
            val amountStr = binding.editTextTargetAmount.text.toString().trim()

            if (title.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val targetAmount = amountStr.toDoubleOrNull()
            if (targetAmount == null || targetAmount <= 0) {
                Toast.makeText(context, "Please enter a valid target amount.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.saveNewGoal(title, targetAmount)
        }
    }

    private fun observeSaveState() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                SaveState.SAVING -> {
                    binding.buttonSaveGoal.isEnabled = false
                }
                SaveState.SUCCESS -> {
                    Toast.makeText(context, "New goal saved!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                SaveState.FAILED -> {
                    Toast.makeText(context, "Error: Could not save goal.", Toast.LENGTH_SHORT).show()
                    binding.buttonSaveGoal.isEnabled = true
                }
                else -> { // IDLE
                    binding.buttonSaveGoal.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}