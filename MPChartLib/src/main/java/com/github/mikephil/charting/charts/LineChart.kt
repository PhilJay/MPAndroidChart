package com.github.mikephil.charting.charts

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer
import java.util.Locale

open class LineChart : BarLineChartBase<LineData?>, LineDataProvider {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun init() {
        super.init()
        mRenderer = LineChartRenderer(this, mAnimator, mViewPortHandler)
    }

    override val lineData: LineData
        get() {
            mData?.let {
                return it
            } ?: run {
                return LineData()
            }
        }

    public override fun onDetachedFromWindow() {
        // releases the bitmap in the renderer to avoid oom error
        if (mRenderer != null && mRenderer is LineChartRenderer) {
            (mRenderer as LineChartRenderer).releaseBitmap()
        }
        super.onDetachedFromWindow()
    }

    override fun getAccessibilityDescription(): String {
        val lineData = lineData
        val numberOfPoints = lineData.entryCount

        // Min and max values...
        val yAxisValueFormatter = axisLeft.valueFormatter
        val minVal = yAxisValueFormatter.getFormattedValue(lineData.yMin, null)
        val maxVal = yAxisValueFormatter.getFormattedValue(lineData.yMax, null)

        // Data range...
        val xAxisValueFormatter = xAxis.valueFormatter
        val minRange = xAxisValueFormatter.getFormattedValue(lineData.xMin, null)
        val maxRange = xAxisValueFormatter.getFormattedValue(lineData.xMax, null)
        val entries = if (numberOfPoints == 1) "entry" else "entries"
        return String.format(
            Locale.getDefault(), "The line chart has %d %s. " +
                    "The minimum value is %s and maximum value is %s." +
                    "Data ranges from %s to %s.",
            numberOfPoints, entries, minVal, maxVal, minRange, maxRange
        )
    }
}
