
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Chart that draws lines, surfaces, circles, ...
 * 
 * @author Philipp Jahoda
 */
public class LineChart extends BarLineChartBase {

    /** the width of the highlighning line */
    protected float mHighlightWidth = 3f;

    /** paint for the inner circle of the value indicators */
    protected Paint mCirclePaintInner;

    /** flag for cubic curves instead of lines */
    protected boolean mDrawCubic = false;

    public LineChart(Context context) {
        super(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));
    }

    /**
     * Sets a LineData object as a model for the LineChart.
     * 
     * @param data
     */
    public void setData(LineData data) {
        super.setData(data);
    }

    @Override
    protected void drawHighlights() {

        // if there are values to highlight and highlighnting is enabled, do it
        if (mHighlightEnabled && mHighLightIndicatorEnabled && valuesToHighlight()) {

            for (int i = 0; i < mIndicesToHightlight.length; i++) {

                DataSet set = getDataSetByIndex(mIndicesToHightlight[i].getDataSetIndex());

                int xIndex = mIndicesToHightlight[i].getXIndex(); // get the
                                                                  // x-position

                if (xIndex > mDeltaX * mPhaseX)
                    continue;

                float y = set.getYValForXIndex(xIndex) * mPhaseY; // get the
                                                                  // y-position

                float[] pts = new float[] {
                        xIndex, mYChartMax, xIndex, mYChartMin, 0, y, mDeltaX, y
                };

                transformPointArray(pts);
                // draw the highlight lines
                mDrawCanvas.drawLines(pts, mHighlightPaint);
            }
        }
    }

    /**
     * draws the given y values to the screen
     */
    @Override
    protected void drawData() {

        ArrayList<LineDataSet> dataSets = (ArrayList<LineDataSet>) mCurrentData.getDataSets();

        mRenderPaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            LineDataSet dataSet = dataSets.get(i);
            ArrayList<? extends Entry> entries = dataSet.getYVals();

            float[] valuePoints = generateTransformedValues(entries, 0f);

            mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
            mRenderPaint.setPathEffect(dataSet.getDashPathEffect());

            if (mDrawCubic) {

                // get the color that is specified for this position from the
                // DataSet
                mRenderPaint.setColor(dataSet.getColor(i));

                Path spline = new Path();

                spline.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal());

                // create a new path
                for (int x = 1; x < entries.size() - 3; x += 2) {

                    // spline.rQuadTo(entries.get(x).getXIndex(),
                    // entries.get(x).getVal(), entries.get(x+1).getXIndex(),
                    // entries.get(x+1).getVal());

                    spline.cubicTo(entries.get(x).getXIndex(), entries.get(x).getVal(), entries
                            .get(x + 1).getXIndex(), entries.get(x + 1).getVal(), entries
                            .get(x + 2).getXIndex(), entries.get(x + 2).getVal());
                }

                // spline.close();

                transformPath(spline);

                mDrawCanvas.drawPath(spline, mRenderPaint);

            } else {

                for (int j = 0; j < (valuePoints.length - 2) * mPhaseX; j += 2) {

                    // get the color that is specified for this position from
                    // the DataSet, this will reuse colors, if the index is out
                    // of bounds
                    mRenderPaint.setColor(dataSet.getColor(j / 2));

                    if (isOffContentRight(valuePoints[j]))
                        break;

                    // make sure the lines don't do shitty things outside bounds
                    if (j != 0 && isOffContentLeft(valuePoints[j - 1])
                            && isOffContentTop(valuePoints[j + 1])
                            && isOffContentBottom(valuePoints[j + 1]))
                        continue;

                    mDrawCanvas.drawLine(valuePoints[j], valuePoints[j + 1], valuePoints[j + 2],
                            valuePoints[j + 3], mRenderPaint);
                }
            }

            mRenderPaint.setPathEffect(null);

            // if drawing filled is enabled
            if (dataSet.isDrawFilledEnabled() && entries.size() > 0) {
                // mDrawCanvas.drawVertices(VertexMode.TRIANGLE_STRIP,
                // valuePoints.length, valuePoints, 0,
                // null, 0, null, 0, null, 0, 0, paint);

                mRenderPaint.setStyle(Paint.Style.FILL);

                mRenderPaint.setColor(dataSet.getFillColor());
                // filled is drawn with less alpha
                mRenderPaint.setAlpha(dataSet.getFillAlpha());

                Path filled = generateFilledPath(entries);

                transformPath(filled);

                mDrawCanvas.drawPath(filled, mRenderPaint);

                // restore alpha
                mRenderPaint.setAlpha(255);
            }
        }
    }

    /**
     * Generates the path that is used for filled drawing.
     * 
     * @param entries
     * @return
     */
    private Path generateFilledPath(ArrayList<? extends Entry> entries) {

        Path filled = new Path();
        filled.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal() * mPhaseY);

        // create a new path
        for (int x = 1; x < entries.size() * mPhaseX; x++) {

            Entry e = entries.get(x);
            filled.lineTo(e.getXIndex(), e.getVal() * mPhaseY);
        }

        // close up
        filled.lineTo(entries.get((int) ((entries.size() - 1) * mPhaseX)).getXIndex(), mYChartMin);
        filled.lineTo(entries.get(0).getXIndex(), mYChartMin);
        filled.close();

        return filled;
    }

    /**
     * Calculates the middle point between two points and multiplies its
     * coordinates with the given smoothness _Mulitplier.
     * 
     * @param p1 First point
     * @param p2 Second point
     * @param _Result Resulting point
     * @param mult Smoothness multiplier
     */
    private void calculatePointDiff(PointF p1, PointF p2, PointF _Result, float mult) {
        float diffX = p2.x - p1.x;
        float diffY = p2.y - p1.y;
        _Result.x = (p1.x + (diffX * mult));
        _Result.y = (p1.y + (diffY * mult));
    }

    @Override
    protected void drawValues() {

        // if values are drawn
        if (mDrawYValues && mCurrentData.getYValCount() < mMaxVisibleCount * mScaleX) {

            ArrayList<LineDataSet> dataSets = (ArrayList<LineDataSet>) mCurrentData.getDataSets();

            for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

                LineDataSet dataSet = dataSets.get(i);

                // make sure the values do not interfear with the circles
                int valOffset = (int) (dataSet.getCircleSize() * 1.75f);

                if (!dataSet.isDrawCirclesEnabled())
                    valOffset = valOffset / 2;

                ArrayList<? extends Entry> entries = dataSet.getYVals();

                float[] positions = generateTransformedValues(entries, 0f);

                for (int j = 0; j < positions.length * mPhaseX; j += 2) {

                    if (isOffContentRight(positions[j]))
                        break;

                    if (isOffContentLeft(positions[j]) || isOffContentTop(positions[j + 1])
                            || isOffContentBottom(positions[j + 1]))
                        continue;

                    float val = entries.get(j / 2).getVal();

                    if (mDrawUnitInChart) {

                        mDrawCanvas.drawText(mFormatValue.format(val) + mUnit, positions[j],
                                positions[j + 1]
                                        - valOffset, mValuePaint);
                    } else {

                        mDrawCanvas.drawText(mFormatValue.format(val), positions[j],
                                positions[j + 1] - valOffset,
                                mValuePaint);
                    }
                }
            }
        }
    }

    /**
     * draws the circle value indicators
     */
    @Override
    protected void drawAdditional() {

        mRenderPaint.setStyle(Paint.Style.FILL);

        ArrayList<LineDataSet> dataSets = (ArrayList<LineDataSet>) mCurrentData.getDataSets();

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            LineDataSet dataSet = dataSets.get(i);

            // if drawing circles is enabled for this dataset
            if (dataSet.isDrawCirclesEnabled()) {

                ArrayList<? extends Entry> entries = dataSet.getYVals();

                float[] positions = generateTransformedValues(entries, 0f);

                for (int j = 0; j < positions.length * mPhaseX; j += 2) {

                    // Set the color for the currently drawn value. If the index
                    // is
                    // out of bounds, reuse colors.
                    mRenderPaint.setColor(dataSet.getCircleColor(j));

                    if (isOffContentRight(positions[j]))
                        break;

                    // make sure the circles don't do shitty things outside
                    // bounds
                    if (isOffContentLeft(positions[j]) || isOffContentTop(positions[j + 1])
                            || isOffContentBottom(positions[j + 1]))
                        continue;

                    mDrawCanvas.drawCircle(positions[j], positions[j + 1], dataSet.getCircleSize(),
                            mRenderPaint);
                    mDrawCanvas.drawCircle(positions[j], positions[j + 1],
                            dataSet.getCircleSize() / 2,
                            mCirclePaintInner);
                }
            } // else do nothing

        }
    }

    /**
     * set the width of the highlightning lines, default 3f
     * 
     * @param width
     */
    public void setHighlightLineWidth(float width) {
        mHighlightWidth = width;
    }

    /**
     * returns the width of the highlightning line, default 3f
     * 
     * @return
     */
    public float getHighlightLineWidth() {
        return mHighlightWidth;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_CIRCLES_INNER:
                mCirclePaintInner = p;
                break;
            case PAINT_HIGHLIGHT_LINE:
                mHighlightPaint = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        Paint p = super.getPaint(which);
        if (p != null)
            return p;

        switch (which) {
            case PAINT_CIRCLES_INNER:
                return mCirclePaintInner;
            case PAINT_HIGHLIGHT_LINE:
                return mHighlightPaint;
        }

        return null;
    }
}
