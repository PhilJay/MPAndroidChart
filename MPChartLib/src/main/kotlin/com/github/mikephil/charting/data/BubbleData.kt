package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet

/** Data for a bubble chart: one or more [IBubbleDataSet]s. */
public open class BubbleData : BarLineScatterCandleBubbleData<IBubbleDataSet<*>> {

    /** Creates an empty data object. */
    public constructor() : super()

    /** Creates a data object holding the given sets. */
    public constructor(vararg dataSets: IBubbleDataSet<*>) : super(*dataSets)

    /** Creates a data object holding [dataSets]; an `ArrayList` is kept by reference, other lists are copied. */
    public constructor(dataSets: List<IBubbleDataSet<*>>) : super(dataSets)

    /** Sets the stroke width in dp of the highlight circle for every set. */
    public fun setHighlightCircleWidth(width: Float) {
        for (set in dataSets) set.highlightCircleWidth = width
    }
}
