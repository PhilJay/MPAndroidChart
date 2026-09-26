package com.github.mikephil.charting.charts

import android.graphics.RectF
import android.graphics.Paint
import android.graphics.Canvas
import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.interfaces.dataprovider.ScatterDataProvider
import com.github.mikephil.charting.renderer.ScatterChartRenderer

/**
 * Chart that draws [ScatterData] as one shape per entry. It implements [ScatterDataProvider] for the
 * scatter renderer. Circles and squares draw fastest, triangles slowest.
 */
public open class ScatterChart @JvmOverloads constructor(
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
    public enum class ScatterShape(private val shapeIdentifier: String) {
        SQUARE("SQUARE"),
        CIRCLE("CIRCLE"),
        TRIANGLE("TRIANGLE"),
        CROSS("CROSS"),
        X("X"),
        CHEVRON_UP("CHEVRON_UP"),
        CHEVRON_DOWN("CHEVRON_DOWN");

        override fun toString(): String = shapeIdentifier
    }

    override fun drawNoDataIcon(canvas: Canvas, bounds: RectF, paint: Paint) {
        paint.style = Paint.Style.FILL
        val r = bounds.height() / 7f
        for ((fx, fy) in listOf(0.15f to 0.75f, 0.4f to 0.35f, 0.62f to 0.6f, 0.85f to 0.2f)) {
            canvas.drawCircle(bounds.left + bounds.width() * fx, bounds.top + bounds.height() * fy, r, paint)
        }
    }
}
