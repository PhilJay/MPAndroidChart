
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.ArrayList;

public class YAxisRendererRadarChart extends YAxisRenderer {

    private RadarChart mChart;

    public YAxisRendererRadarChart(ViewPortHandler viewPortHandler, YAxis yAxis, RadarChart chart) {
        super(viewPortHandler, yAxis, null);

        this.mChart = chart;
    }

    @Override
    public void computeAxis(float yMin, float yMax) {
        
        int labelCount = mYAxis.getLabelCount();

        double max = yMax > 0 ? yMax : 1.0;
        double range = max - mYAxis.mAxisMinimum;

        double rawInterval = range / labelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);
        double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }

        double first = Math.ceil(mYAxis.mAxisMinimum / interval) * interval;
        double last = Utils.nextUp(Math.floor(max / interval) * interval);

        double f;
        int n = 0;
        for (f = first; f <= last; f += interval) {
            ++n;
        }

        mYAxis.mEntryCount = n;

        mYAxis.mAxisMaximum = (float) interval * n;
        
        Log.i("", "Maximum: " + mYAxis.mAxisMaximum);
    }

    @Override
    public void renderAxis(Canvas c) {

        if (!mYAxis.isEnabled())
            return;

        mAxisPaint.setTypeface(mYAxis.getTypeface());
        mAxisPaint.setTextSize(mYAxis.getTextSize());
        mAxisPaint.setColor(mYAxis.getTextColor());

        PointF center = mChart.getCenterOffsets();
        float factor = mChart.getFactor();

        int labelCount = mYAxis.mEntryCount;

        for (int j = 0; j < labelCount; j++) {

            if (j == labelCount - 1 && mYAxis.isDrawTopYLabelEntryEnabled() == false)
                break;

            float r = ((mChart.getYChartMax() / labelCount) * j) * factor;

            PointF p = Utils.getPosition(center, r, mChart.getRotationAngle());

            float val = r / factor;

            String label = Utils.formatNumber(val, mYAxis.mDecimals,
                    mYAxis.isSeparateThousandsEnabled());

            c.drawText(label, p.x + 10, p.y - 5, mAxisPaint);
        }
    }

    @Override
    public void renderLimitLines(Canvas c) {

        ArrayList<LimitLine> limitLines = mYAxis.getLimitLines();

        if (limitLines == null)
            return;

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);

            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());

            float r = l.getLimit() * factor;

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
