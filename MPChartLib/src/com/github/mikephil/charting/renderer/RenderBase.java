
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.github.mikephil.charting.data.ChartData;

public abstract class RenderBase<T extends ChartData> {
    
    /** this is the paint object used for drawing the data onto the chart */
    protected Paint mRenderPaint;  
    
    /** the phase that is animated and influences the drawn values on the y-axis */
    protected float mPhaseY = 1f;

    /** the phase that is animated and influences the drawn values on the x-axis */
    protected float mPhaseX = 1f;
    
    public RenderBase() {
        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Style.FILL);
    }

    public void render(Canvas canvas, T data, Transformer trans) {

        renderData(canvas, data, trans);
        renderValues(canvas, data, trans);
        renderHighlights(canvas, data, trans);
    }

    protected abstract void renderData(Canvas canvas, T data, Transformer trans);

    protected abstract void renderValues(Canvas canvas, T data, Transformer trans);

    protected abstract void renderHighlights(Canvas canvas, T data, Transformer trans);
    
    /**
     * This gets the y-phase that is used to animate the values.
     * 
     * @return
     */
    public float getPhaseY() {
        return mPhaseY;
    }

    /**
     * This modifys the y-phase that is used to animate the values.
     * 
     * @param phase
     */
    public void setPhaseY(float phase) {
        mPhaseY = phase;
    }

    /**
     * This gets the x-phase that is used to animate the values.
     * 
     * @return
     */
    public float getPhaseX() {
        return mPhaseX;
    }

    /**
     * This modifys the x-phase that is used to animate the values.
     * 
     * @param phase
     */
    public void setPhaseX(float phase) {
        mPhaseX = phase;
    }
}
