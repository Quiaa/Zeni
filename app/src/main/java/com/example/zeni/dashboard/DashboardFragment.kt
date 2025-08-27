package com.example.zeni.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeni.R
import com.example.zeni.databinding.FragmentDashboardBinding
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        binding.buttonSignOut.setOnClickListener {
            viewModel.signOut()
            findNavController().navigate(R.id.splashFragment)
        }

        binding.fabAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addTransactionFragment)
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.recyclerViewTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { firebaseUser ->
            if (firebaseUser != null) {
                binding.textViewUserEmail.text = firebaseUser.email
            } else {
                findNavController().navigate(R.id.splashFragment)
            }
        }

        // Observe the list of transactions
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            // Submit the new list to the adapter.
            // ListAdapter will efficiently calculate and apply the changes.
            transactionAdapter.submitList(transactions)
        }

        // Observe the calculated balance
        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
            binding.textViewBalanceValue.text = format.format(balance)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}