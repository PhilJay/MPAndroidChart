package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Base class of the renderers that draw chart data (bars, lines, slices, ...).
 *
 * The chart calls [initBuffers] whenever its data changes and then, on every draw pass, [drawData],
 * [drawHighlighted], [drawExtras] and [drawValues] in that order. Subclass it and assign the instance to
 * `chart.renderer` to change how a chart draws. All canvas coordinates are pixels.
 *
 * @property animator Supplies the animation phases (0 to 1) that scale the drawn entries.
 */
abstract class DataRenderer(protected val animator: ChartAnimator, viewPortHandler: ViewPortHandler) : Renderer(viewPortHandler) {

    /**
     * Main paint used to draw the data shapes (bars, lines, slices). Anti-aliased and filled by default; subclasses
     * change its color, style and stroke width per data set.
     */
    protected val renderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    /**
     * Paint used to draw the highlight indicator of a selected entry. A 2 px orange stroke by default; subclasses
     * restyle it.
     */
    protected val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.rgb(255, 187, 115)
    }

    /**
     * Paint used to draw the value labels of entries. Dark gray, centered, 9 dp by default; [applyValueTextStyle]
     * copies the typeface and text size of a data set into it before its values are drawn.
     */
    protected val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(63, 63, 63)
        textAlign = Paint.Align.CENTER
        textSize = Utils.convertDpToPixel(9f)
    }

    /**
     * The paint used for value labels, see [valuePaint].
     */
    val paintValues: Paint
        get() = valuePaint

    /**
     * The paint used for highlight indicators, see [highlightPaint].
     */
    val paintHighlight: Paint
        get() = highlightPaint

    /**
     * The paint used for the data shapes, see [renderPaint].
     */
    val paintRender: Paint
        get() = renderPaint

    /**
     * Draws one value label of a stacked bar entry, where [stackIndex] is the position of [value] in the stack.
     * Same as [drawValue] otherwise.
     */
    open fun drawValue(
        c: Canvas,
        formatter: IValueFormatter,
        value: Float,
        stackIndex: Int,
        entry: Entry<*>,
        dataSetIndex: Int,
        x: Float,
        y: Float,
        color: Int
    ) {
        valuePaint.color = color
        c.drawText(formatter.getStackedFormattedValue(value, stackIndex, entry, dataSetIndex, viewPortHandler), x, y, valuePaint)
    }

    /**
     * Copies the value typeface and value text size (dp, converted to pixels) of [set] into [valuePaint].
     */
    protected fun applyValueTextStyle(set: IDataSet<*>) {
        valuePaint.typeface = set.valueTypeface
        valuePaint.textSize = Utils.convertDpToPixel(set.valueTextSize)
    }

    /**
     * Creates or resizes the buffers needed for the current data. Called by the chart whenever its data changes,
     * before the next draw.
     */
    abstract fun initBuffers()

    /**
     * Draws the data of the chart (bars, lines, slices, ...) onto [c]. Called first in a draw pass, with the
     * canvas clipped to the content rectangle when the chart clips data.
     */
    abstract fun drawData(c: Canvas)

    /**
     * Draws the formatted value labels and icons of all entries onto [c]. Called last in a draw pass, after the
     * axes have been drawn.
     */
    abstract fun drawValues(c: Canvas)

    /**
     * Draws one value label with [valuePaint] in [color] at the pixel position ([x], [y]), where [y] is the text
     * baseline.
     *
     * @param formatter formats [value] of [entry], which belongs to the data set at [dataSetIndex]
     */
    open fun drawValue(c: Canvas, formatter: IValueFormatter, value: Float, entry: Entry<*>, dataSetIndex: Int, x: Float, y: Float, color: Int) {
        valuePaint.color = color
        c.drawText(formatter.getFormattedValue(value, entry, dataSetIndex, viewPortHandler), x, y, valuePaint)
    }

    /**
     * Draws whatever the data type needs in addition to the data, such as line circles, the radar web or the pie
     * hole. Called after [drawHighlighted], without the content clip.
     */
    abstract fun drawExtras(c: Canvas)

    /**
     * Draws the highlight indicators for the given selections onto [c]. Called right after [drawData].
     *
     * @param indices the highlights to draw; entries outside the visible range or in data sets with highlighting
     * disabled are skipped
     */
    abstract fun drawHighlighted(c: Canvas, indices: List<Highlight>)
}
