package com.github.mikephil.charting.data

import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet

/** Data for a radar chart: one or more [IRadarDataSet]s, one polygon each. */
open class RadarData : ChartData<IRadarDataSet<*>> {

    /** Creates an empty data object. */
    constructor() : super()

    /** Creates a data object holding the given sets. */
    constructor(vararg dataSets: IRadarDataSet<*>) : super(*dataSets)

    /** Creates a data object holding [dataSets]; an `ArrayList` is kept by reference, other lists are copied. */
    constructor(dataSets: List<IRadarDataSet<*>>) : super(dataSets)


    /**
     * Returns the entry a highlight refers to. For radar charts [Highlight.x] holds the entry index, not an x value.
     *
     * @return the entry, or null when [Highlight.dataSetIndex] is out of range.
     * @throws IndexOutOfBoundsException when the entry index is out of range for that set.
     */
    override fun getEntryForHighlight(highlight: Highlight): Entry<*>? {
        return getDataSetByIndex(highlight.dataSetIndex)?.getEntryForIndex(highlight.x.toInt())
    }
}
