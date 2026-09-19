package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet

/** Data for a scatter chart: one or more [IScatterDataSet]s. */
open class ScatterData : BarLineScatterCandleBubbleData<IScatterDataSet<*>> {

    /** Creates an empty data object. */
    constructor() : super()

    /** Creates a data object holding the given sets. */
    constructor(vararg dataSets: IScatterDataSet<*>) : super(*dataSets)

    /** Creates a data object holding [dataSets]; an `ArrayList` is kept by reference, other lists are copied. */
    constructor(dataSets: List<IScatterDataSet<*>>) : super(dataSets)

    /** Largest [IScatterDataSet.scatterShapeSize] in pixels over all sets, or 0 with no sets. */
    val greatestShapeSize: Float
        get() = dataSets.maxOfOrNull { it.scatterShapeSize } ?: 0f
}
