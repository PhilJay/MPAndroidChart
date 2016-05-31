package com.github.mikephil.charting.interfaces.dataprovider;

import com.github.mikephil.charting.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
