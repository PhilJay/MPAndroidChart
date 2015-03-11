
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.utils.Transformer;

/**
 * Abstract baseclass of all Renderers.
 * 
 * @author Philipp Jahoda
 */
public abstract class Renderer {

    /**
     * the component that handles the drawing area of the chart and it's offsets
     */
    protected ViewPortHandler mViewPortHandler;

    protected int mMinX = 0;

    protected int mMaxX = 0;

    public Renderer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }

    /**
     * Returns true if the specified value fits in between the provided min
     * and max bounds, false if not.
     * 
     * @param val
     * @param min
     * @param max
     * @return
     */
    protected boolean fitsBounds(float val, float min, float max) {

        if (val < min || val > max)
            return false;
        else
            return true;
    }

    /**
     * Calculates the minimum and maximum x-value the chart can currently
     * display (with the given zoom level).
     * 
     * @param trans
     */
    protected void calcXBounds(Transformer trans) {

        double minx = trans.getValuesByTouchPoint(mViewPortHandler.contentLeft(), 0).x;
        double maxx = trans.getValuesByTouchPoint(mViewPortHandler.contentRight(), 0).x;

        if (!Double.isInfinite(minx))
            mMinX = (int) minx;
        if (!Double.isInfinite(maxx))
            mMaxX = (int) maxx; 
    }
}
