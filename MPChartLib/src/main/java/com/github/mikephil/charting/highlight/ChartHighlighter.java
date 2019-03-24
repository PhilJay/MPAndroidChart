package com.github.mikephil.charting.highlight;

import android.util.Log;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointD;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.github.mikephil.charting.highlight.Highlight.Type.LEFT_AXIS;
import static com.github.mikephil.charting.highlight.Highlight.Type.NULL;
import static com.github.mikephil.charting.highlight.Highlight.Type.RIGHT_AXIS;
import static com.github.mikephil.charting.highlight.Highlight.Type.X_AXIS;

/**
 * Created by Philipp Jahoda on 21/07/15.
 */
public class ChartHighlighter<T extends BarLineScatterCandleBubbleDataProvider> implements IHighlighter
{

    /**
     * instance of the data-provider
     */
    protected T mChart;

    /**
     * buffer for storing previously highlighted values
     */
    protected List<Highlight> mHighlightBuffer = new ArrayList<Highlight>();

    public ChartHighlighter(T chart) {
        this.mChart = chart;
    }

    /**
     * Creates the appropriate Highlight, and add to the appropriate chart item.
     *
     * @param x touch x
     * @param y touch y
     * @return highlight
     */
    @SuppressWarnings("ConstantConditions")  // suppress ugly NPE warnings on getAxis calls
    @NonNull
    @Override
    public Highlight getHighlight(float x, float y) {

        MPPointD touch = getValsForTouch(x, y);
        float xVal = (float) touch.x;
        float yVal = (float) touch.y;

        Highlight high = new Highlight(xVal, yVal, NULL, x, y);
        MPPointD.recycleInstance(touch);

        // look for touch point outside the data area
        if (y > mChart.getContentRect().bottom && mChart.hasXAxis()) {
            high = new Highlight(xVal, yVal, X_AXIS, x, y);
        } else if (x < mChart.getContentRect().left && mChart.hasLeftAxis()) {
            high = new Highlight(xVal, yVal, LEFT_AXIS, x, y);
        } else if (x > mChart.getContentRect().right && mChart.hasRightAxis()) {
            // we assumed LEFT axis above; recompute touch for RIGHT axis
            touch = getValsForTouch(x, y, YAxis.AxisDependency.RIGHT);
            high = new Highlight(xVal, yVal, RIGHT_AXIS, x, y);
        }

        // if not outside, look inside for value highlight
        if (high.isNull()) {
            high = getHighlightForX(xVal, x, y);
        }
        if (((Chart) mChart).isLogEnabled())
            Log.i("ChartHighlighter" , high.toString());
        return high;
    }

    /**
     * Returns a recyclable MPPointD instance.
     * Returns the corresponding xPos for a given touch-position in pixels.
     *
     * @param x
     * @param y
     * @return
     */
    protected MPPointD getValsForTouch(float x, float y) {
        return getValsForTouch(x, y, YAxis.AxisDependency.LEFT);
    }

    /**
     * Returns a recyclable MPPointD instance.
     * Returns the corresponding xPos for a given touch-position in pixels.
     *
     * @param x x in pixels
     * @param y y in pixels
     * @param dependency axis dependency
     * @return values using dependency axis
     */
    protected MPPointD getValsForTouch(float x, float y, YAxis.AxisDependency dependency) {

        // take any transformer to determine the x-axis value
        MPPointD pos = mChart.getTransformer(dependency).getValuesByTouchPoint(x, y);
        return pos;
    }

    /**
     * Returns the corresponding Highlight for a given xVal and x- and y-touch position in pixels.
     *
     * @param xVal
     * @param x
     * @param y
     * @return
     */
    @Nullable
    protected Highlight getHighlightForX(float xVal, float x, float y) {

        List<Highlight> closestValues = getHighlightsAtXValue(xVal, x, y);

        if(closestValues.isEmpty()) {
            return new Highlight(x, y, NULL, Float.NaN, Float.NaN);
        }

        float leftAxisMinDist = getMinimumDistance(closestValues, y, YAxis.AxisDependency.LEFT);
        float rightAxisMinDist = getMinimumDistance(closestValues, y, YAxis.AxisDependency.RIGHT);

        YAxis.AxisDependency axis = leftAxisMinDist < rightAxisMinDist ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT;

        Highlight detail = getClosestHighlightByPixel(closestValues, x, y, axis, mChart.getMaxHighlightDistance());

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
    protected float getMinimumDistance(List<Highlight> closestValues, float pos, YAxis.AxisDependency axis) {

        float distance = Float.MAX_VALUE;

        for (int i = 0; i < closestValues.size(); i++) {

            Highlight high = closestValues.get(i);

            if (high.getAxis() == axis) {

                float tempDistance = Math.abs(getHighlightPos(high) - pos);
                if (tempDistance < distance) {
                    distance = tempDistance;
                }
            }
        }

        return distance;
    }

    protected float getHighlightPos(Highlight h) {
        return h.getYPx();
    }

    /**
     * Returns a list of Highlight objects representing the entries closest to the given xVal.
     * The returned list contains two objects per DataSet (closest rounding up, closest rounding down).
     *
     * @param xVal the transformed x-value of the x-touch position
     * @param x    touch position
     * @param y    touch position
     * @return
     */
    protected List<Highlight> getHighlightsAtXValue(float xVal, float x, float y) {

        mHighlightBuffer.clear();

        BarLineScatterCandleBubbleData data = getData();

        if (data == null)
            return mHighlightBuffer;

        for (int i = 0, dataSetCount = data.getDataSetCount(); i < dataSetCount; i++) {

            IDataSet dataSet = data.getDataSetByIndex(i);

            // don't include DataSets that cannot be highlighted
            if (!dataSet.isHighlightEnabled())
                continue;

            mHighlightBuffer.addAll(buildHighlights(dataSet, i, xVal, DataSet.Rounding.CLOSEST));
        }

        return mHighlightBuffer;
    }

    /**
     * An array of `Highlight` objects corresponding to the selected xValue and dataSetIndex.
     *
     * @param set
     * @param dataSetIndex
     * @param xVal
     * @param rounding
     * @return
     */
    protected List<Highlight> buildHighlights(IDataSet set, int dataSetIndex, float xVal, DataSet.Rounding rounding) {
        Log.i("___ChartHighlighter", String.format("buildHighlights index= %d xVal= %.1f, %s", dataSetIndex, xVal, rounding.name()));
        ArrayList<Highlight> highlights = new ArrayList<>();

        //noinspection unchecked
        List<Entry> entries = set.getEntriesForXValue(xVal);
        Log.i("___ChartHighlighter", String.format("  buildHighlights found %d entries at %.2f", entries.size(), xVal));
        if (entries.size() == 0) {
            // Try to find closest x-value and take all entries for that x-value
            final Entry closest = set.getEntryForXValue(xVal, Float.NaN, rounding);
            Log.i("___ChartHighlighter", String.format("    buildHighlights closest entry is %s", closest));
            if (closest != null)
            {
                //noinspection unchecked
                entries = set.getEntriesForXValue(closest.getX());
                Log.i("___ChartHighlighter", String.format("      buildHighlights found %d entries at %.2f", entries.size(), closest.getX()));
            }
        }

        if (entries.size() == 0)
            return highlights;

        for (Entry e : entries) {
            MPPointD pixels = mChart.getTransformer(
                    set.getAxisDependency()).getPixelForValues(e.getX(), e.getY());

            highlights.add(new Highlight(
                    e.getX(), e.getY(),
                    (float) pixels.x, (float) pixels.y,
                    dataSetIndex, set.getEntryIndex(e), set.getAxisDependency()));
        }

        return highlights;
    }

    /**
     * Returns the Highlight of the DataSet that contains the closest value on the
     * y-axis.
     *
     * @param closestValues        contains two Highlight objects per DataSet closest to the selected x-position (determined by
     *                             rounding up an down)
     * @param x
     * @param y
     * @param axis                 the closest axis
     * @param minSelectionDistance
     * @return
     */
    public Highlight getClosestHighlightByPixel(List<Highlight> closestValues, float x, float y,
                                                YAxis.AxisDependency axis, float minSelectionDistance) {

        Highlight closest = null;
        float distance = minSelectionDistance;

        for (int i = 0; i < closestValues.size(); i++) {

            Highlight high = closestValues.get(i);

            if (axis == null || high.getAxis() == axis) {

                float cDistance = getDistance(x, y, high.getXPx(), high.getYPx());

                if (cDistance < distance) {
                    closest = high;
                    distance = cDistance;
                }
            }
        }
        Log.i("___ChartHighlighter", String.format("getClosestHighlightByPixel searching %d highlights, return %s", closestValues.size(), closest));
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
        //return Math.abs(y1 - y2);
        //return Math.abs(x1 - x2);
        return (float) Math.hypot(x1 - x2, y1 - y2);
    }

    protected BarLineScatterCandleBubbleData getData() {
        return mChart.getData();
    }
}
