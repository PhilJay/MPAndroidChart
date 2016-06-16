package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
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
    public void computeAxis(float yMin, float yMax) {
        computeAxisValues(yMin, yMax);
    }

    @Override
    protected void computeAxisValues(float min, float max) {
        float yMin = min;
        float yMax = max;

        int labelCount = mYAxis.getLabelCount();
        double range = Math.abs(yMax - yMin);

        if (labelCount == 0 || range <= 0) {
            mYAxis.mEntries = new float[]{};
            mYAxis.mEntryCount = 0;
            return;
        }

        double rawInterval = range / labelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);
        double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }

        // force label count
        if (mYAxis.isForceLabelsEnabled()) {

            float step = (float) range / (float) (labelCount - 1);
            mYAxis.mEntryCount = labelCount;

            if (mYAxis.mEntries.length < labelCount) {
                // Ensure stops contains at least numStops elements.
                mYAxis.mEntries = new float[labelCount];
            }

            float v = min;

            for (int i = 0; i < labelCount; i++) {
                mYAxis.mEntries[i] = v;
                v += step;
            }

            // no forced count
        } else {

            // if the labels should only show min and max
            if (mYAxis.isShowOnlyMinMaxEnabled()) {

                mYAxis.mEntryCount = 2;
                mYAxis.mEntries = new float[2];
                mYAxis.mEntries[0] = yMin;
                mYAxis.mEntries[1] = yMax;

            } else {

                final double rawCount = yMin / interval;
                double first = rawCount < 0.0 ? Math.floor(rawCount) * interval : Math.ceil(rawCount) * interval;

                if (first == 0.0) // Fix for IEEE negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    first = 0.0;

                double last = Utils.nextUp(Math.floor(yMax / interval) * interval);

                double f;
                int i;
                int n = 0;
                for (f = first; f <= last; f += interval) {
                    ++n;
                }

                if (!mYAxis.isAxisMaxCustom())
                    n += 1;

                mYAxis.mEntryCount = n;

                if (mYAxis.mEntries.length < n) {
                    // Ensure stops contains at least numStops elements.
                    mYAxis.mEntries = new float[n];
                }

                for (f = first, i = 0; i < n; f += interval, ++i) {
                    mYAxis.mEntries[i] = (float) f;
                }
            }
        }

        if (interval < 1) {
            mYAxis.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mYAxis.mDecimals = 0;
        }

        if (mYAxis.mEntries[0] < yMin) {
            // If startAtZero is disabled, and the first label is lower that the axis minimum,
            // Then adjust the axis minimum
            mYAxis.mAxisMinimum = mYAxis.mEntries[0];
        }

        mYAxis.mAxisMaximum = mYAxis.mEntries[mYAxis.mEntryCount - 1];
        mYAxis.mAxisRange = Math.abs(mYAxis.mAxisMaximum - mYAxis.mAxisMinimum);
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mYAxis.isEnabled() || !mYAxis.isDrawLabelsEnabled())
            return;

        mAxisLabelPaint.setTypeface(mYAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mYAxis.getTextSize());
        mAxisLabelPaint.setColor(mYAxis.getTextColor());

        PointF center = mChart.getCenterOffsets();
        float factor = mChart.getFactor();

        int labelCount = mYAxis.mEntryCount;

        for (int j = 0; j < labelCount; j++) {

            if (j == labelCount - 1 && mYAxis.isDrawTopYLabelEntryEnabled() == false)
                break;

            float r = (mYAxis.mEntries[j] - mYAxis.mAxisMinimum) * factor;

            PointF p = Utils.getPosition(center, r, mChart.getRotationAngle());

            String label = mYAxis.getFormattedLabel(j);

            c.drawText(label, p.x + 10, p.y, mAxisLabelPaint);
        }
    }

    @Override
    public void renderLimitLines(Canvas c) {

        List<LimitLine> limitLines = mYAxis.getLimitLines();

        if (limitLines == null)
            return;

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            if (!l.isEnabled())
                continue;

            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());

            float r = (l.getLimit() - mChart.getYChartMin()) * factor;

            Path limitPath = new Path();

            for (int j = 0; j < mChart.getData().getXValCount(); j++) {

                PointF p = Utils.getPosition(center, r, sliceangle * j + mChart.getRotationAngle());

                if (j == 0)
                    limitPath.moveTo(p.x, p.y);
                else
                    limitPath.lineTo(p.x, p.y);
            }

            limitPath.close();

            c.drawPath(limitPath, mLimitLinePaint);
        }
    }
}
