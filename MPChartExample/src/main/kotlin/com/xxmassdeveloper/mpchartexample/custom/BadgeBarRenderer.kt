package com.xxmassdeveloper.mpchartexample.custom

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws every bar value on a rounded badge. Only [drawValue] is replaced, so the bars, the stacked case, the
 * icons and the bounds checks stay as the built-in renderer does them.
 */
class BadgeBarRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler,
    badgeColor: Int
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = badgeColor }

    private val badge = RectF()

    override fun drawValue(
        c: Canvas,
        formatter: IValueFormatter,
        value: Float,
        entry: Entry<*>,
        dataSetIndex: Int,
        x: Float,
        y: Float,
        color: Int
    ) {
        val text = formatter.getFormattedValue(value, entry, dataSetIndex, viewPortHandler)
        val padding = Utils.convertDpToPixel(5f)
        val halfWidth = paintValues.measureText(text) / 2f + padding

        badge.set(x - halfWidth, y - paintValues.textSize - padding / 2f, x + halfWidth, y + padding / 2f)
        val radius = badge.height() / 2f
        c.drawRoundRect(badge, radius, radius, badgePaint)

        super.drawValue(c, formatter, value, entry, dataSetIndex, x, y, color)
    }
}
