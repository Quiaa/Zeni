// File: app/src/main/java/com/example/zeni/reminders/RemindersFragment.kt
package com.example.zeni.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeni.databinding.FragmentRemindersBinding
import com.example.zeni.R
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

        binding.buttonAddReminder.setOnClickListener {
            // Navigate to AddReminderFragment
            findNavController().navigate(R.id.action_remindersFragment_to_addReminderFragment)
        }
    }

    private fun setupRecyclerView() {
        remindersAdapter = RemindersAdapter()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}