
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Path;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class ScatterChart extends BarLineChartBase {

    /** enum that defines the shape that is drawn where the values are */
    public enum ScatterShape {
        CROSS, TRIANGLE, CIRCLE, SQUARE, CUSTOM
    }

    /**
     * custom path object the user can provide that is drawn where the values
     * are at
     */
    private Path mCustomScatterPath = null;

    /** the shape that is drawn onto the chart where the values are at */
    private ScatterShape mScatterShape = ScatterShape.SQUARE;

    /** the size the scattershape will have, in screen pixels */
    private float mShapeSize = 12f;

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
    protected void drawData() {
        ArrayList<DataSet> dataSets = mData.getDataSets();

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            DataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            float[] pos = generateTransformedValues(entries, 0f);

            // Get the colors for the DataSet at the current index. If the index
            // is out of bounds, reuse DataSet colors.
            ArrayList<Integer> colors = mCt.getDataSetColors(i % mCt.getColors().size());

            for (int j = 0; j < pos.length; j += 2) {

                // Set the color for the currently drawn value. If the index is
                // out of bounds, reuse colors.
                mRenderPaint.setColor(colors.get(j % colors.size()));

                if (isOffContentRight(pos[j]))
                    break;
                
                float shapeHalf = mShapeSize / 2f;

                // make sure the lines don't do shitty things outside bounds
                if (j != 0 && isOffContentLeft(pos[j - 1])
                        && isOffContentTop(pos[j + 1])
                        && isOffContentBottom(pos[j + 1]))
                    continue;

                if (mScatterShape == ScatterShape.SQUARE) {

                    mDrawCanvas.drawRect(pos[j] - shapeHalf, pos[j + 1] - shapeHalf, pos[j] + shapeHalf, pos[j + 1]
                            + shapeHalf, mRenderPaint);

                } else if (mScatterShape == ScatterShape.CIRCLE) {

                    mDrawCanvas.drawCircle(pos[j], pos[j + 1], mShapeSize / 2f, mRenderPaint);

                } else if (mScatterShape == ScatterShape.CROSS) {
                    
                    mDrawCanvas.drawLine(pos[j] - shapeHalf, pos[j + 1], pos[j] + shapeHalf,
                            pos[j + 1], mRenderPaint);
                    mDrawCanvas.drawLine(pos[j], pos[j + 1] - shapeHalf, pos[j], pos[j + 1]
                            + shapeHalf, mRenderPaint);

                } else if (mScatterShape == ScatterShape.TRIANGLE) {

                    Path tri = new Path();
                    tri.moveTo(pos[j], pos[j + 1] - shapeHalf);
                    tri.lineTo(pos[j] + shapeHalf, pos[j + 1] + shapeHalf);
                    tri.lineTo(pos[j] - shapeHalf, pos[j + 1] + shapeHalf);
                    tri.close();

                    mDrawCanvas.drawPath(tri, mRenderPaint);

                } else if (mScatterShape == ScatterShape.CUSTOM) {

                    if (mCustomScatterPath == null)
                        return;

                    // transform the provided custom path
                    transformPath(mCustomScatterPath);
                    mDrawCanvas.drawPath(mCustomScatterPath, mRenderPaint);
                }

            }
        }
    }

    @Override
    protected void drawValues() {
        // if values are drawn
        if (mDrawYValues && mData.getYValCount() < mMaxVisibleCount * mScaleX) {

            ArrayList<DataSet> dataSets = mData.getDataSets();

            for (int i = 0; i < mData.getDataSetCount(); i++) {

                DataSet dataSet = dataSets.get(i);
                ArrayList<Entry> entries = dataSet.getYVals();

                float[] positions = generateTransformedValues(entries, 0f);

                for (int j = 0; j < positions.length; j += 2) {

                    if (isOffContentRight(positions[j]))
                        break;

                    if (isOffContentLeft(positions[j]) || isOffContentTop(positions[j + 1])
                            || isOffContentBottom(positions[j + 1]))
                        continue;

                    float val = entries.get(j / 2).getVal();

                    if (mDrawUnitInChart) {

                        mDrawCanvas.drawText(mFormatValue.format(val) + mUnit, positions[j],
                                positions[j + 1] - mShapeSize, mValuePaint);
                    } else {

                        mDrawCanvas.drawText(mFormatValue.format(val), positions[j],
                                positions[j + 1] - mShapeSize,
                                mValuePaint);
                    }
                }
            }
        }
    }

    @Override
    protected void drawHighlights() {

        // if there are values to highlight and highlighnting is enabled, do it
        if (mHighlightEnabled && valuesToHighlight()) {

            for (int i = 0; i < mIndicesToHightlight.length; i++) {

                DataSet set = getDataSetByIndex(mIndicesToHightlight[i].getDataSetIndex());

                int xIndex = mIndicesToHightlight[i].getXIndex(); // get the
                                                                  // x-position
                float y = set.getYValForXIndex(xIndex); // get the y-position

                float[] pts = new float[] {
                        xIndex, mYChartMax, xIndex, mYChartMin, 0, y, mDeltaX, y
                };

                transformPointArray(pts);
                // draw the highlight lines
                mDrawCanvas.drawLines(pts, mHighlightPaint);
            }
        }
    }

    @Override
    protected void drawAdditional() {
        // TODO Auto-generated method stub

    }

    /**
     * Sets the size the drawn scattershape will have. This only applies for non
     * cusom shapes. Default 12f
     * 
     * @param size
     */
    public void setScatterShapeSize(float size) {
        mShapeSize = size;
    }

    /**
     * returns the currently set scatter shape size
     * 
     * @return
     */
    public float getScatterShapeSize() {
        return mShapeSize;
    }

    /**
     * Set the shape that is drawn on the position where the values are at. If
     * "CUSTOM" is chosen, you need to call setCustomScatterShape(...) and
     * provide a path object that is drawn.
     * 
     * @param shape
     */
    public void setScatterShape(ScatterShape shape) {
        mScatterShape = shape;
    }

    /**
     * returns the currently set scatter shape
     * 
     * @return
     */
    public ScatterShape getScatterShape() {
        return mScatterShape;
    }

    /**
     * Sets a path object as the shape to be drawn where the values are at. Do
     * not forget to call setScatterShape(ScatterShape.CUSTOM).
     * 
     * @param shape
     */
    public void setCustomScatterShape(Path shape) {
        mCustomScatterPath = shape;
    }
}
