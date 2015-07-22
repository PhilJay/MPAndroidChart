package com.github.mikephil.charting.interfaces;

import com.github.mikephil.charting.data.BarData;

public interface BarDataProvider extends BarLineScatterCandleDataProvider {

    public BarData getBarData();
    public boolean isDrawBarShadowEnabled();
    public boolean isDrawValueAboveBarEnabled();
    public boolean isDrawHighlightArrowEnabled();
    public boolean isDrawValuesForWholeStackEnabled();
    public boolean isDrawSuppressZeroSumsEnabled();
}
