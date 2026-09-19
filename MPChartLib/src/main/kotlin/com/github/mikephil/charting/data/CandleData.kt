package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet

/** Data for a candle stick chart: one or more [ICandleDataSet]s. */
open class CandleData : BarLineScatterCandleBubbleData<ICandleDataSet<*>> {

    /** Creates an empty data object. */
    constructor() : super()

    /** Creates a data object holding the given sets. */
    constructor(vararg dataSets: ICandleDataSet<*>) : super(*dataSets)

    /** Creates a data object holding [dataSets]; an `ArrayList` is kept by reference, other lists are copied. */
    constructor(dataSets: List<ICandleDataSet<*>>) : super(dataSets)
}
