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
import com.example.zeni.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Get a reference to the AuthViewModel
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAuthResult() // Start observing the result

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Call the ViewModel to handle login
                viewModel.loginUser(email, password)
            } else {
                Toast.makeText(context, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    // Function to observe the LiveData from the ViewModel
    private fun observeAuthResult() {
        viewModel.authResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                // Login success, navigate to dashboard
                Log.d("LoginFragment", "Authentication successful.")
                findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
            }.onFailure { e ->
                // Login failed, show an error message
                Log.w("LoginFragment", "Authentication failed.", e)
                Toast.makeText(context, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}