// File: app/src/main/java/com/example/zeni/splash/SplashFragment.kt
package com.example.zeni.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.zeni.R
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    // Declare a FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // Use a Handler to delay the navigation check slightly.
        // This can help prevent screen flicker.
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // User is signed in, navigate to Dashboard
                findNavController().navigate(R.id.action_splashFragment_to_dashboardFragment)
            } else {
                // No user is signed in, navigate to Login
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }, 2000) // 2 second delay
    }
}