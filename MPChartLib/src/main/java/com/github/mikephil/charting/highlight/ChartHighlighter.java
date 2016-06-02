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
     * Returns a Highlight object corresponding to the given xPx- and yPx- touch positions in pixels.
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

        // take any transformer to determine the xPx-axis yValue
        PointD pos = mChart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(x, y);
        return pos;
    }

    /**
     * Returns the corresponding SelectionDetail for a given xVal and yPx-touch position in pixels.
     *
     * @param xVal
     * @param y
     * @return
     */
    protected SelectionDetail getSelectionDetail(float xVal, float x, float y) {

        List<SelectionDetail> valsAtIndex = getSelectionDetailsAtIndex(xVal);

        float leftdist = Utils.getMinimumDistance(valsAtIndex, y, YAxis.AxisDependency.LEFT);
        float rightdist = Utils.getMinimumDistance(valsAtIndex, y, YAxis.AxisDependency.RIGHT);

        YAxis.AxisDependency axis = leftdist < rightdist ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT;

        SelectionDetail detail = Utils.getClosestSelectionDetailByPixel(valsAtIndex, x, y, axis);

        return detail;
    }

    /**
     * Returns a list of SelectionDetail object corresponding to the given xIndex.
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
}
