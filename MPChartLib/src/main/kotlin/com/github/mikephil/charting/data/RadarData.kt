package com.github.mikephil.charting.data

import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet

/** Data for a radar chart: one or more [IRadarDataSet]s, one polygon each. */
public open class RadarData : ChartData<IRadarDataSet<*>> {

    /** Creates an empty data object. */
    public constructor() : super()

    /** Creates a data object holding the given sets. */
    public constructor(vararg dataSets: IRadarDataSet<*>) : super(*dataSets)

    /** Creates a data object holding [dataSets]; an `ArrayList` is kept by reference, other lists are copied. */
    public constructor(dataSets: List<IRadarDataSet<*>>) : super(dataSets)


    /**
     * Returns the entry a highlight refers to. For radar charts [Highlight.x] holds the entry index, not an x value.
     *
     * @return the entry, or null when [Highlight.dataSetIndex] or the entry index is out of range.
     */
    override fun getEntryForHighlight(highlight: Highlight): Entry<*>? {
        val set = getDataSetByIndex(highlight.dataSetIndex) ?: return null
        val index = highlight.x.toInt()
        return if (index in 0 until set.entryCount) set.getEntryForIndex(index) else null
    }
}
