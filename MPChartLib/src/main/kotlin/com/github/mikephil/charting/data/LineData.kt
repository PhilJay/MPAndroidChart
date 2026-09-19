package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

/** Data for a line chart: one or more [ILineDataSet]s, one per line. */
open class LineData : BarLineScatterCandleBubbleData<ILineDataSet<*>> {

    /** Creates an empty data object. */
    constructor() : super()

    /** Creates a data object holding the given sets. */
    constructor(vararg dataSets: ILineDataSet<*>) : super(*dataSets)

    /** Creates a data object holding [dataSets]; an `ArrayList` is kept by reference, other lists are copied. */
    constructor(dataSets: List<ILineDataSet<*>>) : super(dataSets)
}
