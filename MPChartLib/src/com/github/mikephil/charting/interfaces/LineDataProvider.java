
package com.github.mikephil.charting.interfaces;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.FillFormatter;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    public LineData getLineData();

    public YAxis getAxis(YAxis.AxisDependency dependency);
}
