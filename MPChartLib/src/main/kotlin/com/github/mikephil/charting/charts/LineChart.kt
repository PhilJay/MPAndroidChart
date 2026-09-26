package com.github.mikephil.charting.charts

import android.graphics.Path
import android.graphics.RectF
import android.graphics.Paint
import android.graphics.Canvas
import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer

/**
 * Chart that draws [LineData] as straight, stepped or curved lines with optional circles and filled
 * areas. It implements [LineDataProvider] for the line renderer.
 */
public open class LineChart @JvmOverloads constructor(
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

    override fun drawNoDataIcon(canvas: Canvas, bounds: RectF, paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = bounds.height() / 8f
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        val inset = paint.strokeWidth / 2f
        val ys = floatArrayOf(0.8f, 0.45f, 0.65f, 0.15f)
        val path = Path()
        for (i in ys.indices) {
            val x = bounds.left + inset + (bounds.width() - 2 * inset) * i / (ys.size - 1)
            val y = bounds.top + inset + (bounds.height() - 2 * inset) * ys[i]
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, paint)
    }
}
