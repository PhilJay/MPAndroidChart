package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.components.AxisBase
import java.text.DecimalFormat

/**
 * Formats axis labels with a fixed number of decimal digits and thousands separators. An axis creates one with its
 * computed [AxisBase.decimals] while no formatter is assigned.
 * @property decimalDigits number of decimal digits the formatter produces; 0 or less formats whole numbers.
 */
public open class DefaultAxisValueFormatter(public val decimalDigits: Int) : IAxisValueFormatter {

    /** The number format used for the labels. */
    protected val format: DecimalFormat = DecimalFormat("###,###,###,##0" + decimalPattern(decimalDigits))

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        return format.format(value.toDouble())
    }
}
