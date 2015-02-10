
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public abstract class AxisRenderer extends Renderer {

    protected Transformer mTrans;

    /** paint object for the grid lines */
    protected Paint mGridPaint;

    public AxisRenderer(ViewPortHandler viewPortHandler, Transformer trans) {
        super(viewPortHandler);

        this.mTrans = trans;

        mGridPaint = new Paint();
        mGridPaint.setColor(Color.GRAY);
        mGridPaint.setStrokeWidth(1f);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setAlpha(90);
    }

    public Transformer getTransformer() {
        return mTrans;
    }

    // public abstract void computeAxis(ChartInterface chart);

    /**
     * Draws the axis labels to the screen.
     * 
     * @param c
     */
    public abstract void renderAxis(Canvas c);

    /**
     * Draws the grid lines belonging to the axis.
     * 
     * @param c
     */
    public abstract void renderGridLines(Canvas c);
}
