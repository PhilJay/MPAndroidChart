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

/**
 * Created by Philipp Jahoda on 21/07/15.
 */
public class ChartHighlighter<T extends BarLineScatterCandleBubbleDataProvider> implements Highlighter {

    /**
     * instance of the data-provider
     */
    protected T mChart;

    public ChartHighlighter(T chart) {
        this.mChart = chart;
    }

    @Override
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

        List<SelectionDetail> closestValues = getSelectionDetailsAtXPos(xVal);

        float leftAxisMinDist = getMinimumDistance(closestValues, y, YAxis.AxisDependency.LEFT);
        float rightAxisMinDist = getMinimumDistance(closestValues, y, YAxis.AxisDependency.RIGHT);

        YAxis.AxisDependency axis = leftAxisMinDist < rightAxisMinDist ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT;

        SelectionDetail detail = getClosestSelectionDetailByPixel(closestValues, x, y, axis, mChart
                .getMaxHighlightDistance());

        return detail;
    }

    /**
     * Returns the minimum distance from a touch value (in pixels) to the
     * closest value (in pixels) that is displayed in the chart.
     *
     * @param closestValues
     * @param pos
     * @param axis
     * @return
     */
    protected float getMinimumDistance(List<SelectionDetail> closestValues, float pos, YAxis.AxisDependency axis) {

        float distance = Float.MAX_VALUE;

        for (int i = 0; i < closestValues.size(); i++) {

            SelectionDetail sel = closestValues.get(i);

            if (sel.dataSet.getAxisDependency() == axis) {

                float tempDistance = Math.abs(getSelectionPos(sel) - pos);
                if (tempDistance < distance) {
                    distance = tempDistance;
                }
            }
        }

        return distance;
    }

    protected float getSelectionPos(SelectionDetail sel) {
        return sel.yPx;
    }

    /**
     * Returns a list of SelectionDetail objects representing the entries closest to the given xVal.
     * The returned list contains two objects per DataSet (closest rounding up, closest rounding down).
     *
     * @param xVal
     * @return
     */
    protected List<SelectionDetail> getSelectionDetailsAtXPos(float xVal) {

        List<SelectionDetail> vals = new ArrayList<SelectionDetail>();

        if (mChart.getData() == null) return vals;

        for (int i = 0, dataSetCount = mChart.getData().getDataSetCount(); i < dataSetCount; i++) {

            IDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            // dont include datasets that cannot be highlighted
            if (!dataSet.isHighlightEnabled())
                continue;

            vals.add(getDetail(dataSet, i, xVal, DataSet.Rounding.UP));
            vals.add(getDetail(dataSet, i, xVal, DataSet.Rounding.DOWN));
        }

        return vals;
    }

    /**
     * Returns the SelectionDetail object corresponding to the selected xValue and dataSetIndex.
     *
     * @param set
     * @param dataSetIndex
     * @param xVal
     * @param rounding
     * @return
     */
    protected SelectionDetail getDetail(IDataSet set, int dataSetIndex, float xVal, DataSet.Rounding rounding) {

        final Entry e = set.getEntryForXPos(xVal, rounding);

        if (e == null)
            return null;

        PointD pixels = mChart.getTransformer(set.getAxisDependency()).getPixelsForValues(e.getX(), e.getY());

        return new SelectionDetail((float) pixels.x, (float) pixels.y, e.getX(), e.getY(), dataSetIndex, set);
    }

    /**
     * Returns the SelectionDetail of the DataSet that contains the closest value on the
     * y-axis.
     *
     * @param closestValues contains two values per DataSet closest to the selected x-position (determined by rounding up and
     *                      down)
     * @param x
     * @param y
     * @param axis          the closest axis
     * @return
     */
    public SelectionDetail getClosestSelectionDetailByPixel(List<SelectionDetail> closestValues, float x, float y,
                                                            YAxis.AxisDependency axis, float minSelectionDistance) {

        SelectionDetail closest = null;
        float distance = minSelectionDistance;

        for (int i = 0; i < closestValues.size(); i++) {

            SelectionDetail sel = closestValues.get(i);

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

    /**
     * Calculates the distance between the two given points.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    protected float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.hypot(x1 - x2, y1 - y2);
    }
}
