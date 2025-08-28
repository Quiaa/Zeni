package com.example.zeni.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zeni.databinding.ItemPieLegendBinding
import com.github.mikephil.charting.data.PieEntry
import java.text.NumberFormat
import java.util.*

import kotlin.math.min

class PieLegendAdapter(private var legendData: List<Pair<Int, PieEntry>>, private var total: Float) :
    RecyclerView.Adapter<PieLegendAdapter.LegendViewHolder>() {

    private var isExpanded = false
    private val collapsedItemCount = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegendViewHolder {
        val binding = ItemPieLegendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LegendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LegendViewHolder, position: Int) {
        holder.bind(legendData[position], total)
    }

    override fun getItemCount(): Int {
        return if (isExpanded) {
            legendData.size
        } else {
            min(legendData.size, collapsedItemCount)
        }
    }

    fun toggleExpansion() {
        isExpanded = !isExpanded
        notifyDataSetChanged()
    }

    fun isExpanded(): Boolean {
        return isExpanded
    }

    fun getOriginalItemCount(): Int {
        return legendData.size
    }

    inner class LegendViewHolder(private val binding: ItemPieLegendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entryData: Pair<Int, PieEntry>, totalValue: Float) {
            val (color, entry) = entryData
            binding.viewColor.setBackgroundColor(color)
            binding.textViewCategoryName.text = entry.label

            // Calculate and set percentage
            val percentage = if (totalValue > 0) (entry.value / totalValue) * 100 else 0f
            binding.textViewPercentage.text = String.format(Locale.US, "%%%.1f", percentage)

            // Format and set amount
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
            binding.textViewCategoryAmount.text = currencyFormat.format(entry.value)
        }
    }
}