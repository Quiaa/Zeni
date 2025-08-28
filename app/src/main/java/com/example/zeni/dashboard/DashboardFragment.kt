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
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var pieLegendAdapter: PieLegendAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
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

    private fun setupRecyclerViews() {
        transactionAdapter = TransactionAdapter { /* ... */ }
        binding.recyclerViewTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Setup for the new legend RecyclerView
        binding.recyclerViewPieLegend.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setDrawEntryLabels(false)
            legend.isEnabled = false
            setUsePercentValues(true) // Crucial for PercentFormatter

            // Add the click listener
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e is PieEntry) {
                        viewModel.onSliceSelected(e)
                    }
                }

                override fun onNothingSelected() {
                    viewModel.onSliceSelected(null)
                }
            })
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

        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.textViewBalanceValue.text = currencyFormat.format(balance)
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.textViewIncomeValue.text = currencyFormat.format(income)
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.textViewExpenseValue.text = currencyFormat.format(expense)
        }

        viewModel.pieChartData.observe(viewLifecycleOwner) { (entries, totalExpense) ->
            updatePieChart(entries, totalExpense)
        }

        viewModel.selectedSlice.observe(viewLifecycleOwner) { selectedEntry ->
            if (selectedEntry == null) {
                // If nothing is selected, show total expense
                val totalExpense = viewModel.totalExpense.value ?: 0.0
                binding.pieChart.centerText = "Total Expense\n${currencyFormat.format(totalExpense)}"
            } else {
                // If a slice is selected, show its details
                binding.pieChart.centerText = "${selectedEntry.label}\n${currencyFormat.format(selectedEntry.value)}"
            }
        }
    }

    private fun updatePieChart(entries: List<PieEntry>, totalExpense: Double) {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

        // Set initial center text
        binding.pieChart.centerText = "Total Expense\n${currencyFormat.format(totalExpense)}"
        binding.pieChart.setCenterTextSize(18f)

        if (entries.isEmpty()) {
            binding.cardViewPieChart.visibility = View.GONE
            return
        }
        binding.cardViewPieChart.visibility = View.VISIBLE

        val dataSet = PieDataSet(entries, "Expenses")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        // --- NEW STYLING FOR PERCENTAGES ---
        dataSet.setDrawValues(true) // We want to draw values (percentages)
        dataSet.valueFormatter = PercentFormatter(binding.pieChart) // Format values as percentages
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_primary)

        // Configure lines pointing to the percentage values
        dataSet.valueLinePart1OffsetPercentage = 100f
        dataSet.valueLinePart1Length = 0.5f
        dataSet.valueLinePart2Length = 0.2f
        dataSet.valueLineColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val pieData = PieData(dataSet)
        binding.pieChart.data = pieData
        binding.pieChart.invalidate() // Refresh chart

        // Update custom legend
        val legendData = dataSet.colors.zip(entries)
        pieLegendAdapter = PieLegendAdapter(legendData, totalExpense.toFloat())
        binding.recyclerViewPieLegend.adapter = pieLegendAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}