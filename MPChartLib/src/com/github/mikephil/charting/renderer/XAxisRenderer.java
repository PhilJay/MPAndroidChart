
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XLabelPosition;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class XAxisRenderer extends AxisRenderer {

    /** paint for the x-label values */
    protected Paint mXLabelPaint;

    protected XAxis mXAxis;

    public XAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, trans);

        this.mXAxis = xAxis;

        mXLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXLabelPaint.setColor(Color.BLACK);
        mXLabelPaint.setTextAlign(Align.CENTER);
        mXLabelPaint.setTextSize(Utils.convertDpToPixel(10f));
    }

    public void computeAxis(float xValAverageLength, ArrayList<String> xValues) {

        StringBuffer a = new StringBuffer();

        int max = (int) Math.round(xValAverageLength
                + mXAxis.getSpaceBetweenLabels());

        for (int i = 0; i < max; i++) {
            a.append("h");
        }

        mXAxis.mLabelWidth = Utils.calcTextWidth(mXLabelPaint, a.toString());
        mXAxis.mLabelHeight = Utils.calcTextHeight(mXLabelPaint, "Q");
        mXAxis.setValues(xValues);
    }

    @Override
    public void renderAxis(Canvas c) {

        float yoffset = Utils.convertDpToPixel(4f);

        mXLabelPaint.setTypeface(mXAxis.getTypeface());
        mXLabelPaint.setTextSize(mXAxis.getTextSize());
        mXLabelPaint.setColor(mXAxis.getTextColor());

        if (mXAxis.getPosition() == XLabelPosition.TOP) {

            drawLabels(c, mViewPortHandler.offsetTop() - yoffset);

        } else if (mXAxis.getPosition() == XLabelPosition.BOTTOM) {

            drawLabels(c, mViewPortHandler.contentBottom() + mXAxis.mLabelHeight + yoffset * 1.5f);

        } else if (mXAxis.getPosition() == XLabelPosition.BOTTOM_INSIDE) {

            drawLabels(c, mViewPortHandler.contentBottom() - yoffset);

        } else if (mXAxis.getPosition() == XLabelPosition.TOP_INSIDE) {

            drawLabels(c, mViewPortHandler.offsetTop() + yoffset + mXAxis.mLabelHeight);

        } else { // BOTH SIDED

            drawLabels(c, mViewPortHandler.offsetTop() - yoffset);
            drawLabels(c, mViewPortHandler.contentBottom() + mXAxis.mLabelHeight + yoffset * 1.6f);
        }
    }

    /**
     * draws the x-labels on the specified y-position
     * 
     * @param yPos
     */
    protected void drawLabels(Canvas c, float yPos) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        for (int i = 0; i < mXAxis.getValues().size(); i += mXAxis.mXAxisLabelModulus) {

            position[0] = i;

            // center the text
            if (mXAxis.isCenterXLabelsEnabled())
                position[0] += 0.5f;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                String label = mXAxis.getValues().get(i);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mXAxis.getValues().size() - 1) {
                        float width = Utils.calcTextWidth(mXLabelPaint, label);

                        if (width > mViewPortHandler.offsetRight() * 2
                                && position[0] + width > mViewPortHandler.getChartWidth())
                            position[0] -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mXLabelPaint, label);
                        position[0] += width / 2;
                    }
                }

                c.drawText(label, position[0],
                        yPos,
                        mXLabelPaint);
            }
        }
    }

    @Override
    public void renderGridLines(Canvas c) {

        if (!mXAxis.isDrawGridLinesEnabled())
            return;

        float[] position = new float[] {
                0f, 0f
        };

        mGridPaint.setColor(mXAxis.getGridColor());

        for (int i = 0; i < mXAxis.getValues().size(); i += mXAxis.mXAxisLabelModulus) {

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
