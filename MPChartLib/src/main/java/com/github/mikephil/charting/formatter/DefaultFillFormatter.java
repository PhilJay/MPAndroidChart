package com.github.mikephil.charting.formatter;


import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

/**
 * Default formatter that calculates the position of the filled line.
 *
 * @author Philipp Jahoda
 */
public class DefaultFillFormatter implements IFillFormatter
{

    @Override
    public double getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {

        double fillMin = 0d;
        double chartMaxY = dataProvider.getYChartMax();
        double chartMinY = dataProvider.getYChartMin();

        LineData data = dataProvider.getLineData();

        if (dataSet.getYMax() > 0 && dataSet.getYMin() < 0) {
            fillMin = 0d;
        } else {

            double max, min;

            if (data.getYMax() > 0)
                max = 0d;
            else
                max = chartMaxY;
            if (data.getYMin() < 0)
                min = 0d;
            else
                min = chartMinY;

            fillMin = dataSet.getYMin() >= 0 ? min : max;
        }

        return fillMin;
    }
}