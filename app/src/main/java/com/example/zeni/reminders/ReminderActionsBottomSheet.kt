package com.example.zeni.reminders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.zeni.databinding.BottomSheetReminderActionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReminderActionsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetReminderActionsBinding? = null
    private val binding get() = _binding!!

    private val reminderId: String by lazy {
        requireArguments().getString(ARG_REMINDER_ID)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetReminderActionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewActionEdit.setOnClickListener {
            setFragmentResult(REQUEST_KEY, bundleOf(KEY_ACTION to ACTION_EDIT, KEY_REMINDER_ID to reminderId))
            dismiss()
        }

        binding.textViewActionDelete.setOnClickListener {
            setFragmentResult(REQUEST_KEY, bundleOf(KEY_ACTION to ACTION_DELETE, KEY_REMINDER_ID to reminderId))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ReminderActionsBottomSheet"
        const val REQUEST_KEY = "reminder_actions_request"
        const val KEY_ACTION = "action"
        const val KEY_REMINDER_ID = "reminder_id"
        const val ACTION_EDIT = "edit"
        const val ACTION_DELETE = "delete"
        private const val ARG_REMINDER_ID = "arg_reminder_id"

        fun newInstance(reminderId: String): ReminderActionsBottomSheet {
            return ReminderActionsBottomSheet().apply {
                arguments = bundleOf(ARG_REMINDER_ID to reminderId)
            }
        }
    }
}
