package com.github.mikephil.charting.renderer

import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.max
import kotlin.math.min

/**
 * Base class of the renderers for charts with x and y axes (bar, line, scatter, candle, bubble). Adds helpers
 * that limit drawing to the entries visible on screen.
 */
abstract class BarLineScatterCandleBubbleRenderer(animator: ChartAnimator, viewPortHandler: ViewPortHandler) : DataRenderer(animator, viewPortHandler) {

    /**
     * Index range of the entries currently visible on screen; renderers update it per data set with [XBounds.set].
     */
    protected val xBounds = XBounds()

    /**
     * Entries of the data set drawn last that are worth drawing, filled by [reduceVisible]. A renderer, and a
     * subclass overriding one of its draw methods, walks `indices` up to `count` instead of walking [xBounds].
     */
    protected val reducer = PointReducer()

    /**
     * Width in x value units of one pixel column of the last [reduceVisible], or 0 while decimation is off. A
     * renderer that paints an entry as a shape with a width of its own widens that shape to the room the shapes
     * of its column took together, at most this much, so it still covers the ground the entries left out covered.
     * `reducer.columnCounts` says how many entries each kept one stands for.
     */
    protected var reducedColumnWidth = 0f
        private set

    /**
     * How many pixels the x values are spread over, which is the width of the content rectangle for every chart
     * but the horizontal bar chart, where the x values run down the screen.
     */
    protected open val xAxisPixels: Float
        get() = viewPortHandler.contentWidth

    private val columnValues = DataSetColumnValues()

    private var reducedPositions = FloatArray(0)

    /**
     * Fills [reducer] with the entries of [dataSet] inside [xBounds] that are worth drawing in the viewport of
     * [chart], or with all of them while [BarLineScatterCandleBubbleDataProvider.isDecimationEnabled] is off.
     * Needs a preceding [XBounds.set].
     */
    protected fun reduceVisible(chart: BarLineScatterCandleBubbleDataProvider, dataSet: IBarLineScatterCandleBubbleDataSet<*>) {
        val from = xBounds.min
        val to = xBounds.min + xBounds.range

        if (!chart.isDecimationEnabled) {
            reducer.reduceNothing(from, to)
            reducedColumnWidth = 0f
            return
        }

        columnValues.readFrom(dataSet)

        val pixelWidth = xAxisPixels
        val valuesPerPixel = (chart.highestVisibleX - chart.lowestVisibleX) / max(pixelWidth, 1f)
        reducer.reduce(columnValues, from, to, chart.lowestVisibleX, valuesPerPixel, pixelWidth)

        reducedColumnWidth = valuesPerPixel
    }

    /**
     * Pixel positions of the entries [reduceVisible] picked, as x, y pairs, where y is [highOf] the entry
     * multiplied by [phaseY]. Needs a preceding [reduceVisible].
     *
     * @return a buffer reused between calls; only its first `reducer.count * 2` floats hold positions.
     */
    protected fun reducedPositions(dataSet: IBarLineScatterCandleBubbleDataSet<*>, trans: Transformer, phaseY: Float): FloatArray {
        val floats = reducer.count * 2
        if (reducedPositions.size < floats) reducedPositions = FloatArray(floats)

        for (k in 0 until reducer.count) {
            val entry = dataSet.getEntryForIndex(reducer.indices[k])
            reducedPositions[2 * k] = entry.x
            reducedPositions[2 * k + 1] = highOf(entry) * phaseY
        }

        trans.pointValuesToPixel(reducedPositions, floats)
        return reducedPositions
    }

    /** Bottom of the y range of [entry]. Charts with one y value per entry read it; candle charts read the low. */
    protected open fun lowOf(entry: Entry<*>): Float = entry.y

    /** Top of the y range of [entry]. Charts with one y value per entry read it; candle charts read the high. */
    protected open fun highOf(entry: Entry<*>): Float = entry.y

    /**
     * True while the entries of this renderer are drawn in sizes of their own, so that the reduction keeps the
     * largest of every column as well as its extremes. Only the bubble chart says yes; for everyone else asking
     * would cost a read per entry and answer 0 every time.
     */
    protected open val hasSizes: Boolean
        get() = false

    /** How much of the picture [entry] takes, for renderers whose entries have sizes of their own. */
    protected open fun sizeOf(entry: Entry<*>): Float = 0f

    /**
     * Reads the x value and the y range of the entries of one data set, keeping the entry it fetched last so the
     * several questions [PointReducer] asks about one index cost one fetch.
     */
    private inner class DataSetColumnValues : ColumnValues {

        private lateinit var dataSet: IBarLineScatterCandleBubbleDataSet<*>
        private var cachedIndex = -1
        private var cachedEntry: Entry<*>? = null

        /** Points this at [dataSet] and drops the entry memorized from the previous one. */
        fun readFrom(dataSet: IBarLineScatterCandleBubbleDataSet<*>) {
            this.dataSet = dataSet
            cachedIndex = -1
            cachedEntry = null
        }

        private fun entryAt(index: Int): Entry<*> {
            val cached = cachedEntry
            if (cached != null && cachedIndex == index) return cached
            val entry = dataSet.getEntryForIndex(index)
            cachedIndex = index
            cachedEntry = entry
            return entry
        }

        override fun xAt(index: Int) = entryAt(index).x
        override fun lowAt(index: Int) = lowOf(entryAt(index))
        override fun highAt(index: Int) = highOf(entryAt(index))
        override val hasSizes get() = this@BarLineScatterCandleBubbleRenderer.hasSizes
        override fun sizeAt(index: Int) = sizeOf(entryAt(index))
    }

    /**
     * Returns true if [set] is visible and has value labels or icons enabled.
     */
    protected fun shouldDrawValues(set: IDataSet<*>): Boolean {
        return set.isVisible && (set.isDrawValuesEnabled || set.isDrawIconsEnabled)
    }

    /**
     * Returns true if [set] draws values or icons and has at most [BarLineScatterCandleBubbleDataProvider.maxVisibleCount]
     * entries inside the visible x range of [chart], above which the labels would sit too close together to read.
     */
    protected fun shouldDrawValues(chart: BarLineScatterCandleBubbleDataProvider, set: IBarLineScatterCandleBubbleDataSet<*>): Boolean {
        return shouldDrawValues(set) && visibleEntryCount(chart, set) <= chart.maxVisibleCount
    }

    /**
     * Number of entries of [set] that lie inside the visible x range of [chart].
     */
    protected fun visibleEntryCount(chart: BarLineScatterCandleBubbleDataProvider, set: IBarLineScatterCandleBubbleDataSet<*>): Int {
        if (set.entryCount == 0) return 0

        val from = set.getEntryIndex(chart.lowestVisibleX, Float.NaN, DataSet.Rounding.DOWN).coerceAtLeast(0)
        val to = set.getEntryIndex(chart.highestVisibleX, Float.NaN, DataSet.Rounding.UP).coerceAtLeast(0)

        return max(0, to - from) + 1
    }

    /**
     * Returns true if [e] lies within the part of [set] that the x animation phase has already revealed, false for
     * null.
     */
    protected fun isInBoundsX(e: Entry<*>?, set: IBarLineScatterCandleBubbleDataSet<*>): Boolean {
        if (e == null) return false
        val entryIndex = set.getEntryIndex(e).toFloat()
        return entryIndex < set.entryCount * animator.phaseX
    }

    /**
     * Index range of the entries of one data set that are currently visible. [min] and [max] are entry indices,
     * not x values; [range] is the number of indices to draw after applying the x animation phase.
     */
    protected inner class XBounds {

        /**
         * Index of the first visible entry.
         */
        var min = 0

        /**
         * Index of the last visible entry.
         */
        var max = 0

        /**
         * Number of indices from [min] to [max], multiplied by the x animation phase.
         */
        var range = 0

        /**
         * Recomputes the bounds of [dataSet] from the lowest and highest x value visible in [chart]. The start entry
         * is rounded down and the end entry up, so the entries just outside the screen are included. Both indices are
         * 0 when the data set is empty.
         */
        fun set(chart: BarLineScatterCandleBubbleDataProvider, dataSet: IBarLineScatterCandleBubbleDataSet<*>) {
            val phaseX = max(0f, min(1f, animator.phaseX))

            val low = chart.lowestVisibleX
            val high = chart.highestVisibleX

            min = dataSet.getEntryIndex(low, Float.NaN, DataSet.Rounding.DOWN).coerceAtLeast(0)
            max = dataSet.getEntryIndex(high, Float.NaN, DataSet.Rounding.UP).coerceAtLeast(0)
            if (max < min) max = min
            range = ((max - min) * phaseX).toInt()
        }
    }
}
