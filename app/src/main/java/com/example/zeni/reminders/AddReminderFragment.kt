// File: app/src/main/java/com/example/zeni/reminders/AddReminderFragment.kt
package com.example.zeni.reminders

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.zeni.core.data.model.Reminder
import com.example.zeni.databinding.FragmentAddReminderBinding
import com.example.zeni.transactions.SaveState
import java.text.SimpleDateFormat
import java.util.*

class AddReminderFragment : Fragment() {

    private var _binding: FragmentAddReminderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddReminderViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private val args: AddReminderFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeSaveState()
        observeReminder()

        if (args.reminderId == null) {
            updateDateTimeButtons() // Set initial date text for new reminders
        } else {
            viewModel.loadReminder(args.reminderId!!)
        }
    }

    private fun populateUi(reminder: Reminder) {
        binding.editTextReminderTitle.setText(reminder.title)
        binding.editTextAmount.setText(reminder.amount.toString())
        reminder.reminderDate?.let {
            calendar.time = it
            updateDateTimeButtons()
        }
        // You might want to change the title of the fragment/toolbar as well
        // (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit Reminder"
    }

    private fun observeReminder() {
        viewModel.reminder.observe(viewLifecycleOwner) { reminder ->
            reminder?.let { populateUi(it) }
        }
    }

    private fun setupClickListeners() {
        binding.buttonSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonSelectTime.setOnClickListener {
            showTimePickerDialog()
        }

        binding.buttonSaveReminder.setOnClickListener {
            val title = binding.editTextReminderTitle.text.toString().trim()
            val amountStr = binding.editTextAmount.text.toString().trim()
            val amount = amountStr.toDoubleOrNull()

            if (title.isEmpty() || amount == null || amount <= 0) {
                Toast.makeText(context, "Please fill all fields with valid data.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.saveReminder(title, amount, calendar.time, requireContext().applicationContext)
        }
    }

    private fun observeSaveState() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                SaveState.SAVING -> binding.buttonSaveReminder.isEnabled = false
                SaveState.SUCCESS -> {
                    Toast.makeText(context, "Reminder saved!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                SaveState.FAILED -> {
                    Toast.makeText(context, "Error: Could not save reminder.", Toast.LENGTH_SHORT).show()
                    binding.buttonSaveReminder.isEnabled = true
                }
                else -> binding.buttonSaveReminder.isEnabled = true
            }
        }
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateTimeButtons()
        }

        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePickerDialog() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateDateTimeButtons()
        }
        TimePickerDialog(
            requireContext(),
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour view
        ).show()
    }

    private fun updateDateTimeButtons() {
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
        binding.buttonSelectDate.text = dateFormat.format(calendar.time)

        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
        binding.buttonSelectTime.text = timeFormat.format(calendar.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}