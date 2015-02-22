
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.utils.Transformer;

/**
 * Renderer for the HorizontalBarChart.
 * 
 * @author Philipp Jahoda
 */
public class HorizontalBarChartRenderer extends BarChartRenderer {

    public HorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
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
            mBarShadow.set(mBarRect.left, mViewPortHandler.offsetTop(), mBarRect.right,
                    mViewPortHandler.contentBottom());
        }
    }
}
