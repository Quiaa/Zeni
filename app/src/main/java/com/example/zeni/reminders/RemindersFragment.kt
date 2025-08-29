// File: app/src/main/java/com/example/zeni/reminders/RemindersFragment.kt
package com.example.zeni.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeni.R
import com.example.zeni.core.data.model.Reminder
import com.example.zeni.databinding.FragmentRemindersBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.navigation.fragment.findNavController

class RemindersFragment : Fragment() {

    private var _binding: FragmentRemindersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RemindersViewModel by viewModels()
    private lateinit var remindersAdapter: RemindersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeReminders()
        setupFragmentResultListener()

        binding.buttonAddReminder.setOnClickListener {
            // Navigate to AddReminderFragment
            findNavController().navigate(R.id.action_remindersFragment_to_addReminderFragment)
        }
    }

    private fun setupFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(ReminderActionsBottomSheet.REQUEST_KEY, this) { _, bundle ->
            val action = bundle.getString(ReminderActionsBottomSheet.KEY_ACTION)
            val reminderId = bundle.getString(ReminderActionsBottomSheet.KEY_REMINDER_ID)

            if (action != null && reminderId != null) {
                val reminder = viewModel.reminders.value?.find { it.id == reminderId }
                if (reminder != null) {
                    when (action) {
                        ReminderActionsBottomSheet.ACTION_EDIT -> {
                            val navAction = RemindersFragmentDirections.actionRemindersFragmentToAddReminderFragment(reminderId)
                            findNavController().navigate(navAction)
                        }
                        ReminderActionsBottomSheet.ACTION_DELETE -> {
                            showDeleteConfirmationDialog(reminder)
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        remindersAdapter = RemindersAdapter { reminder ->
            val bottomSheet = ReminderActionsBottomSheet.newInstance(reminder.id)
            bottomSheet.show(childFragmentManager, ReminderActionsBottomSheet.TAG)
        }
        binding.recyclerViewReminders.apply {
            adapter = remindersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeReminders() {
        viewModel.reminders.observe(viewLifecycleOwner) { reminderList ->
            remindersAdapter.submitList(reminderList)
        }
    }

    private fun showDeleteConfirmationDialog(reminder: Reminder) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Reminder")
            .setMessage("Are you sure you want to delete the reminder \"${reminder.title}\"? This action cannot be undone.")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteReminder(reminder, requireContext().applicationContext)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}