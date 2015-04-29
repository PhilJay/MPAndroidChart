package com.github.mikephil.charting.interfaces;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.utils.Transformer;

public interface BarLineScatterCandleDataProvider extends ChartInterface {

    public Transformer getTransformer(AxisDependency axis);
    public int getMaxVisibleCount();
    public boolean isInverted(AxisDependency axis);
    
    public int getLowestVisibleXIndex();
    public int getHighestVisibleXIndex();
}
