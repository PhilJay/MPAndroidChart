package info.appdev.chartexample.custom

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

@Suppress("unused")
class YearXAxisFormatter : IAxisValueFormatter {
    private val months = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    )

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val percent = value / axis!!.mAxisRange
        return months[(months.size * percent).toInt()]
    }
}
