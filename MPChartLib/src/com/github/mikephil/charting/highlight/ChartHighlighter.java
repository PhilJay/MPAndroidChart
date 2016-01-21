package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.SelectionDetail;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 21/07/15.
 */
public class ChartHighlighter<T extends BarLineScatterCandleBubbleDataProvider> {

    /**
     * instance of the data-provider
     */
    protected T mChart;

    public ChartHighlighter(T chart) {
        this.mChart = chart;
    }

    /**
     * Returns a Highlight object corresponding to the given x- and y- touch positions in pixels.
     *
     * @param x
     * @param y
     * @return
     */
    public Highlight getHighlight(float x, float y) {

        int xIndex = getXIndex(x);
        if (xIndex == -Integer.MAX_VALUE)
            return null;

        int dataSetIndex = getDataSetIndex(xIndex, x, y);

        if (dataSetIndex == -Integer.MAX_VALUE)
            return null;

        IDataSet dataSet = mChart.getData().getDataSetByIndex(dataSetIndex);
        //Check for Scatter/Bubble special case
        if (isSpecialBubbleSelectionCase(xIndex, (DataSet) dataSet)) {
            //Get the Y value
            int yIndex = getYIndex(x, y);
            Highlight highlight = new Highlight(xIndex, dataSetIndex);
            highlight.setYIndex(yIndex);
            return highlight;
        }


        return new Highlight(xIndex, dataSetIndex);
    }

    private boolean isSpecialBubbleSelectionCase(int xIndex, DataSet dataSet) {

        int low = 0;
        int high = dataSet.getYVals().size() - 1;

        while (low <= high) {
            int m = (high + low) / 2;

            if (xIndex == ((Entry) dataSet.getYVals().get(m)).getXIndex()) {
                if (m < dataSet.getYVals().size() - 1) {
                    return ((Entry) dataSet.getYVals().get(m - 1)).getXIndex() == xIndex ||
                            ((Entry) dataSet.getYVals().get(m + 1)).getXIndex() == xIndex;
                } else {
                    return ((Entry) dataSet.getYVals().get(m - 1)).getXIndex() == xIndex;
                }
            }

            if (xIndex > ((Entry) dataSet.getYVals().get(m)).getXIndex())
                low = m + 1;
            else
                high = m - 1;
        }

        return false;
    }

    /**
     * Returns the corresponding x-index for a given touch-position in pixels.
     *
     * @param x
     * @return
     */
    protected int getXIndex(float x) {

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;

        // take any transformer to determine the x-axis value
        mChart.getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);

        return (int) Math.round(pts[0]);
    }

    protected int getYIndex(float x, float y) {

        // create an array of the touch-point
        float[] pts = {x, y};

        // take any transformer to determine the y-axis value
        mChart.getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);

        return Math.round(pts[1]);
    }

    /**
     * Returns the corresponding dataset-index for a given xIndex and xy-touch position in pixels.
     *
     * @param xIndex
     * @param x
     * @param y
     * @return
     */
    protected int getDataSetIndex(int xIndex, float x, float y) {

        List<SelectionDetail> valsAtIndex = getSelectionDetailsAtIndex(xIndex, x, y);

        float leftdist = Utils.getMinimumDistance(valsAtIndex, y, YAxis.AxisDependency.LEFT);
        float rightdist = Utils.getMinimumDistance(valsAtIndex, y, YAxis.AxisDependency.RIGHT);

        YAxis.AxisDependency axis = leftdist < rightdist ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT;

        int dataSetIndex = Utils.getClosestDataSetIndex(valsAtIndex, y, axis);

        return dataSetIndex;
    }

    /**
     * Returns a list of SelectionDetail object corresponding to the given xIndex.
     *
     * @param xIndex
     * @return
     */
    protected List<SelectionDetail> getSelectionDetailsAtIndex(int xIndex, float x, float y) {

        List<SelectionDetail> vals = new ArrayList<>();

        float[] pts = new float[2];

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            IDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            // dont include datasets that cannot be highlighted
            if (!dataSet.isHighlightEnabled())
                continue;

            // extract all y-values from all DataSets at the given x-index

            //Get the Y value
            int yIndex = getYIndex(x, y);

            final float yVal = dataSet.getYValForXIndex(xIndex, yIndex);

            if (yVal == Float.NaN)
                continue;

            pts[1] = yVal;

            mChart.getTransformer(dataSet.getAxisDependency()).pointValuesToPixel(pts);


            if (!Float.isNaN(pts[1])) {
                vals.add(new SelectionDetail(pts[1], i, dataSet));
            }

        }

        return vals;
    }
}
