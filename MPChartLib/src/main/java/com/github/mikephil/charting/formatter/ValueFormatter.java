package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Class to format all values before they are drawn as labels.
 */
public abstract class ValueFormatter implements IAxisValueFormatter, IValueFormatter{

    /**
     * <b>DO NOT USE</b>, only for backwards compatibility and will be removed in future versions.
     *
     * @param value the value to be formatted
     * @param axis  the axis the value belongs to
     * @return formatted string label
     */
    @Override
    @Deprecated
    public String getFormattedValue(float value, AxisBase axis) {
        return getFormattedValue(value);
    }

    /**
     * <b>DO NOT USE</b>, only for backwards compatibility and will be removed in future versions.
     * @param value           the value to be formatted
     * @param entry           the entry the value belongs to - in e.g. BarChart, this is of class BarEntry
     * @param dataSetIndex    the index of the DataSet the entry in focus belongs to
     * @param viewPortHandler provides information about the current chart state (scale, translation, ...)
     * @return formatted string label
     */
    @Override
    @Deprecated
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return getFormattedValue(value);
    }

    /**
     * Called when drawing any label, used to change numbers into formatted strings.
     *
     * @param value float to be formatted
     * @return formatted string label
     */
    public String getFormattedValue(float value) {
        return String.valueOf(value);
    }

    /**
     * Used to draw axis labels, calls {@link #getFormattedValue(float)} by default.
     *
     * @param value float to be formatted
     * @param axis  axis being labeled
     * @return formatted string label
     */
    public String getAxisLabel(float value, AxisBase axis) {
        return getFormattedValue(value);
    }

    /**
     * Used to draw bar labels, calls {@link #getFormattedValue(float)} by default.
     *
     * @param barEntry bar being labeled
     * @return formatted string label
     */
    public String getBarLabel(BarEntry barEntry) {
        return getFormattedValue(barEntry.getY());
    }

    /**
     * Used to draw stacked bar labels, calls {@link #getFormattedValue(float)} by default.
     *
     * @param value        current value to be formatted
     * @param stackedEntry stacked entry being labeled, contains all Y values
     * @return formatted string label
     */
    public String getBarStackedLabel(float value, BarEntry stackedEntry) {
        return getFormattedValue(value);
    }

    /**
     * Used to draw line and scatter labels, calls {@link #getFormattedValue(float)} by default.
     *
     * @param entry point being labeled, contains X value
     * @return formatted string label
     */
    public String getPointLabel(Entry entry) {
        return getFormattedValue(entry.getY());
    }

    /**
     * Used to draw pie value labels, calls {@link #getFormattedValue(float)} by default.
     *
     * @param value    float to be formatted, may have been converted to percentage
     * @param pieEntry slice being labeled, contains original, non-percentage Y value
     * @return formatted string label
     */
    public String getPieLabel(float value, PieEntry pieEntry) {
        return getFormattedValue(value);
    }

    /**
     * Used to draw radar value labels, calls {@link #getFormattedValue(float)} by default.
     *
     * @param radarEntry entry being labeled
     * @return formatted string label
     */
    public String getRadarLabel(RadarEntry radarEntry) {
        return getFormattedValue(radarEntry.getY());
    }

    /**
     * Used to draw bubble size labels, calls {@link #getFormattedValue(float)} by default.
     *
     * @param bubbleEntry bubble being labeled, also contains X and Y values
     * @return formatted string label
     */
    public String getBubbleLabel(BubbleEntry bubbleEntry) {
        return getFormattedValue(bubbleEntry.getSize());
    }

    /**
     * Used to draw high labels, calls {@link #getFormattedValue(float)} by default.
     *
     * @param candleEntry candlestick being labeled
     * @return formatted string label
     */
    public String getCandleLabel(CandleEntry candleEntry) {
        return getFormattedValue(candleEntry.getHigh());
    }

}
