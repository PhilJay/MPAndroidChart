package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by philipp on 11/07/15.
 */
public abstract class LineScatterCandleRadarRenderer extends DataRenderer {

    public LineScatterCandleRadarRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    /**
     * Draws vertical & horizontal highlight-lines if enabled.
     * @param c
     * @param pts
     * @param horizontal
     * @param vertical
     */
    protected void drawHighlightLines(Canvas c, float[] pts, boolean horizontal, boolean vertical) {

            // draw vertical highlight lines
            if(vertical)
                c.drawLine(pts[0], pts[1], pts[2], pts[3], mHighlightPaint);

            // draw horizontal highlight lines
            if(horizontal)
                c.drawLine(pts[4], pts[5], pts[6], pts[7], mHighlightPaint);
    }
}
