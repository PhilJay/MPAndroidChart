package com.github.mikephil.charting.charts

import android.graphics.RectF
import android.graphics.Paint
import android.graphics.Canvas
import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider
import com.github.mikephil.charting.renderer.BubbleChartRenderer

/**
 * Chart that draws [BubbleData] as circles whose area, not radius, represents the entry size.
 * It implements [BubbleDataProvider] for the bubble renderer.
 */
public open class BubbleChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BarLineChartBase<BubbleData>(context, attrs, defStyle), BubbleDataProvider {

    override fun init() {
        super.init()

        renderer = BubbleChartRenderer(this, animator, viewPortHandler)
    }

    override val bubbleData: BubbleData?
        get() = data

    override fun drawNoDataIcon(canvas: Canvas, bounds: RectF, paint: Paint) {
        paint.style = Paint.Style.FILL
        val h = bounds.height()
        canvas.drawCircle(bounds.left + bounds.width() * 0.25f, bounds.top + h * 0.62f, h * 0.24f, paint)
        canvas.drawCircle(bounds.left + bounds.width() * 0.66f, bounds.top + h * 0.38f, h * 0.34f, paint)
    }
}
