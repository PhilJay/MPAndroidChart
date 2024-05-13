package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

/**
 * Default formatter used for formatting values inside the chart. Uses a DecimalFormat with
 * pre-calculated number of digits (depending on max and min value).
 */
open class DefaultValueFormatter(digits: Int) : IValueFormatter {
    /**
     * DecimalFormat for formatting
     */
    protected var decimalFormat: DecimalFormat? = null

    /**
     * Returns the number of decimal digits this formatter uses.
     *
     * @return
     */
    var decimalDigits = 0
        protected set

    /**
     * Constructor that specifies to how many digits the value should be formatted.
     */
    init {
        setup(digits)
    }

    /**
     * Sets up the formatter with a given number of decimal digits.
     *
     * @param digits
     */
    fun setup(digits: Int) {
        decimalDigits = digits
        val b = StringBuffer()
        for (i in 0 until digits) {
            if (i == 0) b.append(".")
            b.append("0")
        }
        decimalFormat = DecimalFormat("###,###,###,##0$b")
    }

    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String? {

        // put more logic here ...
        // avoid memory allocations here (for performance reasons)
        return decimalFormat!!.format(value.toDouble())
    }
}