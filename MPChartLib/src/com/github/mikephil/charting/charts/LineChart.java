
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Chart that draws lines, surfaces, circles, ...
 * 
 * @author Philipp Jahoda
 */
public class LineChart extends BarLineChartBase<LineData> {

    /** the width of the highlighning line */
    protected float mHighlightWidth = 3f;

    /** paint for the inner circle of the value indicators */
    protected Paint mCirclePaintInner;

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
    
    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        // if there is only one value in the chart
        if(mOriginalData.getYValCount() == 1) {
            mDeltaX = 1;
        }
    }

    @Override
    protected void drawHighlights() {

        for (int i = 0; i < mIndicesToHightlight.length; i++) {

            LineDataSet set = mOriginalData.getDataSetByIndex(mIndicesToHightlight[i]
                    .getDataSetIndex());

            mHighlightPaint.setColor(set.getHighLightColor());

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

    private class CPoint {

        public float x = 0f;
        public float y = 0f;

        public float dx = 0f;
        public float dy = 0f;

        public CPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * draws the given y values to the screen
     */
    @Override
    protected void drawData() {

        ArrayList<LineDataSet> dataSets = mCurrentData.getDataSets();

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            LineDataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
            mRenderPaint.setPathEffect(dataSet.getDashPathEffect());

            // if drawing cubic lines is enabled
            if (dataSet.isDrawCubicEnabled()) {

                // get the color that is specified for this position from the
                // DataSet
                mRenderPaint.setColor(dataSet.getColor(i));

                float intensity = dataSet.getCubicIntensity();

                // the path for the cubic-spline
                Path spline = new Path();

                ArrayList<CPoint> points = new ArrayList<CPoint>();
                for (Entry e : entries)
                    points.add(new CPoint(e.getXIndex(), e.getVal()));

                if (points.size() > 1) {
                    for (int j = 0; j < points.size() * mPhaseX; j++) {
                        
                        CPoint point = points.get(j);

                        if (j == 0) {
                            CPoint next = points.get(j + 1);
                            point.dx = ((next.x - point.x) * intensity);
                            point.dy = ((next.y - point.y) * intensity);
                        }
                        else if (j == points.size() - 1) {
                            CPoint prev = points.get(j - 1);
                            point.dx = ((point.x - prev.x) * intensity);
                            point.dy = ((point.y - prev.y) * intensity);
                        }
                        else {
                            CPoint next = points.get(j + 1);
                            CPoint prev = points.get(j - 1);
                            point.dx = ((next.x - prev.x) * intensity);
                            point.dy = ((next.y - prev.y) * intensity);
                        }

                        // create the cubic-spline path
                        if (j == 0) {
                            spline.moveTo(point.x, point.y * mPhaseY);
                        }
                        else {
                            CPoint prev = points.get(j - 1);
                            spline.cubicTo(prev.x + prev.dx, (prev.y + prev.dy) * mPhaseY, point.x - point.dx,
                                    (point.y - point.dy) * mPhaseY, point.x, point.y * mPhaseY);
                        }
                    }
                }

                // if filled is enabled, close the path
                if (dataSet.isDrawFilledEnabled()) {
                    
                    float fillMin = dataSet.getYMin() >= 0 ? mYChartMin : 0;

                    spline.lineTo((entries.size() - 1) * mPhaseX, fillMin);
                    spline.lineTo(0, fillMin);
                    spline.close();

                    mRenderPaint.setStyle(Paint.Style.FILL);
                } else {
                    mRenderPaint.setStyle(Paint.Style.STROKE);
                }
                
                transformPath(spline);

                mDrawCanvas.drawPath(spline, mRenderPaint);

                // draw normal (straight) lines
            } else {

                mRenderPaint.setStyle(Paint.Style.STROKE);

                float[] valuePoints = generateTransformedValuesLineScatter(entries);

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

//                    mRenderPaint.setShader(dataSet.getShader());
                    
                    float fillMin = dataSet.getYMin() >= 0 ? mYChartMin : 0;

                    Path filled = generateFilledPath(entries, fillMin);

                    transformPath(filled);

                    mDrawCanvas.drawPath(filled, mRenderPaint);

                    // restore alpha
                    mRenderPaint.setAlpha(255);
//                    mRenderPaint.setShader(null);
                }
            }
        }
    }

    /**
     * Generates the path that is used for filled drawing.
     * 
     * @param entries
     * @return
     */
    private Path generateFilledPath(ArrayList<? extends Entry> entries, float fillMin) {

        Path filled = new Path();
        filled.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal() * mPhaseY);

        // create a new path
        for (int x = 1; x < entries.size() * mPhaseX; x++) {

            Entry e = entries.get(x);
            filled.lineTo(e.getXIndex(), e.getVal() * mPhaseY);
        }

        // close up
        filled.lineTo(entries.get((int) ((entries.size() - 1) * mPhaseX)).getXIndex(), fillMin);
        filled.lineTo(entries.get(0).getXIndex(), fillMin);
        filled.close();

        return filled;
    }

    @Override
    protected void drawValues() {

        // if values are drawn
        if (mDrawYValues && mCurrentData.getYValCount() < mMaxVisibleCount * mScaleX) {

            ArrayList<LineDataSet> dataSets = mCurrentData.getDataSets();

            for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

                LineDataSet dataSet = dataSets.get(i);

                // make sure the values do not interfear with the circles
                int valOffset = (int) (dataSet.getCircleSize() * 1.75f);

                if (!dataSet.isDrawCirclesEnabled())
                    valOffset = valOffset / 2;

                ArrayList<Entry> entries = dataSet.getYVals();

                float[] positions = generateTransformedValuesLineScatter(entries);

                for (int j = 0; j < positions.length * mPhaseX; j += 2) {

                    if (isOffContentRight(positions[j]))
                        break;

                    if (isOffContentLeft(positions[j]) || isOffContentTop(positions[j + 1])
                            || isOffContentBottom(positions[j + 1]))
                        continue;

                    float val = entries.get(j / 2).getVal();

                    if (mDrawUnitInChart) {

                        mDrawCanvas.drawText(mValueFormatter.getFormattedValue(val) + mUnit, positions[j],
                                positions[j + 1]
                                        - valOffset, mValuePaint);
                    } else {

                        mDrawCanvas.drawText(mValueFormatter.getFormattedValue(val), positions[j],
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

        ArrayList<LineDataSet> dataSets = mCurrentData.getDataSets();

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            LineDataSet dataSet = dataSets.get(i);

            // if drawing circles is enabled for this dataset
            if (dataSet.isDrawCirclesEnabled()) {

                ArrayList<Entry> entries = dataSet.getYVals();

                float[] positions = generateTransformedValuesLineScatter(entries);

                for (int j = 0; j < positions.length * mPhaseX; j += 2) {

                    // Set the color for the currently drawn value. If the index
                    // is
                    // out of bounds, reuse colors.
                    mRenderPaint.setColor(dataSet.getCircleColor(j / 2));

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
        }

        return null;
    }
}
