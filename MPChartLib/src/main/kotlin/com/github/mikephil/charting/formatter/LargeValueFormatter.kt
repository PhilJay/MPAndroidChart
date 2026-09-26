package com.github.mikephil.charting.formatter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.round

/**
 * Shortens large numbers with a suffix per power of one thousand: 856 stays 856, 5821 becomes 5.8k, 101800 becomes
 * 102k, 7800000 becomes 7.8m and 1000000000 becomes 1b. Works as value formatter and as axis formatter.
 * Numbers below one thousand and numbers beyond the last entry of [suffix] are written out with one decimal.
 * @param appendix text appended after the shortened number.
 */
public open class LargeValueFormatter(appendix: String = "") : IValueFormatter, IAxisValueFormatter {

    /** Suffix per power of one thousand, starting at 10^0. Default "", "k", "m", "b", "t". */
    public var suffix: List<String> = listOf("", "k", "m", "b", "t")

    /** Text appended after the shortened number. */
    public var appendix: String = appendix

    private val plain = DecimalFormat("###,###,##0.#")
    private val rounded = DecimalFormat("###,###,##0")

    override fun getFormattedValue(value: Float, entry: Entry<*>, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
        return makePretty(value.toDouble()) + appendix
    }

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        return makePretty(value.toDouble()) + appendix
    }

    /** Always 0; the shortened numbers carry their own decimals. */
    public val decimalDigits: Int
        get() = 0

    private fun makePretty(number: Double): String {
        if (!number.isFinite()) return number.toString()

        var scaled = number
        var step = 0
        // Compare what would be printed, so a value that rounds up to 1000 moves on to the next suffix.
        while (step < suffix.size - 1 && abs(asPrinted(scaled)) >= 1000.0) {
            scaled /= 1000.0
            step++
        }
        val text = if (abs(scaled) >= 100.0) rounded.format(scaled) else plain.format(scaled)
        return text + suffix.getOrElse(step) { "" }
    }

    private fun asPrinted(value: Double): Double {
        return if (abs(value) >= 100.0) round(value) else round(value * 10.0) / 10.0
    }
}
