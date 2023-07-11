
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.renderer.LineChartRenderer;

import java.util.Locale;

/**
 * Chart that draws lines, surfaces, circles, ...
 *
 * @author Philipp Jahoda
 */
public class LineChart extends BarLineChartBase<LineData> implements LineDataProvider {

    public LineChart(Context context) {
        super(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new LineChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    public LineData getLineData() {
        return mData;
    }

    @Override
    protected void onDetachedFromWindow() {
        // releases the bitmap in the renderer to avoid oom error
        if (mRenderer != null && mRenderer instanceof LineChartRenderer) {
            ((LineChartRenderer) mRenderer).releaseBitmap();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public String getAccessibilityDescription() {
        LineData lineData = getLineData();

        int numberOfPoints = lineData.getEntryCount();

        // Min and max values...
        IAxisValueFormatter yAxisValueFormmater = getAxisLeft().getValueFormatter();
        String minVal = yAxisValueFormmater.getFormattedValue(lineData.getYMin(), null);
        String maxVal = yAxisValueFormmater.getFormattedValue(lineData.getYMax(), null);

        // Data range...
        IAxisValueFormatter xAxisValueFormatter = getXAxis().getValueFormatter();
        String minRange = xAxisValueFormatter.getFormattedValue(lineData.getXMin(), null);
        String maxRange = xAxisValueFormatter.getFormattedValue(lineData.getXMax(), null);

        String entries = numberOfPoints == 1 ? "entry" : "entries";

        return String.format(Locale.getDefault(), "The line chart has %d %s. " +
                        "The minimum value is %s and maximum value is %s." +
                        "Data ranges from %s to %s.",
                numberOfPoints, entries, minVal, maxVal, minRange, maxRange);
    }
}
