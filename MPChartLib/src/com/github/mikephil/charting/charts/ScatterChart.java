
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Path;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.util.ArrayList;

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
    protected void prepareContentRect() {
        if (isEmpty()) {
            super.prepareContentRect();
        } else {

            float offset = mData.getGreatestShapeSize() / 2f;

            mContentRect.set(mOffsetLeft - offset,
                    mOffsetTop,
                    getWidth() - mOffsetRight + offset,
                    getHeight() - mOffsetBottom);
        }
    }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        if (mDeltaX == 0 && mData.getYValCount() > 0)
            mDeltaX = 1;
    }

    @Override
    protected void drawDataSet(int index) {

        ScatterDataSet dataSet = mData.getDataSets().get(index);
        ArrayList<Entry> entries = dataSet.getYVals();

        float shapeHalf = dataSet.getScatterShapeSize() / 2f;

        float[] valuePoints = mTrans.generateTransformedValuesLineScatter(entries, yyy);

        ScatterShape shape = dataSet.getScatterShape();

        for (int j = 0; j < valuePoints.length * xxx; j += 2) {

            if (isOffContentRight(valuePoints[j]))
                break;

            // make sure the lines don't do shitty things outside bounds
            if (j != 0 && isOffContentLeft(valuePoints[j - 1])
                    && isOffContentTop(valuePoints[j + 1])
                    && isOffContentBottom(valuePoints[j + 1]))
                continue;

            // Set the color for the currently drawn value. If the index is
            // out of bounds, reuse colors.
            mRenderPaint.setColor(dataSet.getColor(j));

            if (shape == ScatterShape.SQUARE) {

                mDrawCanvas.drawRect(valuePoints[j] - shapeHalf,
                        valuePoints[j + 1] - shapeHalf, valuePoints[j]
                                + shapeHalf, valuePoints[j + 1]
                                + shapeHalf, mRenderPaint);

            } else if (shape == ScatterShape.CIRCLE) {

                mDrawCanvas.drawCircle(valuePoints[j], valuePoints[j + 1], shapeHalf,
                        mRenderPaint);

            } else if (shape == ScatterShape.CROSS) {

                mDrawCanvas.drawLine(valuePoints[j] - shapeHalf, valuePoints[j + 1],
                        valuePoints[j] + shapeHalf,
                        valuePoints[j + 1], mRenderPaint);
                mDrawCanvas.drawLine(valuePoints[j], valuePoints[j + 1] - shapeHalf,
                        valuePoints[j], valuePoints[j + 1]
                                + shapeHalf, mRenderPaint);

            } else if (shape == ScatterShape.TRIANGLE) {

                // create a triangle path
                Path tri = new Path();
                tri.moveTo(valuePoints[j], valuePoints[j + 1] - shapeHalf);
                tri.lineTo(valuePoints[j] + shapeHalf, valuePoints[j + 1] + shapeHalf);
                tri.lineTo(valuePoints[j] - shapeHalf, valuePoints[j + 1] + shapeHalf);
                tri.close();

                mDrawCanvas.drawPath(tri, mRenderPaint);

            } else if (shape == ScatterShape.CUSTOM) {

                Path customShape = dataSet.getCustomScatterShape();

                if (customShape == null)
                    return;

                // transform the provided custom path
                mTrans.pathValueToPixel(customShape);
                mDrawCanvas.drawPath(customShape, mRenderPaint);
            }
        }
    }

    @Override
    protected void drawValues() {
        // if values are drawn
        if (mDrawYValues && mData.getYValCount() < mMaxVisibleCount * mTrans.getScaleX()) {

            ArrayList<ScatterDataSet> dataSets = mData
                    .getDataSets();

            for (int i = 0; i < mData.getDataSetCount(); i++) {

                ScatterDataSet dataSet = dataSets.get(i);
                ArrayList<Entry> entries = dataSet.getYVals();

                float[] positions = mTrans.generateTransformedValuesLineScatter(entries, yyy);

                float shapeSize = dataSet.getScatterShapeSize();

                for (int j = 0; j < positions.length * xxx; j += 2) {

                    if (isOffContentRight(positions[j]))
                        break;

                    if (isOffContentLeft(positions[j]) || isOffContentTop(positions[j + 1])
                            || isOffContentBottom(positions[j + 1]))
                        continue;

                    float val = entries.get(j / 2).getVal();

                    if (mDrawUnitInChart) {

                        mDrawCanvas.drawText(mValueFormatter.getFormattedValue(val) + mUnit,
                                positions[j],
                                positions[j + 1] - shapeSize, mValuePaint);
                    } else {

                        mDrawCanvas.drawText(mValueFormatter.getFormattedValue(val), positions[j],
                                positions[j + 1] - shapeSize,
                                mValuePaint);
                    }
                }
            }
        }
    }

    @Override
    protected void drawHighlights() {

        for (int i = 0; i < mIndicesToHightlight.length; i++) {

            ScatterDataSet set = mData.getDataSetByIndex(mIndicesToHightlight[i]
                    .getDataSetIndex());

            if (set == null)
                continue;

            mHighlightPaint.setColor(set.getHighLightColor());

            int xIndex = mIndicesToHightlight[i].getXIndex(); // get the
                                                              // x-position

            if (xIndex > mDeltaX * xxx)
                continue;

            float y = set.getYValForXIndex(xIndex) * yyy; // get the
                                                              // y-position

            float[] pts = new float[] {
                    xIndex, mYChartMax, xIndex, mYChartMin, 0, y, mDeltaX, y
            };

            mTrans.pointValuesToPixel(pts);
            // draw the highlight lines
            mDrawCanvas.drawLines(pts, mHighlightPaint);
        }
    }

    @Override
    protected void drawAdditional() {

    }

    /**
     * Returns all possible predefined scattershapes.
     * 
     * @return
     */
    public static ScatterShape[] getAllPossibleShapes() {
        return new ScatterShape[] {
                ScatterShape.SQUARE, ScatterShape.CIRCLE, ScatterShape.TRIANGLE, ScatterShape.CROSS
        };
    }
}
