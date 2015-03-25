
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Baseclass of all axis renderers.
 * 
 * @author Philipp Jahoda
 */
public abstract class AxisRenderer extends Renderer {

    protected Transformer mTrans;

    /** paint object for the grid lines */
    protected Paint mGridPaint;

    /** paint for the x-label values */
    protected Paint mAxisLabelPaint;

    /** paint for the line surrounding the chart */
    protected Paint mAxisLinePaint;

    public AxisRenderer(ViewPortHandler viewPortHandler, Transformer trans) {
        super(viewPortHandler);

        this.mTrans = trans;

        mAxisLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mGridPaint = new Paint();
        mGridPaint.setColor(Color.GRAY);
        mGridPaint.setStrokeWidth(1f);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setAlpha(90);

        mAxisLinePaint = new Paint();
        mAxisLinePaint.setColor(Color.BLACK);
        mAxisLinePaint.setStrokeWidth(1f);
        mAxisLinePaint.setStyle(Style.STROKE);
    }

    /**
     * Returns the Paint object used for drawing the axis (labels).
     * 
     * @return
     */
    public Paint getPaintAxisLabels() {
        return mAxisLabelPaint;
    }

    /**
     * Returns the Paint object that is used for drawing the grid-lines of the
     * axis.
     * 
     * @return
     */
    public Paint getPaintGrid() {
        return mGridPaint;
    }

    /**
     * Returns the Paint object that is used for drawing the axis-line that goes
     * alongside the axis.
     * 
     * @return
     */
    public Paint getPaintAxisLine() {
        return mAxisLinePaint;
    }

    /**
     * Returns the Transformer object used for transforming the axis values.
     * 
     * @return
     */
    public Transformer getTransformer() {
        return mTrans;
    }

    /**
     * Draws the axis labels to the screen.
     * 
     * @param c
     */
    public abstract void renderAxisLabels(Canvas c);

    /**
     * Draws the grid lines belonging to the axis.
     * 
     * @param c
     */
    public abstract void renderGridLines(Canvas c);

    /**
     * Draws the line that goes alongside the axis.
     * 
     * @param c
     */
    public abstract void renderAxisLine(Canvas c);
}
