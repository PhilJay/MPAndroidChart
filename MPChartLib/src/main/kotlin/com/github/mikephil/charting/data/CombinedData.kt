package com.github.mikephil.charting.data

import android.util.Log
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet

/**
 * Data for a combined chart: up to one [LineData], [BarData], [ScatterData], [CandleData] and [BubbleData]
 * drawn together. The inherited [dataSets] list is rebuilt from the child data objects on every
 * recalculation, so add and remove sets through the child objects; anything added directly to [dataSets]
 * or via [addDataSet] is dropped on the next [notifyDataChanged].
 */
open class CombinedData : BarLineScatterCandleBubbleData<IBarLineScatterCandleBubbleDataSet<out Entry<*>>>() {

    /** The line part, or null for none. Assigning recomputes all ranges. */
    var lineData: LineData? = null
        set(value) {
            field = value
            notifyDataChanged()
        }

    /** The bar part, or null for none. Assigning recomputes all ranges. */
    var barData: BarData? = null
        set(value) {
            field = value
            notifyDataChanged()
        }

    /** The scatter part, or null for none. Assigning recomputes all ranges. */
    var scatterData: ScatterData? = null
        set(value) {
            field = value
            notifyDataChanged()
        }

    /** The candle part, or null for none. Assigning recomputes all ranges. */
    var candleData: CandleData? = null
        set(value) {
            field = value
            notifyDataChanged()
        }

    /** The bubble part, or null for none. Assigning recomputes all ranges. */
    var bubbleData: BubbleData? = null
        set(value) {
            field = value
            notifyDataChanged()
        }

    /**
     * Rebuilds [dataSets] from the sets of all child data objects in [allData] order and recomputes the
     * overall and per axis ranges from them. Each child recomputes its own ranges first.
     */
    public override fun calcMinMax() {
        dataSets.clear()

        yMax = -Float.MAX_VALUE
        yMin = Float.MAX_VALUE
        xMax = -Float.MAX_VALUE
        xMin = Float.MAX_VALUE

        leftAxisMax = -Float.MAX_VALUE
        leftAxisMin = Float.MAX_VALUE
        rightAxisMax = -Float.MAX_VALUE
        rightAxisMin = Float.MAX_VALUE

        for (data in allData) {
            data.notifyDataChanged()

            val sets = data.dataSets
            dataSets.addAll(sets)

            if (data.yMax > yMax) yMax = data.yMax
            if (data.yMin < yMin) yMin = data.yMin
            if (data.xMax > xMax) xMax = data.xMax
            if (data.xMin < xMin) xMin = data.xMin

            for (dataset in sets) {
                if (dataset.axisDependency == YAxis.AxisDependency.LEFT) {
                    if (dataset.yMax > leftAxisMax) leftAxisMax = dataset.yMax
                    if (dataset.yMin < leftAxisMin) leftAxisMin = dataset.yMin
                } else {
                    if (dataset.yMax > rightAxisMax) rightAxisMax = dataset.yMax
                    if (dataset.yMin < rightAxisMin) rightAxisMin = dataset.yMin
                }
            }
        }
    }

    /**
     * The child data objects that are set, always in the order line, bar, scatter, candle, bubble.
     * A position in this list is what [Highlight.dataIndex] and [getDataByIndex] refer to.
     */
    val allData: List<BarLineScatterCandleBubbleData<out IBarLineScatterCandleBubbleDataSet<out Entry<*>>>>
        get() = listOfNotNull(lineData, barData, scatterData, candleData, bubbleData)

    /**
     * Returns the child data object at [index] in [allData].
     *
     * @throws IndexOutOfBoundsException when [index] is not below the size of [allData].
     */
    fun getDataByIndex(index: Int): BarLineScatterCandleBubbleData<out IBarLineScatterCandleBubbleDataSet<out Entry<*>>> {
        return allData[index]
    }

    /** Tells every child data object to recompute its ranges, then rebuilds [dataSets] and the ranges here. */
    override fun notifyDataChanged() {
        lineData?.notifyDataChanged()
        barData?.notifyDataChanged()
        candleData?.notifyDataChanged()
        scatterData?.notifyDataChanged()
        bubbleData?.notifyDataChanged()

        calcMinMax()
    }

    /**
     * Returns the entry a highlight refers to, looked up in the child data object at [Highlight.dataIndex]
     * and the set at [Highlight.dataSetIndex]. Only entries whose x equals [Highlight.x] exactly are
     * considered; the first one whose y equals [Highlight.y] is returned, or the first one if that y is NaN.
     *
     * @return the entry, or null when either index is out of range or no entry matches.
     */
    override fun getEntryForHighlight(highlight: Highlight): Entry<*>? {
        if (highlight.dataIndex !in allData.indices) return null

        val data = getDataByIndex(highlight.dataIndex)

        if (highlight.dataSetIndex !in 0 until data.dataSetCount) return null

        val entries = data.getDataSetByIndex(highlight.dataSetIndex)?.getEntriesForXValue(highlight.x) ?: return null
        for (entry in entries) {
            if (entry.y == highlight.y || highlight.y.isNaN()) return entry
        }
        return null
    }

    /**
     * Returns the set a highlight refers to via [Highlight.dataIndex] and [Highlight.dataSetIndex].
     *
     * @return the set, or null when either index is out of range.
     */
    fun getDataSetByHighlight(highlight: Highlight): IBarLineScatterCandleBubbleDataSet<out Entry<*>>? {
        if (highlight.dataIndex !in allData.indices) return null

        val data = getDataByIndex(highlight.dataIndex)

        if (highlight.dataSetIndex !in 0 until data.dataSetCount) return null

        return data.dataSets[highlight.dataSetIndex]
    }

    /** Returns the position of [data] in [allData], or -1 if it is not one of the child data objects. */
    fun getDataIndex(data: ChartData<*>): Int = allData.indexOf(data)

    /**
     * Removes [d] from the first child data object that contains it and recomputes that child's ranges.
     * The combined ranges and [dataSets] are not rebuilt; call [notifyDataChanged] afterwards.
     *
     * @return true if a set was removed.
     */
    override fun removeDataSet(d: IBarLineScatterCandleBubbleDataSet<out Entry<*>>?): Boolean {
        for (data in allData) {
            @Suppress("UNCHECKED_CAST")
            val typed = data as ChartData<IBarLineScatterCandleBubbleDataSet<out Entry<*>>>
            if (typed.removeDataSet(d)) return true
        }
        return false
    }

    /** Not supported for combined data: logs an error and returns false. Remove sets from the child data objects. */
    override fun removeDataSet(index: Int): Boolean {
        Log.e("MPAndroidChart", "removeDataSet(int index) not supported for CombinedData")
        return false
    }

    /** Not supported for combined data: logs an error and returns false. Remove entries from the child data objects. */
    override fun removeEntry(e: Entry<*>?, dataSetIndex: Int): Boolean {
        Log.e("MPAndroidChart", "removeEntry(...) not supported for CombinedData")
        return false
    }

    /** Not supported for combined data: logs an error and returns false. Remove entries from the child data objects. */
    override fun removeEntry(xValue: Float, dataSetIndex: Int): Boolean {
        Log.e("MPAndroidChart", "removeEntry(...) not supported for CombinedData")
        return false
    }
}
