// File: app/src/main/java/com/example/zeni/dashboard/DashboardFragment.kt
package com.example.zeni.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeni.R
import com.example.zeni.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
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
        setupPieChart()
        observeViewModel()

        binding.buttonSignOut.setOnClickListener {
            viewModel.signOut()
            findNavController().navigate(R.id.splashFragment)
        }

        binding.fabAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addTransactionFragment)
        }

        binding.buttonViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_allTransactionsFragment)
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter { /* Long click is handled in AllTransactionsFragment */ }
        binding.recyclerViewTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setEntryLabelColor(Color.BLACK)
            legend.isEnabled = false
        }
    }

    // Renamed from setupObservers to observeViewModel
    private fun observeViewModel() {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

        viewModel.user.observe(viewLifecycleOwner) { firebaseUser ->
            if (firebaseUser != null) {
                binding.textViewUserEmail.text = firebaseUser.email
            } else {
                findNavController().navigate(R.id.splashFragment)
            }
        }

        viewModel.recentTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
        }

        // --- UPDATE OBSERVERS ---
        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.textViewBalanceValue.text = currencyFormat.format(balance)
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.textViewIncomeValue.text = currencyFormat.format(income)
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.textViewExpenseValue.text = currencyFormat.format(expense)
        }

        viewModel.expenseByCategory.observe(viewLifecycleOwner) { pieEntries ->
            updatePieChart(pieEntries)
        }
    }

    private fun updatePieChart(entries: List<PieEntry>) {
        if (entries.isEmpty()) {
            binding.pieChart.visibility = View.GONE
            return
        }
        binding.pieChart.visibility = View.VISIBLE

        val dataSet = PieDataSet(entries, "Expenses by Category")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        // Set the custom currency formatter
        dataSet.valueFormatter = CurrencyValueFormatter()

        val pieData = PieData(dataSet)
        binding.pieChart.data = pieData
        binding.pieChart.invalidate() // Refresh the chart
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}