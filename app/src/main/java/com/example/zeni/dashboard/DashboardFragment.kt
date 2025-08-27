// File: app/src/main/java/com/example/zeni/dashboard/DashboardFragment.kt
package com.example.zeni.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.zeni.R
import com.example.zeni.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // Get a reference to the DashboardViewModel
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers() // Start observing LiveData

        // Set OnClickListener for the sign out button
        binding.buttonSignOut.setOnClickListener {
            viewModel.signOut()
            // After signing out, navigate back to the splash screen.
            // Splash screen will then redirect to the login screen.
            findNavController().navigate(R.id.splashFragment)
        }

        // TODO: Setup RecyclerView and FAB click listener
    }

    private fun setupObservers() {
        // Observe the user LiveData
        viewModel.user.observe(viewLifecycleOwner) { firebaseUser ->
            if (firebaseUser != null) {
                // If user is not null, update the UI with their email
                binding.textViewUserEmail.text = firebaseUser.email
            } else {
                // If user is null (which happens after sign out), navigate away
                // This is a safeguard, the primary navigation is in the sign out button click
                findNavController().navigate(R.id.splashFragment)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}