
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

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
    public void renderAxisLabels(Canvas c) {

        if (!mYAxis.isEnabled() || !mYAxis.isDrawLabelsEnabled())
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

            float r = mYAxis.mEntries[j] * factor;

            PointF p = Utils.getPosition(center, r, mChart.getRotationAngle());

            String label = mYAxis.getFormattedLabel(j);

            c.drawText(label, p.x + 10, p.y, mAxisPaint);
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
