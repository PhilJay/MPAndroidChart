package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.components.AxisBase
import kotlin.math.roundToInt

/**
 * This formatter is used for passing an array of x-axis labels, on whole x steps.
 */
open class IndexAxisValueFormatter : IAxisValueFormatter {

    var values: Array<String> = arrayOf()

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    constructor(values: Array<String>?) {
        if (values != null)
            this.values = values
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    constructor(values: Collection<String>?) {
        if (values != null)
            this.values = values.toTypedArray()
    }

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val index = value.roundToInt()
        return if (index < 0 || index >= values.size || index != value.toInt())
            ""
        else
            values[index]
    }

}
