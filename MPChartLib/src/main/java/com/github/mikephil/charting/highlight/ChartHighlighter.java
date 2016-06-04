package com.github.mikephil.charting.highlight;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.SelectionDetail;
import com.github.mikephil.charting.utils.Utils;

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

        float xVal = (float) getValsForTouch(x, y).x;

        SelectionDetail selectionDetail = getSelectionDetail(xVal, x, y);
        if (selectionDetail == null)
            return null;

        return new Highlight(selectionDetail.xValue,
                selectionDetail.yValue,
                selectionDetail.dataIndex,
                selectionDetail.dataSetIndex);
    }

    /**
     * Returns the corresponding xPos for a given touch-position in pixels.
     *
     * @param x
     * @return
     */
    protected PointD getValsForTouch(float x, float y) {

        // take any transformer to determine the x-axis value
        PointD pos = mChart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(x, y);
        return pos;
    }

    /**
     * Returns the corresponding SelectionDetail for a given xVal and y-touch position in pixels.
     *
     * @param xVal
     * @param y
     * @return
     */
    protected SelectionDetail getSelectionDetail(float xVal, float x, float y) {

        List<SelectionDetail> valsAtIndex = getSelectionDetailsAtIndex(xVal);

        float leftdist = getMinimumDistance(valsAtIndex, y, YAxis.AxisDependency.LEFT);
        float rightdist = getMinimumDistance(valsAtIndex, y, YAxis.AxisDependency.RIGHT);

        YAxis.AxisDependency axis = leftdist < rightdist ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT;

        SelectionDetail detail = getClosestSelectionDetailByPixel(valsAtIndex, x, y, axis, mChart
                .getMaxHighlightDistance());

        return detail;
    }

    /**
     * Returns the minimum distance from a touch value (in pixels) to the
     * closest value (in pixels) that is displayed in the chart.
     *
     * @param valsAtIndex
     * @param pos
     * @param axis
     * @return
     */
    protected float getMinimumDistance(List<SelectionDetail> valsAtIndex,
                                            float pos,
                                            YAxis.AxisDependency axis) {

        float distance = Float.MAX_VALUE;

        for (int i = 0; i < valsAtIndex.size(); i++) {

            SelectionDetail sel = valsAtIndex.get(i);

            if (sel.dataSet.getAxisDependency() == axis) {

                float cdistance = Math.abs(getSelectionPos(sel) - pos);
                if (cdistance < distance) {
                    distance = cdistance;
                }
            }
        }

        return distance;
    }

    protected float getSelectionPos(SelectionDetail sel) {
        return sel.yPx;
    }

    /**
     * Returns a list of SelectionDetail object corresponding to the given xVal.
     *
     * @param xVal
     * @return
     */
    protected List<SelectionDetail> getSelectionDetailsAtIndex(float xVal) {

        List<SelectionDetail> vals = new ArrayList<SelectionDetail>();

        if (mChart.getData() == null) return vals;

        for (int i = 0, dataSetCount = mChart.getData().getDataSetCount(); i < dataSetCount; i++) {

            IDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            // dont include datasets that cannot be highlighted
            if (!dataSet.isHighlightEnabled())
                continue;

            vals.add(getDetails(dataSet, i, xVal, DataSet.Rounding.UP));
            vals.add(getDetails(dataSet, i, xVal, DataSet.Rounding.DOWN));
        }

        return vals;
    }

    protected SelectionDetail getDetails(IDataSet set, int dataSetIndex, float xVal, DataSet.Rounding rounding) {

        final Entry e = set.getEntryForXPos(xVal, rounding);

        PointD pixels = mChart.getTransformer(set.getAxisDependency()).getPixelsForValues(e.getX(), e.getY());

        return new SelectionDetail((float) pixels.x, (float) pixels.y, e.getX(), e.getY(), dataSetIndex, set);
    }

    /**
     * Returns the SelectionDetail of the DataSet that contains the closest value on the
     * y-axis.
     *
     * @param valsAtIndex all the values at a specific index
     * @return
     */
    public SelectionDetail getClosestSelectionDetailByPixel(
            List<SelectionDetail> valsAtIndex,
            float x, float y,
            YAxis.AxisDependency axis, float minSelectionDistance) {

        SelectionDetail closest = null;
        float distance = minSelectionDistance;

        for (int i = 0; i < valsAtIndex.size(); i++) {

            SelectionDetail sel = valsAtIndex.get(i);

            if (axis == null || sel.dataSet.getAxisDependency() == axis) {

                float cDistance = getDistance(x, y, sel.xPx, sel.yPx);

                if (cDistance < distance) {
                    closest = sel;
                    distance = cDistance;
                }
            }
        }

        return closest;
    }

    protected float getDistance(float x, float y, float selX, float selY) {
        return (float) Math.hypot(x - selX, y - selY);
    }
}
