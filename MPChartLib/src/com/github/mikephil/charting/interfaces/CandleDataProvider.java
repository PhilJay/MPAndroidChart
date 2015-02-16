package com.github.mikephil.charting.interfaces;

import com.github.mikephil.charting.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleDataProvider {

    public CandleData getCandleData();
}
