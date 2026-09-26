package com.github.mikephil.charting.charts

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.highlight.BarHighlighter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer

/**
 * Chart that draws [BarData] as vertical bars, plain, grouped or stacked. It implements
 * [BarDataProvider] for the bar renderer and highlighter. See [HorizontalBarChart] for horizontal bars.
 */
public open class BarChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BarLineChartBase<BarData>(context, attrs, defStyle), BarDataProvider {

    /** Whether a tap highlights the whole stacked bar instead of the single tapped stack value. Default false. */
    override var isHighlightFullBarEnabled: Boolean = false

    /** Whether value labels sit above the bar top instead of below it. Default true. */
    override var isDrawValueAboveBarEnabled: Boolean = true

    /** Whether a grey area up to the axis maximum is drawn behind each bar. Costs performance. Default false. */
    override var isDrawBarShadowEnabled: Boolean = false

    /**
     * Whether the x axis range is widened by half a bar width on each side so the outer bars are
     * fully visible. Takes effect on the next [notifyDataSetChanged].
     */
    public var isFitBarsEnabled: Boolean = false

    override fun init() {
        super.init()

        renderer = BarChartRenderer(this, animator, viewPortHandler)

        highlighter = BarHighlighter(this)

        xAxis.spaceMin = 0.5f
        xAxis.spaceMax = 0.5f
    }

    override fun calcMinMax() {
        val data = data ?: return

        if (isFitBarsEnabled) {
            xAxis.calculate(data.xMin - data.barWidth / 2f, data.xMax + data.barWidth / 2f)
        } else {
            xAxis.calculate(data.xMin, data.xMax)
        }

        axisLeft.calculate(data.getYMin(YAxis.AxisDependency.LEFT), data.getYMax(YAxis.AxisDependency.LEFT))
        axisRight.calculate(data.getYMin(YAxis.AxisDependency.RIGHT), data.getYMax(YAxis.AxisDependency.RIGHT))
    }

    override fun getHighlightByTouchPoint(x: Float, y: Float): Highlight? {
        if (data == null) {
            Log.e(LOG_TAG, "Can't select by touch. No data set.")
            return null
        }

        val h = highlighter.getHighlight(x, y)
        if (h == null || !isHighlightFullBarEnabled) return h

        return Highlight(h.x, h.y, h.xPx, h.yPx, h.dataSetIndex, -1, h.axis)
    }

    /**
     * Bounding box in pixels of the bar drawn for an entry. Use the overload with an output rectangle
     * in hot code to avoid the allocation.
     *
     * @return The bounds, or a rectangle with `Float.MIN_VALUE` on all sides when the entry is not in the data.
     */
    public fun getBarBounds(e: BarEntry<*>): RectF {
        val bounds = RectF()
        getBarBounds(e, bounds)
        return bounds
    }

    /**
     * Writes the bounding box in pixels of the bar drawn for an entry into [outputRect].
     * All sides are set to `Float.MIN_VALUE` when the entry is not in the data.
     */
    public open fun getBarBounds(e: BarEntry<*>, outputRect: RectF) {
        val data = data
        val set = data?.getDataSetForEntry(e)

        if (data == null || set == null) {
            outputRect.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE)
            return
        }

        val y = e.y
        val x = e.x

        val barWidth = data.barWidth

        val left = x - barWidth / 2f
        val right = x + barWidth / 2f
        val top = if (y >= 0) y else 0f
        val bottom = if (y <= 0) y else 0f

        outputRect.set(left, top, right, bottom)

        getTransformer(set.axisDependency).rectValueToPixel(outputRect)
    }

    override val barData: BarData?
        get() = data

    /**
     * Places the bars of all data sets side by side in groups, one group per x position, then refreshes
     * the chart. Delegates to `BarData.groupBars`, which also changes the bar width and entry x values.
     *
     * @param fromX X value where the first group starts.
     * @param groupSpace Space between two groups, in x units.
     * @param barSpace Space between two bars inside a group, in x units.
     * @throws IllegalStateException when no data has been set.
     */
    public fun groupBars(fromX: Float, groupSpace: Float, barSpace: Float) {
        val barData = barData ?: throw IllegalStateException("You need to set data for the chart before grouping bars.")
        barData.groupBars(fromX, groupSpace, barSpace)
        notifyDataSetChanged()
    }
}
