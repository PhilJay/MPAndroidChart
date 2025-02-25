package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.components.AxisBase
import java.text.DecimalFormat

open class DefaultAxisValueFormatter(digits: Int) : IAxisValueFormatter {
    /**
     * decimal format for formatting
     */
    protected var decimalFormat: DecimalFormat
    /**
     * Returns the number of decimal digits this formatter uses or -1, if unspecified.
     */
    /**
     * the number of decimal digits this formatter uses
     */
    var decimalDigits = 0
        protected set

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     */
    init {
        decimalDigits = digits
        val b = StringBuffer()
        for (i in 0 until digits) {
            if (i == 0) b.append(".")
            b.append("0")
        }
        decimalFormat = DecimalFormat("###,###,###,##0$b")
    }

    override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
        // avoid memory allocations here (for performance)
        return decimalFormat.format(value.toDouble())
    }
}
