package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Superclass of all render classes for the different data types (line, bar, ...).
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class DataRenderer(
    /**
     * the animator object used to perform animations on the chart data
     */
    @JvmField protected var animator: ChartAnimator, viewPortHandler: ViewPortHandler
) : Renderer(viewPortHandler) {
    /**
     * Returns the Paint object used for rendering.
     */
    /**
     * main paint object used for rendering
     */
    var paintRender: Paint
        protected set

    /**
     * Returns the Paint object this renderer uses for drawing highlight
     * indicators.
     */
    /**
     * paint used for highlighting values
     */
    var paintHighlight: Paint
        protected set

    protected var drawPaint: Paint

    /**
     * Returns the Paint object this renderer uses for drawing the values (value-text).
     */
    /**
     * paint object for drawing values (text representing values of chart
     * entries)
     */
    var paintValues: Paint
        protected set

    init {
        paintRender = Paint(Paint.ANTI_ALIAS_FLAG)
        paintRender.style = Paint.Style.FILL

        drawPaint = Paint(Paint.DITHER_FLAG)

        paintValues = Paint(Paint.ANTI_ALIAS_FLAG)
        paintValues.color = Color.rgb(63, 63, 63)
        paintValues.textAlign = Align.CENTER
        paintValues.textSize = Utils.convertDpToPixel(9f)

        paintHighlight = Paint(Paint.ANTI_ALIAS_FLAG)
        paintHighlight.style = Paint.Style.STROKE
        paintHighlight.strokeWidth = 2f
        paintHighlight.color = Color.rgb(255, 187, 115)
    }

    protected open fun isDrawingValuesAllowed(chart: ChartInterface): Boolean {
        return (chart.data!!.entryCount < chart.maxVisibleCount
                * viewPortHandler.scaleX)
    }

    /**
     * Applies the required styling (provided by the DataSet) to the value-paint
     * object.
     */
    protected fun applyValueTextStyle(set: IDataSet<*>) {
        paintValues.setTypeface(set.valueTypeface)
        paintValues.textSize = set.valueTextSize
    }

    /**
     * Initializes the buffers used for rendering with a new size. Since this
     * method performs memory allocations, it should only be called if
     * necessary.
     */
    abstract fun initBuffers()

    /**
     * Draws the actual data in form of lines, bars, ... depending on Renderer subclass.
     */
    abstract fun drawData(c: Canvas)

    /**
     * Loops over all Entries and draws their values.
     */
    abstract fun drawValues(c: Canvas)

    /**
     * Draws the value of the given entry by using the provided IValueFormatter.
     *
     * @param c            canvas
     * @param formatter    formatter for custom value-formatting
     * @param value        the value to be drawn
     * @param entry        the entry the value belongs to
     * @param dataSetIndex the index of the DataSet the drawn Entry belongs to
     * @param x            position
     * @param y            position
     */
    fun drawValue(c: Canvas, formatter: IValueFormatter, value: Float, entry: Entry?, dataSetIndex: Int, x: Float, y: Float, color: Int) {
        paintValues.color = color
        c.drawText(formatter.getFormattedValue(value, entry, dataSetIndex, viewPortHandler)!!, x, y, paintValues)
    }

    /**
     * Draws any kind of additional information (e.g. line-circles).
     */
    abstract fun drawExtras(c: Canvas)

    /**
     * Draws all highlight indicators for the values that are currently highlighted.
     * @param indices the highlighted values
     */
    abstract fun drawHighlighted(c: Canvas, indices: Array<Highlight>)
}
