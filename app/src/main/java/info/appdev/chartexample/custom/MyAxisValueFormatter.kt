package info.appdev.chartexample.custom

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DecimalFormat

class MyAxisValueFormatter : IAxisValueFormatter {
    private val mFormat = DecimalFormat("###,###,###,##0.0")

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return mFormat.format(value.toDouble()) + " $"
    }
}
