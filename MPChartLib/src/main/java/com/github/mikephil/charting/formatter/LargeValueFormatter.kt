package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

/**
 * Predefined value-formatter that formats large numbers in a pretty way.
 * Outputs: 856 = 856; 1000 = 1k; 5821 = 5.8k; 10500 = 10k; 101800 = 102k;
 * 2000000 = 2m; 7800000 = 7.8m; 92150000 = 92m; 123200000 = 123m; 9999999 =
 * 10m; 1000000000 = 1b;
 * Special thanks to Roman Gromov
 * (https://github.com/romangromov) for this piece of code.
 */
open class LargeValueFormatter() : IValueFormatter, IAxisValueFormatter {

    private var suffix = arrayOf(
        "", "k", "m", "b", "t"
    )
    private var maxLength = 5
    private val decimalFormat: DecimalFormat = DecimalFormat("###E00")
    private var text = ""

    /**
     * Creates a formatter that appends a specified text to the result string
     *
     * @param appendix a text that will be appended
     */
    constructor(appendix: String) : this() {
        text = appendix
    }

    // IValueFormatter
    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
        return makePretty(value.toDouble()) + text
    }

    // IAxisValueFormatter
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return makePretty(value.toDouble()) + text
    }

    /**
     * Set an appendix text to be added at the end of the formatted value.
     *
     * @param appendix
     */
    fun setAppendix(appendix: String) {
        text = appendix
    }

    /**
     * Set custom suffix to be appended after the values.
     * Default suffix: ["", "k", "m", "b", "t"]
     *
     * @param suffixArray new suffix
     */
    fun setSuffix(suffixArray: Array<String>) {
        suffix = suffixArray
    }

    fun setMaxLength(max: Int) {
        maxLength = max
    }

    /**
     * Formats each number properly. Special thanks to Roman Gromov
     * (https://github.com/romangromov) for this piece of code.
     */
    private fun makePretty(number: Double): String {
        var decimalFormat = decimalFormat.format(number)
        val numericValue1 = Character.getNumericValue(decimalFormat[decimalFormat.length - 1])
        val numericValue2 = Character.getNumericValue(decimalFormat[decimalFormat.length - 2])
        val combined = Integer.valueOf(numericValue2.toString() + "" + numericValue1)
        decimalFormat = decimalFormat.replace("E[0-9][0-9]".toRegex(), suffix[combined / 3])
        while (decimalFormat.length > maxLength || decimalFormat.matches("[0-9]+\\.[a-z]".toRegex())) {
            decimalFormat = decimalFormat.substring(0, decimalFormat.length - 2) + decimalFormat.substring(decimalFormat.length - 1)
        }
        return decimalFormat
    }

}
