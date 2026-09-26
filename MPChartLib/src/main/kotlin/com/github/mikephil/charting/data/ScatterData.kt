package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet

/** Data for a scatter chart: one or more [IScatterDataSet]s. */
public open class ScatterData : BarLineScatterCandleBubbleData<IScatterDataSet<*>> {

    /** Creates an empty data object. */
    public constructor() : super()

    /** Creates a data object holding the given sets. */
    public constructor(vararg dataSets: IScatterDataSet<*>) : super(*dataSets)

    /** Creates a data object holding [dataSets]; an `ArrayList` is kept by reference, other lists are copied. */
    public constructor(dataSets: List<IScatterDataSet<*>>) : super(dataSets)

    /** Largest [IScatterDataSet.scatterShapeSize] in dp over all sets, or 0 with no sets. */
    public val greatestShapeSize: Float
        get() = dataSets.maxOfOrNull { it.scatterShapeSize } ?: 0f
}
