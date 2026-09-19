package com.github.mikephil.charting.charts

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Keep
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider
import com.github.mikephil.charting.renderer.ScatterChartRenderer

/**
 * Chart that draws [ScatterData] as one shape per entry. It implements [ScatterDataProvider] for the
 * scatter renderer. Circles and squares draw fastest, triangles slowest.
 */
@Keep
open class ScatterChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BarLineChartBase<ScatterData>(context, attrs, defStyle), ScatterDataProvider {

    override fun init() {
        super.init()

        renderer = ScatterChartRenderer(this, animator, viewPortHandler)

        xAxis.spaceMin = 0.5f
        xAxis.spaceMax = 0.5f
    }

    override val scatterData: ScatterData?
        get() = data

    /** Built-in shapes a scatter data set can be drawn with. Setting one on the data set picks its renderer. */
    enum class ScatterShape(private val shapeIdentifier: String) {
        SQUARE("SQUARE"),
        CIRCLE("CIRCLE"),
        TRIANGLE("TRIANGLE"),
        CROSS("CROSS"),
        X("X"),
        CHEVRON_UP("CHEVRON_UP"),
        CHEVRON_DOWN("CHEVRON_DOWN");

        override fun toString(): String = shapeIdentifier
    }
}
