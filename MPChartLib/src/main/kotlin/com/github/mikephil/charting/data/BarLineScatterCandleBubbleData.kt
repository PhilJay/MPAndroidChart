package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet

/**
 * Base for the data objects of the charts with an x and a y axis (bar, line, scatter, candle, bubble and
 * combined). Adds nothing to [ChartData] beyond the set type; it lets a combined chart treat them alike.
 *
 * @param T the data set type this object holds.
 */
public abstract class BarLineScatterCandleBubbleData<T : IBarLineScatterCandleBubbleDataSet<out Entry<*>>> : ChartData<T> {

    /** Creates an empty data object. */
    public constructor() : super()

    /** Creates a data object holding the given sets. */
    public constructor(vararg sets: T) : super(*sets)

    /** Creates a data object holding [sets]; an `ArrayList` is kept by reference, other lists are copied. */
    public constructor(sets: List<T>) : super(sets)
}
