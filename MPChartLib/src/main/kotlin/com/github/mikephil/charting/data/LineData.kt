package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

/** Data for a line chart: one or more [ILineDataSet]s, one per line. */
public open class LineData : BarLineScatterCandleBubbleData<ILineDataSet<*>> {

    /** Creates an empty data object. */
    public constructor() : super()

    /** Creates a data object holding the given sets. */
    public constructor(vararg dataSets: ILineDataSet<*>) : super(*dataSets)

    /** Creates a data object holding [dataSets]; an `ArrayList` is kept by reference, other lists are copied. */
    public constructor(dataSets: List<ILineDataSet<*>>) : super(dataSets)
}
