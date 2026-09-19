package com.github.mikephil.charting.highlight

import com.github.mikephil.charting.components.YAxis

/**
 * Identifies one selected value in a chart: which entry it is, where it sits on screen and which
 * data set, axis and, for stacked bars, which stack value it belongs to.
 *
 * Highlighters create these from touch positions; charts pass them to
 * [com.github.mikephil.charting.listener.OnChartValueSelectedListener] and use them to draw
 * highlight indicators and markers. You can also create one yourself and pass it to
 * `chart.highlightValue(...)`.
 */
class Highlight {

    /** The x value of the highlighted entry in value space. For pie and radar charts this is the entry index. */
    val x: Float

    /** The y value of the highlighted entry in value space; NaN when the highlight was created from x only. */
    val y: Float

    /** The x position of the highlighted entry in pixels; 0 when created without pixel positions. */
    var xPx = 0f
        private set

    /** The y position of the highlighted entry in pixels; 0 when created without pixel positions. */
    var yPx = 0f
        private set

    /**
     * Index of the data object inside a combined chart's data, for example 0 for its line data and 1 for
     * its bar data. -1 for charts with a single data object.
     */
    var dataIndex = -1

    /** Index of the data set the highlighted entry belongs to. */
    val dataSetIndex: Int

    /** Index of the selected value inside a stacked bar entry; -1 when the entry is not stacked. */
    var stackIndex = -1

    /** The y axis the highlighted entry is plotted against; null when unknown. */
    var axis: YAxis.AxisDependency? = null
        private set

    /** The x position in pixels where this highlight was last drawn, set by the renderer through [setDraw]. */
    var drawX = 0f
        private set

    /** The y position in pixels where this highlight was last drawn, set by the renderer through [setDraw]. */
    var drawY = 0f
        private set

    /**
     * Creates a highlight from values only. Pixel positions stay 0 and the axis stays null.
     *
     * @param x the x value of the entry.
     * @param y the y value of the entry, used to pick between entries that share the same x.
     * @param dataSetIndex index of the data set.
     * @param dataIndex index of the data object in a combined chart, -1 otherwise.
     */
    constructor(x: Float, y: Float, dataSetIndex: Int, dataIndex: Int = -1) {
        this.x = x
        this.y = y
        this.dataSetIndex = dataSetIndex
        this.dataIndex = dataIndex
    }

    /**
     * Creates a highlight for one value of a stacked bar entry. The y value is NaN.
     *
     * @param x the x value of the bar entry.
     * @param dataSetIndex index of the data set.
     * @param stackIndex index of the value inside the stack.
     */
    constructor(x: Float, dataSetIndex: Int, stackIndex: Int) : this(x, Float.NaN, dataSetIndex) {
        this.stackIndex = stackIndex
    }

    /**
     * Creates a highlight with values, pixel position and axis, as the highlighters do.
     *
     * @param x the x value of the entry.
     * @param y the y value of the entry.
     * @param xPx the x position of the entry in pixels.
     * @param yPx the y position of the entry in pixels.
     * @param dataSetIndex index of the data set.
     * @param axis the y axis the entry is plotted against.
     */
    constructor(x: Float, y: Float, xPx: Float, yPx: Float, dataSetIndex: Int, axis: YAxis.AxisDependency?) {
        this.x = x
        this.y = y
        this.xPx = xPx
        this.yPx = yPx
        this.dataSetIndex = dataSetIndex
        this.axis = axis
    }

    /**
     * Creates a highlight for one value of a stacked bar entry with its pixel position and axis.
     *
     * @param x the x value of the bar entry.
     * @param y the y value of the bar entry.
     * @param xPx the x position in pixels.
     * @param yPx the y position of the selected stack value in pixels.
     * @param dataSetIndex index of the data set.
     * @param stackIndex index of the value inside the stack.
     * @param axis the y axis the entry is plotted against.
     */
    constructor(x: Float, y: Float, xPx: Float, yPx: Float, dataSetIndex: Int, stackIndex: Int, axis: YAxis.AxisDependency?) :
        this(x, y, xPx, yPx, dataSetIndex, axis) {
        this.stackIndex = stackIndex
    }

    /** True if this highlight points at one value inside a stacked bar entry. */
    val isStacked: Boolean
        get() = stackIndex >= 0

    /** Records the pixel position where this highlight was drawn, see [drawX] and [drawY]. */
    fun setDraw(x: Float, y: Float) {
        drawX = x
        drawY = y
    }

    /**
     * True if [h] selects the same entry: same data set index, x value, stack index and data index.
     * The y value and pixel positions are not compared. Null returns false.
     */
    fun equalTo(h: Highlight?): Boolean {
        if (h == null) return false
        return dataSetIndex == h.dataSetIndex && x == h.x && stackIndex == h.stackIndex && dataIndex == h.dataIndex
    }

    override fun toString(): String {
        return "Highlight, x: $x, y: $y, dataSetIndex: $dataSetIndex, stackIndex (only stacked barentry): $stackIndex"
    }
}
