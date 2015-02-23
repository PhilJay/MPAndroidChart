
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * Renderer for the HorizontalBarChart.
 * 
 * @author Philipp Jahoda
 */
public class HorizontalBarChartRenderer extends BarChartRenderer {
    
    private float xOffset = 0f;
    private float yOffset = 0f;

    public HorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        
        mValuePaint.setTextAlign(Align.LEFT);
        yOffset = Utils.calcTextHeight(mValuePaint, "Q");
        xOffset = Utils.convertDpToPixel(4f);
    }

    @Override
    protected void prepareBar(float x, float y, float barspace, Transformer trans) {

        float spaceHalf = barspace / 2f;

        float top = x - 0.5f + spaceHalf;
        float bottom = x + 0.5f - spaceHalf;
        float left = y >= 0 ? y : 0;
        float right = y <= 0 ? y : 0;

        mBarRect.set(left, top, right, bottom);

        trans.rectValueToPixelHorizontal(mBarRect, mAnimator.getPhaseY());

        // if a shadow is drawn, prepare it too
        if (mChart.isDrawBarShadowEnabled()) {
            mBarShadow.set(mViewPortHandler.contentLeft(), mBarRect.top,
                    mViewPortHandler.contentRight(),
                    mBarRect.bottom);
        }
    }
    
    @Override
    public float[] getTransformedValues(Transformer trans, ArrayList<BarEntry> entries, int dataSetIndex) {       
        return trans.generateTransformedValuesHorizontalBarChart(entries, dataSetIndex, mChart.getBarData(), mAnimator.getPhaseY());
    }
    
    @Override
    protected void drawValue(Canvas c, float val, float xPos, float yPos) {
        super.drawValue(c, val, xPos + xOffset, yPos + yOffset);
    }
}
