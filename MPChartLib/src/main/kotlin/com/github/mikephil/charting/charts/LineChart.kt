package com.github.mikephil.charting.charts

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Keep
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer

/**
 * Chart that draws [LineData] as straight, stepped or curved lines with optional circles and filled
 * areas. It implements [LineDataProvider] for the line renderer.
 */
@Keep
open class LineChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BarLineChartBase<LineData>(context, attrs, defStyle), LineDataProvider {

    override fun init() {
        super.init()

        renderer = LineChartRenderer(this, animator, viewPortHandler)
    }

    override val lineData: LineData?
        get() = data

    override fun onDetachedFromWindow() {
        if (::renderer.isInitialized) {
            (renderer as? LineChartRenderer)?.releaseBitmap()
        }
        super.onDetachedFromWindow()
    }
}
