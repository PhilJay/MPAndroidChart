package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class LineRenderer extends RenderBase<LineData> {

    /**
     * Class needed for saving the points when drawing cubic-lines.
     * 
     * @author Philipp Jahoda
     */
    private class CPoint {

        public float x = 0f;
        public float y = 0f;

        /** x-axis distance */
        public float dx = 0f;

        /** y-axis distance */
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
    protected void renderData(Canvas canvas, LineData data, Transformer trans) {

        ArrayList<LineDataSet> dataSets = data.getDataSets();

        for (int i = 0; i < data.getDataSetCount(); i++) {

            LineDataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            if (entries.size() < 1)
                continue;

            mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
            mRenderPaint.setPathEffect(dataSet.getDashPathEffect());

            // if drawing cubic lines is enabled
            if (dataSet.isDrawCubicEnabled()) {

                // get the color that is specified for this position from the
                // DataSet
                mRenderPaint.setColor(dataSet.getColor());

                float intensity = dataSet.getCubicIntensity();

                // the path for the cubic-spline
                Path spline = new Path();

                ArrayList<CPoint> points = new ArrayList<CPoint>();
                for (Entry e : entries)
                    points.add(new CPoint(e.getXIndex(), e.getVal()));

                if (points.size() > 1) {
                    for (int j = 0; j < points.size() * 1f; j++) {

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
                            spline.moveTo(point.x, point.y * 1f);
                        }
                        else {
                            CPoint prev = points.get(j - 1);
                            spline.cubicTo(prev.x + prev.dx, (prev.y + prev.dy) * 1f, point.x
                                    - point.dx,
                                    (point.y - point.dy) * 1f, point.x, point.y * 1f);
                        }
                    }
                }

                // if filled is enabled, close the path
                if (dataSet.isDrawFilledEnabled()) {

//                    float fillMin = mFillFormatter
//                            .getFillLinePosition(dataSet, mOriginalData, mYChartMax, mYChartMin);

                    spline.lineTo((entries.size() - 1) * 1f, 0f);
                    spline.lineTo(0, 0f);
                    spline.close();

                    mRenderPaint.setStyle(Paint.Style.FILL);
                } else {
                    mRenderPaint.setStyle(Paint.Style.STROKE);
                }

                trans.pathValueToPixel(spline);

                canvas.drawPath(spline, mRenderPaint);

                // draw normal (straight) lines
            } else {

                mRenderPaint.setStyle(Paint.Style.STROKE);

                // more than 1 color
                if (dataSet.getColors() == null || dataSet.getColors().size() > 1) {

                    /**
                     * 
                     * 
                     * 
                     * 
                     * PHASE
                     * 
                     * 
                     */
                    float[] valuePoints = trans.generateTransformedValuesLineScatter(entries, 1f);

                    for (int j = 0; j < (valuePoints.length - 2) * 1f; j += 2) {

//                        if (isOffContentRight(valuePoints[j]))
//                            break;
//
//                        // make sure the lines don't do shitty things outside
//                        // bounds
//                        if (j != 0 && isOffContentLeft(valuePoints[j - 1])
//                                && isOffContentTop(valuePoints[j + 1])
//                                && isOffContentBottom(valuePoints[j + 1]))
//                            continue;

                        // get the color that is set for this line-segment
                        mRenderPaint.setColor(dataSet.getColor(j / 2));

                        canvas.drawLine(valuePoints[j], valuePoints[j + 1],
                                valuePoints[j + 2], valuePoints[j + 3], mRenderPaint);
                    }

                } else { // only one color per dataset

                    mRenderPaint.setColor(dataSet.getColor());

                    Path line = generateLinePath(entries);
                    trans.pathValueToPixel(line);

                    canvas.drawPath(line, mRenderPaint);
                }

                mRenderPaint.setPathEffect(null);

                // if drawing filled is enabled
                if (dataSet.isDrawFilledEnabled() && entries.size() > 0) {
                    // canvas.drawVertices(VertexMode.TRIANGLE_STRIP,
                    // valuePoints.length, valuePoints, 0,
                    // null, 0, null, 0, null, 0, 0, paint);

                    mRenderPaint.setStyle(Paint.Style.FILL);

                    mRenderPaint.setColor(dataSet.getFillColor());
                    // filled is drawn with less alpha
                    mRenderPaint.setAlpha(dataSet.getFillAlpha());

                    // mRenderPaint.setShader(dataSet.getShader());

                    Path filled = generateFilledPath(entries, 0);

                    trans.pathValueToPixel(filled); 

                    canvas.drawPath(filled, mRenderPaint);

                    // restore alpha
                    mRenderPaint.setAlpha(255);
                    // mRenderPaint.setShader(null);
                }
            }

            mRenderPaint.setPathEffect(null);
        }
    }
    
    /**
     * Generates the path that is used for filled drawing.
     * 
     * @param entries
     * @return
     */
    private Path generateFilledPath(ArrayList<Entry> entries, float fillMin) {

        Path filled = new Path();
        filled.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal() * 1f);

        // create a new path
        for (int x = 1; x < entries.size() * 1f; x++) {

            Entry e = entries.get(x);
            filled.lineTo(e.getXIndex(), e.getVal() * 1f);
        }

        // close up
        filled.lineTo(entries.get((int) ((entries.size() - 1) * 1f)).getXIndex(), fillMin);
        filled.lineTo(entries.get(0).getXIndex(), fillMin);
        filled.close();

        return filled;
    }

    /**
     * Generates the path that is used for drawing a single line.
     * 
     * @param entries
     * @return
     */
    private Path generateLinePath(ArrayList<Entry> entries) {

        Path line = new Path();
        line.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal() * 1f);

        // create a new path
        for (int x = 1; x < entries.size() * 1f; x++) {

            Entry e = entries.get(x);
            line.lineTo(e.getXIndex(), e.getVal() * 1f);
        }

        return line;
    }

    @Override
    protected void renderValues(Canvas canvas, LineData data, Transformer trans) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void renderHighlights(Canvas canvas, LineData data, Transformer trans) {
        // TODO Auto-generated method stub
        
    }

    

}
