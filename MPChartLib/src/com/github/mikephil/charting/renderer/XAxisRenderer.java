
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class XAxisRenderer extends AxisRenderer {

    protected XAxis mXAxis;

    public XAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, trans);

        this.mXAxis = xAxis;

        mAxisLabelPaint.setColor(Color.BLACK);
        mAxisLabelPaint.setTextAlign(Align.CENTER);
        mAxisLabelPaint.setTextSize(Utils.convertDpToPixel(10f));
    }

    public void computeAxis(float xValAverageLength, List<String> xValues) {

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());

        StringBuffer a = new StringBuffer();

        int max = (int) Math.round(xValAverageLength
                + mXAxis.getSpaceBetweenLabels());

        for (int i = 0; i < max; i++) {
            a.append("h");
        }

        mXAxis.mLabelWidth = Utils.calcTextWidth(mAxisLabelPaint, a.toString());
        mXAxis.mLabelHeight = Utils.calcTextHeight(mAxisLabelPaint, "Q");
        mXAxis.setValues(xValues);
    }

    @Override
    public void renderAxisLabels(Canvas c) {

        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        float yoffset = Utils.convertDpToPixel(4f);

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        if (mXAxis.getPosition() == XAxisPosition.TOP) {

            drawLabels(c, mViewPortHandler.offsetTop() - yoffset);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {

            drawLabels(c, mViewPortHandler.contentBottom() + mXAxis.mLabelHeight + yoffset * 1.5f);

        } else if (mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE) {

            drawLabels(c, mViewPortHandler.contentBottom() - yoffset);

        } else if (mXAxis.getPosition() == XAxisPosition.TOP_INSIDE) {

            drawLabels(c, mViewPortHandler.offsetTop() + yoffset + mXAxis.mLabelHeight);

        } else { // BOTH SIDED

            drawLabels(c, mViewPortHandler.offsetTop() - yoffset);
            drawLabels(c, mViewPortHandler.contentBottom() + mXAxis.mLabelHeight + yoffset * 1.6f);
        }
    }

    @Override
    public void renderAxisLine(Canvas c) {
        
        calcXBounds(mTrans);

        if (!mXAxis.isDrawAxisLineEnabled() || !mXAxis.isEnabled())
            return;

        mAxisLinePaint.setColor(mXAxis.getAxisLineColor());
        mAxisLinePaint.setStrokeWidth(mXAxis.getAxisLineWidth());

        if (mXAxis.getPosition() == XAxisPosition.TOP
                || mXAxis.getPosition() == XAxisPosition.TOP_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentTop(), mAxisLinePaint);
        }

        if (mXAxis.getPosition() == XAxisPosition.BOTTOM
                || mXAxis.getPosition() == XAxisPosition.BOTTOM_INSIDE
                || mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {
            c.drawLine(mViewPortHandler.contentLeft(),
                    mViewPortHandler.contentBottom(), mViewPortHandler.contentRight(),
                    mViewPortHandler.contentBottom(), mAxisLinePaint);
        }
    }

    /**
     * draws the x-labels on the specified y-position
     * 
     * @param pos
     */
    protected void drawLabels(Canvas c, float pos) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        int maxx = mMaxX;
        int minx = mMinX;

        if (maxx >= mXAxis.getValues().size())
            maxx = mXAxis.getValues().size()-1;

        if (minx < 0)
            minx = 0;

        for (int i = minx; i <= maxx; i += mXAxis.mAxisLabelModulus) {

            position[0] = i;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                String label = mXAxis.getValues().get(i);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mXAxis.getValues().size() - 1 && mXAxis.getValues().size() > 1) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                        if (width > mViewPortHandler.offsetRight() * 2
                                && position[0] + width > mViewPortHandler.getChartWidth())
                            position[0] -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        position[0] += width / 2;
                    }
                }

                c.drawText(label, position[0],
                        pos,
                        mAxisLabelPaint);
            }
        }
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled() || !mXAxis.isEnabled())
            return;

        float[] position = new float[] {
                0f, 0f
        };

        mGridPaint.setColor(mXAxis.getGridColor());
        mGridPaint.setStrokeWidth(mXAxis.getGridLineWidth());

        for (int i = mMinX; i <= mMaxX; i += mXAxis.mAxisLabelModulus) {

            position[0] = i;

            mTrans.pointValuesToPixel(position);

            if (position[0] >= mViewPortHandler.offsetLeft()
                    && position[0] <= mViewPortHandler.getChartWidth()) {

                c.drawLine(position[0], mViewPortHandler.offsetTop(), position[0],
                        mViewPortHandler.contentBottom(), mGridPaint);
            }
        }
    }
}
