package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.components.AxisBase

/**
 * Formats the labels of an axis. Set it with [AxisBase.valueFormatter].
 * Usage: `IAxisValueFormatter { value, _ -> value.toInt().toString() }`.
 * Called for every label on every frame, so avoid allocations and heavy work.
 */
fun interface IAxisValueFormatter {

    /**
     * @param value the axis value to format, in value space
     * @param axis the axis the value belongs to
     * @return the label text
     */
    fun getFormattedValue(value: Float, axis: AxisBase): String
}
