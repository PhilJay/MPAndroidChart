
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class XAxisRendererRadarChart extends XAxisRenderer {

    private RadarChart mChart;

    public XAxisRendererRadarChart(ViewPortHandler viewPortHandler, XAxis xAxis, RadarChart chart) {
        super(viewPortHandler, xAxis, null);

        mChart = chart;
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        mAxisPaint.setTypeface(mXAxis.getTypeface());
        mAxisPaint.setTextSize(mXAxis.getTextSize());
        mAxisPaint.setColor(mXAxis.getTextColor());

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        for (int i = 0; i < mXAxis.getValues().size(); i++) {

            String text = mXAxis.getValues().get(i);

            float angle = (sliceangle * i + mChart.getRotationAngle()) % 360f;

            PointF p = Utils.getPosition(center, mChart.getYRange() * factor
                    + mXAxis.mLabelWidth / 2f, angle);

            c.drawText(text, p.x, p.y + mXAxis.mLabelHeight / 2f, mAxisPaint);
        }
    }
}
