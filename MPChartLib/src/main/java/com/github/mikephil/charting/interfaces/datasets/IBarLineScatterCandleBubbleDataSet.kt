package com.github.mikephil.charting.interfaces.datasets

import com.github.mikephil.charting.data.Entry

interface IBarLineScatterCandleBubbleDataSet<T : Entry> : IDataSet<T> {

    val highLightColor: Int
}
