package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 11/07/15.
 * Modified by Gurgen Khachatryan on 08/01/24.
 */
public abstract class LineScatterCandleRadarRenderer extends BarLineScatterCandleBubbleRenderer {

    /**
     * path that is used for drawing highlight-lines (drawLines(...) cannot be used because of dashes)
     */
    private final Path mHighlightLinePath = new Path();
    private final Paint mPointPaint = new Paint();
    private final Paint mPointBorderPaint = new Paint();

    public LineScatterCandleRadarRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    /**
     * Draws a highlight circle if enabled.
     *
     * @param c chart Canvas
     * @param x x-position of the highlight line intersection
     * @param y y-position of the highlight line intersection
     * @param set the currently drawn dataset
     */
    protected void drawHighlightCircle(Canvas c, float x, float y, ILineScatterCandleRadarDataSet<?> set) {
        if (set.isCircleHighlightEnabled()) {
            float radius = set.getHighlightCircleRadius();
            if (set.getHighlightCircleBorderWidth() > 0f) {
                float innerCircleRadius = radius + set.getHighlightCircleBorderWidth();
                mPointBorderPaint.setColor(set.getHighLightCircleBorderColor());
                c.drawCircle(x, y, innerCircleRadius, mPointBorderPaint);
            }
            mPointPaint.setColor(set.getHighLightCircleColor());
            c.drawCircle(x, y, radius, mPointPaint);
        }
    }

    /**
     * Draws vertical & horizontal highlight-lines if enabled.
     *
     * @param c chart Canvas
     * @param x x-position of the highlight line intersection
     * @param y y-position of the highlight line intersection
     * @param set the currently drawn dataset
     */
    protected void drawHighlightLines(Canvas c, float x, float y, ILineScatterCandleRadarDataSet<?> set) {

        // set color and stroke-width
        mHighlightPaint.setColor(set.getHighLightColor());
        mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());

        // draw highlighted lines (if enabled)
        mHighlightPaint.setPathEffect(set.getDashPathEffectHighlight());

        // draw vertical highlight lines
        if (set.isVerticalHighlightIndicatorEnabled()) {

            // create vertical path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(x, mViewPortHandler.contentTop());
            mHighlightLinePath.lineTo(x, mViewPortHandler.contentBottom());

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }

        // draw horizontal highlight lines
        if (set.isHorizontalHighlightIndicatorEnabled()) {

            // create horizontal path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(mViewPortHandler.contentLeft(), y);
            mHighlightLinePath.lineTo(mViewPortHandler.contentRight(), y);

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }
    }
}
