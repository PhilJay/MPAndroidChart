
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.HorizontalBarChartTransformer;
import com.github.mikephil.charting.utils.Utils;

/**
 * BarChart with horizontal bar orientation. In this implementation, x- and
 * y-axis are switched.
 */
public class HorizontalBarChart extends BarChart
{
    public HorizontalBarChart(Context context)
    {
        super(context);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs,
            int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        setDrawXLabels(false);
//        setDrawYLabels(true);
//        mTrans = new HorizontalBarChartTransformer();
        mValuePaint.setTextAlign(Paint.Align.LEFT);
    }

    /**
     * Prepares a bar for drawing on the specified x-index and y-position. Also
     * prepares the shadow-bar if enabled.
     *
     * @param x the x-position
     * @param y the y-position
     * @param barspace the space between bars
     */
//    @Override
//    protected void prepareBar(float x, float y, float barspace) {
//        super.prepareBar(x, y, barspace);
//
//        float spaceHalf = barspace / 2f;
//
//        float top = x + spaceHalf;
//        float bottom = x + 1f - spaceHalf;
//
//        mBarRect.set(0, top, y, bottom);
//
//        mTrans.rectValueToPixel(mBarRect, mPhaseY);
//
//        // if a shadow is drawn, prepare it too
//        if (mDrawBarShadow) {
//            mBarShadow.set(mBarRect.left, mOffsetTop, mBarRect.right, getHeight() - mOffsetBottom);
//        }
//    }

//    @Override
//    protected void calcModulus() {
//
//        float[] values = new float[9];
//        mTrans.getTouchMatrix().getValues(values);
//
//        mXLabels.mYAxisLabelModulus = (int) Math
//                .ceil((mData.getXValCount() * mXLabels.mLabelHeight)
//                        / (mContentRect.height() * values[Matrix.MSCALE_Y]));
//    }
//
//    @Override
//    protected void drawXLabels(float yPos)
//    {
//        if (!mDrawXLabels)
//            return;
//
//        mXLabelPaint.setTextAlign(Paint.Align.RIGHT);
//        // pre allocate to save performance (dont allocate in loop)
//        float[] position = new float[] {
//                0f, 0f
//        };
//
//        float offset = Utils.calcTextHeight(mXLabelPaint, mData.getXVals().get(0)) / 2f;
//
//        int step = mData.getDataSetCount();
//
//        for (int i = 0; i < mData.getXValCount(); i += mXLabels.mYAxisLabelModulus) {
//
//            position[1] = i * step + i * mData.getGroupSpace()
//                    + mData.getGroupSpace() / 2f;
//
//            // center the text
//            if (mXLabels.isCenterXLabelsEnabled())
//                position[1] += (step / 2f);
//
//            mTrans.pointValuesToPixel(position);
//
//            if (position[1] >= mOffsetTop && position[1] <= getHeight() - mOffsetBottom)
//            {
//                String label = mData.getXVals().get(i);
//
//                mDrawCanvas.drawText(label, mOffsetLeft - 10, position[1] + offset, mXLabelPaint);
//            }
//        }
//    }

    private static String TALL_VALUE = "100%";

    @Override
    protected float getPositiveYOffset(boolean drawAboveValueBar)
    {
        Rect bounds = new Rect();
        mValuePaint.getTextBounds(TALL_VALUE, 0, TALL_VALUE.length(), bounds);

        return bounds.height() / 2;
    }

    @Override
    protected float getNegativeYOffset(boolean drawAboveValueBar)
    {
        return getPositiveYOffset(drawAboveValueBar);
    }

//    /**
//     * Sets up the y-axis labels. Computes the desired number of labels between
//     * the two given extremes. Unlike the papareXLabels() method, this method
//     * needs to be called upon every refresh of the view.
//     *
//     * @return
//     */
//    @Override
//    protected void prepareYLabels()
//    {
//        mYLabels = new XLabelsAsYLabels();
//        // super.prepareYLabels();
//
//    }

    private class XLabelsAsYLabels extends YAxis
    {
        public XLabelsAsYLabels(AxisDependency position) {
            super(position);
            // TODO Auto-generated constructor stub
        }

        /**
         * Returns the longest formatted label (in terms of characters) the
         * y-labels contain.
         *
         * @return
         */
        @Override
        public String getLongestLabel()
        {
            String longest = "";

            for (int i = 0; i < mData.getXValCount(); i++)
            {
                String text = mData.getXVals().get(i);

                if (longest.length() < text.length())
                    longest = text;
            }

            return longest;
        }

        /**
         * Returns the formatted y-label at the specified index. This will
         * either use the auto-formatter or the custom formatter (if one is
         * set).
         *
         * @param index
         * @return
         */
        @Override
        public String getFormattedLabel(int index)
        {
            super.getFormattedLabel(index);
            if (index < 0)
                return "";

            return mData.getXVals().get(index);
        }
    }
}
