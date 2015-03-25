package com.github.mikephil.charting.interfaces;

import com.github.mikephil.charting.data.OHLCData;

public interface OHLCDataProvider extends BarLineScatterCandleDataProvider {

    public OHLCData getOHLCData();
}
