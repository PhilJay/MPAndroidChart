package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.github.mikephil.charting.utils.Utils;

/**
 * Baseclass of all axis label renderers.
 *
 * @author Sean Nicholls
 */
public class AxisLabelRenderer {


    public void drawLabel(Canvas c, Paint axisLabelPaint, String formattedLabel, float axisValue, int axisIndex, float x, float y, float w, float h) {
        //Utils.drawText(c, formattedLabel, x, y, axisLabelPaint, null, 0);
        c.drawText(formattedLabel, 0, formattedLabel.length(), x, y, axisLabelPaint);
    }

    public void drawLabel(Canvas c, Paint axisLabelPaint, String formattedLabel, float axisValue, int axisIndex, float x, float y, float w, float h, PointF anchor, float angleDegrees) {
        //Utils.drawText(c, formattedLabel, x, y, axisLabelPaint, anchor, angleDegrees);
        c.drawText(formattedLabel, 0, formattedLabel.length(), x, y, axisLabelPaint);
    }

}
