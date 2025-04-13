package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Path;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class YAxisRendererRadarChart extends YAxisRenderer {

    private RadarChart mChart;

    public YAxisRendererRadarChart(ViewPortHandler viewPortHandler, YAxis yAxis, RadarChart chart) {
        super(viewPortHandler, yAxis, null);

        this.mChart = chart;
    }

    @Override
    protected void computeAxisValues(float min, float max) {

        float yMin = min;
        float yMax = max;

        int labelCount = axis.getLabelCount();
        double range = Math.abs(yMax - yMin);

        if (labelCount == 0 || range <= 0 || Double.isInfinite(range)) {
            axis.mEntries = new float[]{};
            axis.mCenteredEntries = new float[]{};
            axis.mEntryCount = 0;
            return;
        }

        // Find out how much spacing (in y value space) between axis values
        double rawInterval = range / labelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (axis.isGranularityEnabled())
            interval = interval < axis.getGranularity() ? axis.getGranularity() : interval;

        // Normalize interval
        double intervalMagnitude = Utils.roundToNextSignificant(Math.pow(10, (int) Math.log10(interval)));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or 90
            // if it's 0.0 after floor(), we use the old value
            interval = Math.floor(10.0 * intervalMagnitude) == 0.0
                    ? interval
                    : Math.floor(10.0 * intervalMagnitude);
        }

        boolean centeringEnabled = axis.isCenterAxisLabelsEnabled();
        int n = centeringEnabled ? 1 : 0;

        // force label count
        if (axis.isForceLabelsEnabled()) {

            float step = (float) range / (float) (labelCount - 1);
            axis.mEntryCount = labelCount;

            if (axis.mEntries.length < labelCount) {
                // Ensure stops contains at least numStops elements.
                axis.mEntries = new float[labelCount];
            }

            float v = min;

            for (int i = 0; i < labelCount; i++) {
                axis.mEntries[i] = v;
                v += step;
            }

            n = labelCount;

            // no forced count
        } else {

            double first = interval == 0.0 ? 0.0 : Math.ceil(yMin / interval) * interval;
            if (centeringEnabled) {
                first -= interval;
            }

            double last = interval == 0.0 ? 0.0 : Utils.nextUp(Math.floor(yMax / interval) * interval);

            double f;
            int i;

            if (interval != 0.0) {
                for (f = first; f <= last; f += interval) {
                    ++n;
                }
            }

            n++;

            axis.mEntryCount = n;

            if (axis.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                axis.mEntries = new float[n];
            }

            for (f = first, i = 0; i < n; f += interval, ++i) {

                if (f == 0.0) // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    f = 0.0;

                axis.mEntries[i] = (float) f;
            }
        }

        // set decimals
        if (interval < 1) {
            axis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            axis.mDecimals = 0;
        }

        if (centeringEnabled) {

            if (axis.mCenteredEntries.length < n) {
                axis.mCenteredEntries = new float[n];
            }

            float offset = (axis.mEntries[1] - axis.mEntries[0]) / 2f;

            for (int i = 0; i < n; i++) {
                axis.mCenteredEntries[i] = axis.mEntries[i] + offset;
            }
        }

        axis.mAxisMinimum = axis.mEntries[0];
        axis.mAxisMaximum = axis.mEntries[n-1];
        axis.mAxisRange = Math.abs(axis.mAxisMaximum - axis.mAxisMinimum);
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!yAxis.isEnabled() || !yAxis.isDrawLabelsEnabled())
            return;

        paintAxisLabels.setTypeface(yAxis.getTypeface());
        paintAxisLabels.setTextSize(yAxis.getTextSize());
        paintAxisLabels.setColor(yAxis.getTextColor());

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0,0);
        float factor = mChart.getFactor();

        final int from = yAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = yAxis.isDrawTopYLabelEntryEnabled()
                ? yAxis.mEntryCount
                : (yAxis.mEntryCount - 1);

        float xOffset = yAxis.getLabelXOffset();

        for (int j = from; j < to; j++) {

            float r = (yAxis.mEntries[j] - yAxis.mAxisMinimum) * factor;

            Utils.getPosition(center, r, mChart.getRotationAngle(), pOut);

            String label = yAxis.getFormattedLabel(j);

            c.drawText(label, pOut.x + xOffset, pOut.y, paintAxisLabels);
        }
        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    private Path mRenderLimitLinesPathBuffer = new Path();
    @Override
    public void renderLimitLines(Canvas c) {

        List<LimitLine> limitLines = yAxis.getLimitLines();

        if (limitLines == null)
            return;

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0,0);
        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            limitLinePaint.setColor(l.getLineColor());
            limitLinePaint.setPathEffect(l.getDashPathEffect());
            limitLinePaint.setStrokeWidth(l.getLineWidth());

            float r = (l.getLimit() - mChart.getYChartMin()) * factor;

            Path limitPath = mRenderLimitLinesPathBuffer;
            limitPath.reset();


            for (int j = 0; j < mChart.getData().getMaxEntryCountSet().getEntryCount(); j++) {

                Utils.getPosition(center, r, sliceangle * j + mChart.getRotationAngle(), pOut);

                if (j == 0)
                    limitPath.moveTo(pOut.x, pOut.y);
                else
                    limitPath.lineTo(pOut.x, pOut.y);
            }
            limitPath.close();

            c.drawPath(limitPath, limitLinePaint);
        }
        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }
}
