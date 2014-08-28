package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class DrawableFillableLineChart extends LineChart {

    private Drawable mFillDrawable;
    private int mParentBackgroundColor = 0;

    private boolean mFillInverted = false;

    public DrawableFillableLineChart(Context context) {
        super(context);
    }

    public DrawableFillableLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableFillableLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * If set to true, it will fill the top section of the graph with the drawable fill
     * If false, it will fill the bottom section
     */
    public void setFillInverted(boolean inverted) {
        mFillInverted = inverted;
    }

    public boolean isFillInverted() {
        return mFillInverted;
    }

    @Override
    protected void drawData() {
        ArrayList<DataSet> dataSets =  mCurrentData.getDataSets();

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            DataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            float[] valuePoints = generateTransformedValues(entries, 0f);

            ArrayList<Integer> colors = mCt.getDataSetColors(i % mCt.getColors().size());

            Paint paint = mRenderPaint;

            if (mDrawFilled && entries.size() > 0) {
                fillChart(entries);
            }

            if (mDrawCubic) {

                paint.setColor(colors.get(i % colors.size()));

                Path spline = new Path();

                spline.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal());

                for (int x = 1; x < entries.size() - 3; x += 2) {

                    spline.cubicTo(entries.get(x).getXIndex(), entries.get(x).getVal(), entries
                            .get(x + 1).getXIndex(), entries.get(x + 1).getVal(), entries
                            .get(x + 2).getXIndex(), entries.get(x + 2).getVal());
                }

                transformPath(spline);

                mDrawCanvas.drawPath(spline, paint);

            } else {

                for (int j = 0; j < valuePoints.length - 2; j += 2) {

                    paint.setColor(colors.get(j % colors.size()));

                    if (isOffContentRight(valuePoints[j])) {
                        break;
                    }

                    if (j != 0 && isOffContentLeft(valuePoints[j - 1])
                            && isOffContentTop(valuePoints[j + 1])
                            && isOffContentBottom(valuePoints[j + 1]))
                        continue;

                    mDrawCanvas.drawLine(valuePoints[j], valuePoints[j + 1], valuePoints[j + 2],
                            valuePoints[j + 3], paint);
                }
            }
        }
    }

    private void fillChart(ArrayList<Entry> entries) {
        Path filled = new Path();
        filled.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal());

        for (int x = 1; x < entries.size(); x++) {
            filled.lineTo(entries.get(x).getXIndex(), entries.get(x).getVal());
        }

        if (mFillInverted) {
            filled.lineTo(entries.get(entries.size() - 1).getXIndex(), mYChartMax);
            filled.lineTo(entries.get(0).getXIndex(), mYChartMax);
        } else  {
            filled.lineTo(entries.get(entries.size() - 1).getXIndex(), mYChartMin);
            filled.lineTo(entries.get(0).getXIndex(), mYChartMin);
        }

        filled.close();

        transformPath(filled);

        mFillDrawable.setBounds(mDrawCanvas.getClipBounds());
        mFillDrawable.setDither(true);
        mFillDrawable.draw(mDrawCanvas);


        filled.toggleInverseFillType();


        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(mParentBackgroundColor);

        mDrawCanvas.drawPath(filled, backgroundPaint);
    }

    @Override
    @Deprecated
    public void setDrawFilled(boolean filled) {
        // Use enableChartFill or disableChartFill
    }

    public void enableChartFill(Drawable chartBottomFillDrawable, int chartTopColorFill) {
        mFillDrawable = chartBottomFillDrawable;
        mParentBackgroundColor = chartTopColorFill;
        mDrawFilled = true;
    }

    public void disableChartFill() {
        mDrawFilled = false;
        mFillDrawable = null;
        mParentBackgroundColor = 0;
    }


}
