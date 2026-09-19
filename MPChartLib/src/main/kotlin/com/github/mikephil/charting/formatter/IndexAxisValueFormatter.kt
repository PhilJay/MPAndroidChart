package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.components.AxisBase
import kotlin.math.roundToInt

/**
 * Shows a fixed label per whole x value, taking the label at index x from [values]. Use it on the x-axis when the
 * x values are indices into a list of names, together with an [AxisBase.granularity] of 1.
 * @param values the labels; index 0 is shown at x = 0.
 */
open class IndexAxisValueFormatter(values: List<String> = emptyList()) : IAxisValueFormatter {


    /** The labels, one per whole x value starting at 0. */
    var values: List<String> = values
        set(value) {
            field = value
            valueCount = value.size
        }

    private var valueCount = values.size

    /**
     * @return the label at the index nearest to [value], or an empty string when that index is outside [values] or
     * [value] has a fraction of 0.5 or more.
     */
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val index = value.roundToInt()
        if (index < 0 || index >= valueCount || index != value.toInt()) return ""
        return values[index]
    }
}
