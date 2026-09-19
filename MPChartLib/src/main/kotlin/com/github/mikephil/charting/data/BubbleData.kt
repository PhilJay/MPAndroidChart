package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet

/** Data for a bubble chart: one or more [IBubbleDataSet]s. */
open class BubbleData : BarLineScatterCandleBubbleData<IBubbleDataSet<*>> {

    /** Creates an empty data object. */
    constructor() : super()

    /** Creates a data object holding the given sets. */
    constructor(vararg dataSets: IBubbleDataSet<*>) : super(*dataSets)

    /** Creates a data object holding [dataSets]; an `ArrayList` is kept by reference, other lists are copied. */
    constructor(dataSets: List<IBubbleDataSet<*>>) : super(dataSets)

    /** Sets the stroke width in dp of the highlight circle for every set. */
    fun setHighlightCircleWidth(width: Float) {
        for (set in dataSets) set.highlightCircleWidth = width
    }
}
