package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

/**
 * Formats values with a fixed number of decimal digits and thousands separators, for example 1,234.50. The chart
 * creates one with a digit count derived from its data range and uses it wherever no formatter is set.
 * @param digits number of decimal digits.
 */
open class DefaultValueFormatter(digits: Int) : IValueFormatter {

    /** The number format built by [setup]. */
    protected lateinit var format: DecimalFormat

    /** Number of decimal digits the formatter produces. */
    var decimalDigits = 0
        protected set

    init {
        setup(digits)
    }

    /**
     * Rebuilds the number format.
     * @param digits number of decimal digits; 0 or less formats whole numbers
     */
    fun setup(digits: Int) {
        decimalDigits = digits
        format = DecimalFormat("###,###,###,##0" + decimalPattern(digits))
    }

    override fun getFormattedValue(value: Float, entry: Entry<*>, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
        return format.format(value.toDouble())
    }
}

internal fun decimalPattern(digits: Int): String {
    if (digits <= 0) return ""
    return "." + "0".repeat(digits)
}
