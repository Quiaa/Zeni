// File: app/src/main/java/com/example/zeni/dashboard/CurrencyValueFormatter.kt
package com.example.zeni.dashboard

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.util.*

// Custom formatter to display pie chart values as currency.
class CurrencyValueFormatter : ValueFormatter() {
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun getFormattedValue(value: Float): String {
        // We only show the value, not the category label here.
        // The library will handle the category label separately.
        return currencyFormat.format(value)
    }
}