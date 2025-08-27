package com.example.zeni.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.zeni.R
import com.example.zeni.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Get a reference to the AuthViewModel
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAuthResult() // Start observing the result

        binding.buttonRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Call the ViewModel to handle registration
                viewModel.registerUser(email, password)
            } else {
                Toast.makeText(context, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    // Function to observe the LiveData from the ViewModel
    private fun observeAuthResult() {
        viewModel.authResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                // Registration success, navigate to dashboard
                Log.d("RegisterFragment", "Authentication successful.")
                findNavController().navigate(R.id.action_registerFragment_to_dashboardFragment)
            }.onFailure { e ->
                // Registration failed, show an error message
                Log.w("RegisterFragment", "Authentication failed.", e)
                Toast.makeText(context, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}