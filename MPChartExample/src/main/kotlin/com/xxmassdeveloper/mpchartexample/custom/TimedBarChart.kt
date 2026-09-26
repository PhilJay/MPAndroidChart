package com.xxmassdeveloper.mpchartexample.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarChart

/** Bar chart that times its own draw, so a readout can show what the chart costs rather than the window. */
class TimedBarChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BarChart(context, attrs, defStyle) {

    val frameTimes = FrameTimes()

    override fun onDraw(canvas: Canvas) {
        val startedAt = System.nanoTime()
        super.onDraw(canvas)
        frameTimes.record(System.nanoTime() - startedAt)
    }
}
