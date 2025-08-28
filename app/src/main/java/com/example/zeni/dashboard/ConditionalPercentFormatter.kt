package com.example.zeni.dashboard

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class ConditionalPercentFormatter(private val pieChart: PieChart) : ValueFormatter() {

    private val format = DecimalFormat("###,###,##0.0")

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        // The value is the raw value of the slice, not the percentage
        // We calculate the percentage manually
        val total = pieChart.data.yValueSum
        if (total == 0f) {
            return ""
        }
        val percent = (value / total) * 100
        return if (percent < 2) {
            ""
        } else {
            format.format(percent) + " %"
        }
    }
}
