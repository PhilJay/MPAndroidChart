package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointD;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 21/07/15.
 * Fully optimized interaction targeting engine fork.
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

    @Override
    public Highlight getHighlight(float x, float y) {

        MPPointD pos = getValsForTouch(x, y);
        float xVal = (float) pos.x;
        MPPointD.recycleInstance(pos);

        Highlight high = getHighlightForX(xVal, x, y);
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

        // take any transformer to determine the x-axis value
        MPPointD pos = mChart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(x, y);
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
    protected Highlight getHighlightForX(float xVal, float x, float y) {

        List<Highlight> closestValues = getHighlightsAtXValue(xVal, x, y);

        if(closestValues.isEmpty()) {
            return null;
        }

        float leftAxisMinDist = getMinimumDistance(closestValues, y, YAxis.AxisDependency.LEFT);
        float rightAxisMinDist = getMinimumDistance(closestValues, y, YAxis.AxisDependency.RIGHT);

        YAxis.AxisDependency axis = leftAxisMinDist < rightAxisMinDist ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT;

        float density = ((android.view.View) mChart).getContext().getResources().getDisplayMetrics().density;

        // Default safety bubble for standard line/scatter charts
        float maxSelectionDistance = 40f * density;

        Highlight detail = getClosestHighlightByPixel(closestValues, x, y, axis, maxSelectionDistance);

        return detail;
    }

    /**
     * Returns the Highlight of the DataSet that contains the closest value on the
     * y-axis.
     *
     * @param closestValues        contains two Highlight objects per DataSet closest to the selected x-position
     * @param x
     * @param y
     * @param axis                 the closest axis
     * @param minSelectionDistance
     * @return
     */
    public Highlight getClosestHighlightByPixel(List<Highlight> closestValues, float x, float y,
                                                YAxis.AxisDependency axis, float minSelectionDistance) {

        Highlight closest = null;
        float shortestDistance = Float.MAX_VALUE;

        boolean isBubble = mChart instanceof com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider;

        for (int i = 0; i < closestValues.size(); i++) {
            Highlight high = closestValues.get(i);

            if (axis == null || high.getAxis() == axis) {
                float cDistance = getDistance(x, y, high.getXPx(), high.getYPx());

                if (isBubble) {
                    com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet dataSet =
                            (com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet) mChart.getData().getDataSetByIndex(high.getDataSetIndex());

                    if (dataSet == null) continue;

                    Entry entry = dataSet.getEntryForXValue(high.getX(), Float.NaN, DataSet.Rounding.CLOSEST);

                    if (entry instanceof com.github.mikephil.charting.data.BubbleEntry) {
                        com.github.mikephil.charting.data.BubbleEntry bubbleEntry = (com.github.mikephil.charting.data.BubbleEntry) entry;

                        float density = ((android.view.View) mChart).getContext().getResources().getDisplayMetrics().density;

// 1. SAFE ZOOM SCALE TRACKING VIA BASE CHART VIEW
                        float currentZoomScale = 1.0f;
                        if (mChart instanceof com.github.mikephil.charting.charts.BarLineChartBase) {
                            currentZoomScale = ((com.github.mikephil.charting.charts.BarLineChartBase) mChart).getViewPortHandler().getScaleX();
                        }

// Calculate the base radius drawn on screen
                        float rawSize = bubbleEntry.getSize();
                        float baseRadius = rawSize / 2f;

// Multiply by currentZoomScale so the touch footprint expands dynamically as the user zooms in!
                        float physicalBubbleRadius = baseRadius * density * currentZoomScale;

                        // 2. THE TINY-BUBBLE ACCESSIBILITY BOOST
                        // Give all small/nested entries an absolute floor touch radius of 15dp
                        // This guarantees tiny hidden dots remain physically clickable over massive background shapes
                        float minimumTouchFloor = 15f * density;
                        if (physicalBubbleRadius < minimumTouchFloor) {
                            physicalBubbleRadius = minimumTouchFloor;
                        }

                        // 3. SELECTION RESOLUTION
                        if (cDistance <= physicalBubbleRadius) {
                            if (cDistance < shortestDistance) {
                                shortestDistance = cDistance;
                                closest = high;
                            }
                        }
                    }
                } else {
                    // Standard 40dp safety target mapping for Line/Scatter plots
                    if (cDistance < minSelectionDistance && cDistance < shortestDistance) {
                        shortestDistance = cDistance;
                        closest = high;
                    }
                }
            }
        }

        return closest;
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
        return h.getYPx(); // default for vertical charts
    }

    /**
     * Returns a list of Highlight objects representing the entries closest to the given xVal.
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
     */
    protected List<Highlight> buildHighlights(IDataSet set, int dataSetIndex, float xVal, DataSet.Rounding rounding) {

        ArrayList<Highlight> highlights = new ArrayList<>();

        //noinspection unchecked
        List<Entry> entries = set.getEntriesForXValue(xVal);
        if (entries.size() == 0) {
            // Try to find closest x-value and take all entries for that x-value
            final Entry closest = set.getEntryForXValue(xVal, Float.NaN, rounding);
            if (closest != null)
            {
                //noinspection unchecked
                entries = set.getEntriesForXValue(closest.getX());
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
                    dataSetIndex, set.getAxisDependency()));
        }

        return highlights;
    }

    /**
     * Calculates the distance between the two given points.
     */
    protected float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.hypot(x1 - x2, y1 - y2);
    }

    protected BarLineScatterCandleBubbleData getData() {
        return mChart.getData();
    }
}