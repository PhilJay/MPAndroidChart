
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.renderer.ScatterChartRenderer;

/**
 * The ScatterChart. Draws dots, triangles, squares and custom shapes into the
 * chartview.
 * 
 * @author Philipp Jahoda
 */
public class ScatterChart extends BarLineChartBase<ScatterData> {

    /** enum that defines the shape that is drawn where the values are */
    public enum ScatterShape {
        CROSS, TRIANGLE, CIRCLE, SQUARE, CUSTOM
    }

    public ScatterChart(Context context) {
        super(context);
    }

    public ScatterChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScatterChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new ScatterChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void calculateOffsets() {
        super.calculateOffsets();

        float offset = mData.getGreatestShapeSize() / 2f;
        mViewPortHandler.restrainViewPort(mViewPortHandler.offsetLeft() - offset,
                mViewPortHandler.offsetTop(), mViewPortHandler.offsetRight() - offset,
                mViewPortHandler.offsetBottom());

        prepareOffsetMatrix();
    }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        if (mDeltaX == 0 && mData.getYValCount() > 0)
            mDeltaX = 1;
    }

    /**
     * Returns all possible predefined ScatterShapes (excluding CUSTOM).
     * 
     * @return
     */
    public static ScatterShape[] getAllPossibleShapes() {
        return new ScatterShape[] {
                ScatterShape.SQUARE, ScatterShape.CIRCLE, ScatterShape.TRIANGLE, ScatterShape.CROSS
        };
    }
}
