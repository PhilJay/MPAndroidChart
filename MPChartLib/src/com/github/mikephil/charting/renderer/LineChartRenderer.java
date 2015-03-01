
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.LineDataProvider;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Transformer;

import java.util.ArrayList;

public class LineChartRenderer extends DataRenderer {

    protected LineDataProvider mChart;

    /** paint for the inner circle of the value indicators */
    protected Paint mCirclePaintInner;

    public LineChartRenderer(LineDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);
    }

    @Override
    public void drawData(Canvas c) {

        LineData lineData = mChart.getLineData();

        for (LineDataSet set : lineData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    /**
     * Class needed for saving the points when drawing cubic-lines.
     * 
     * @author Philipp Jahoda
     */
    protected class CPoint {

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

    protected void drawDataSet(Canvas c, LineDataSet dataSet) {

        ArrayList<Entry> entries = dataSet.getYVals();

        if (entries.size() < 1)
            return;

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setPathEffect(dataSet.getDashPathEffect());

        // if drawing cubic lines is enabled
        if (dataSet.isDrawCubicEnabled()) {
            drawCubic(c, dataSet, entries);

            // draw normal (straight) lines
        } else {
            drawLinear(c, dataSet, entries);
        }

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubic(Canvas c, LineDataSet dataSet, ArrayList<Entry> entries) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float intensity = dataSet.getCubicIntensity();

        // the path for the cubic-spline
        Path spline = new Path();

        ArrayList<CPoint> points = new ArrayList<CPoint>();
        for (Entry e : entries) {
            if (e != null)
                points.add(new CPoint(e.getXIndex(), e.getVal()));
        }

        if (points.size() > 1) {
            for (int j = 0; j < points.size() * phaseX; j++) {

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
                    spline.moveTo(point.x, point.y * phaseY);
                }
                else {
                    CPoint prev = points.get(j - 1);
                    spline.cubicTo(prev.x + prev.dx, (prev.y + prev.dy) * phaseY, point.x
                            - point.dx,
                            (point.y - point.dy) * phaseY, point.x, point.y * phaseY);
                }
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            // create a new path, this is bad for performance
            drawCubicFill(c, dataSet, new Path(spline), trans);
        }

        mRenderPaint.setColor(dataSet.getColor());

        mRenderPaint.setStyle(Paint.Style.STROKE);

        trans.pathValueToPixel(spline);

        c.drawPath(spline, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicFill(Canvas c, LineDataSet dataSet, Path spline, Transformer trans) {

        float fillMin = mChart.getFillFormatter()
                .getFillLinePosition(dataSet, mChart.getLineData(), mChart.getYChartMax(),
                        mChart.getYChartMin());

        spline.lineTo(
                dataSet.getYVals()
                        .get((int) ((dataSet.getYVals().size() - 1) * mAnimator.getPhaseX()))
                        .getXIndex(), fillMin);
        spline.lineTo(mChart.getXChartMin(), fillMin);
        spline.close();

        mRenderPaint.setStyle(Paint.Style.FILL);

        mRenderPaint.setColor(dataSet.getFillColor());
        // filled is drawn with less alpha
        mRenderPaint.setAlpha(dataSet.getFillAlpha());

        trans.pathValueToPixel(spline);
        c.drawPath(spline, mRenderPaint);

        mRenderPaint.setAlpha(255);
    }

    protected void drawLinear(Canvas c, LineDataSet dataSet, ArrayList<Entry> entries) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        mRenderPaint.setStyle(Paint.Style.STROKE);

        // more than 1 color
        if (dataSet.getColors() == null || dataSet.getColors().size() > 1) {

            float[] positions = trans.generateTransformedValuesLine(
                    entries, phaseY);

            for (int j = 0; j < (positions.length - 2) * phaseX; j += 2) {

                if (!mViewPortHandler.isInBoundsRight(positions[j]))
                    break;

                // make sure the lines don't do shitty things outside
                // bounds
                if (j != 0 && !mViewPortHandler.isInBoundsLeft(positions[j - 1])
                        && !mViewPortHandler.isInBoundsY(positions[j + 1]))
                    continue;

                // get the color that is set for this line-segment
                mRenderPaint.setColor(dataSet.getColor(j / 2));

                c.drawLine(positions[j], positions[j + 1],
                        positions[j + 2], positions[j + 3], mRenderPaint);
            }

        } else { // only one color per dataset

            mRenderPaint.setColor(dataSet.getColor());

            Path line = generateLinePath(entries);
            trans.pathValueToPixel(line);

            c.drawPath(line, mRenderPaint);
        }

        mRenderPaint.setPathEffect(null);

        // if drawing filled is enabled
        if (dataSet.isDrawFilledEnabled() && entries.size() > 0) {
            drawLinearFill(c, dataSet, entries, trans);
        }
    }

    protected void drawLinearFill(Canvas c, LineDataSet dataSet, ArrayList<Entry> entries,
            Transformer trans) {

        mRenderPaint.setStyle(Paint.Style.FILL);

        mRenderPaint.setColor(dataSet.getFillColor());
        // filled is drawn with less alpha
        mRenderPaint.setAlpha(dataSet.getFillAlpha());

        // mRenderPaint.setShader(dataSet.getShader());

        Path filled = generateFilledPath(
                entries,
                mChart.getFillFormatter().getFillLinePosition(dataSet, mChart.getLineData(),
                        mChart.getYChartMax(), mChart.getYChartMin()));

        trans.pathValueToPixel(filled);

        c.drawPath(filled, mRenderPaint);

        // restore alpha
        mRenderPaint.setAlpha(255);
        // mRenderPaint.setShader(null);
    }

    /**
     * Generates the path that is used for filled drawing.
     * 
     * @param entries
     * @return
     */
    private Path generateFilledPath(ArrayList<Entry> entries, float fillMin) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        Path filled = new Path();
        filled.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal() * phaseY);

        // create a new path
        for (int x = 1; x < entries.size() * phaseX; x++) {

            Entry e = entries.get(x);
            filled.lineTo(e.getXIndex(), e.getVal() * phaseY);
        }

        // close up
        filled.lineTo(entries.get((int) ((entries.size() - 1) * phaseX)).getXIndex(), fillMin);
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

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        Path line = new Path();
        line.moveTo(entries.get(0).getXIndex(), entries.get(0).getVal() * phaseY);

        // create a new path
        for (int x = 1; x < entries.size() * phaseX; x++) {

            Entry e = entries.get(x);
            line.lineTo(e.getXIndex(), e.getVal() * phaseY);
        }

        return line;
    }

    @Override
    public void drawValues(Canvas c) {

        if (mChart.getLineData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            ArrayList<LineDataSet> dataSets = mChart.getLineData().getDataSets();

            for (int i = 0; i < dataSets.size(); i++) {

                LineDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled())
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                // make sure the values do not interfear with the circles
                int valOffset = (int) (dataSet.getCircleSize() * 1.75f);

                if (!dataSet.isDrawCirclesEnabled())
                    valOffset = valOffset / 2;

                ArrayList<Entry> entries = dataSet.getYVals();

                float[] positions = trans.generateTransformedValuesLine(
                        entries, mAnimator.getPhaseY());

                for (int j = 0; j < positions.length * mAnimator.getPhaseX(); j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    float val = entries.get(j / 2).getVal();

                    c.drawText(dataSet.getValueFormatter().getFormattedValue(val), x,
                            y - valOffset,
                            mValuePaint);
                }
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        drawCircles(c);
    }

    protected void drawCircles(Canvas c) {
        mRenderPaint.setStyle(Paint.Style.FILL);

        ArrayList<LineDataSet> dataSets = mChart.getLineData().getDataSets();

        for (int i = 0; i < mChart.getLineData().getDataSetCount(); i++) {

            LineDataSet dataSet = dataSets.get(i);
            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            // if drawing circles is enabled for this dataset
            if (dataSet.isDrawCirclesEnabled()) {

                ArrayList<Entry> entries = dataSet.getYVals();

                float[] positions = trans.generateTransformedValuesLine(
                        entries, mAnimator.getPhaseY());

                for (int j = 0; j < positions.length * mAnimator.getPhaseX(); j += 2) {

                    mRenderPaint.setColor(dataSet.getCircleColor(j / 2));

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    // make sure the circles don't do shitty things outside
                    // bounds
                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    c.drawCircle(x, y, dataSet.getCircleSize(),
                            mRenderPaint);
                    
                    if (dataSet.isDrawCircleHoleEnabled())
                        c.drawCircle(x, y,
                                dataSet.getCircleSize() / 2f,
                                mCirclePaintInner);
                }
            } // else do nothing
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        for (int i = 0; i < indices.length; i++) {

            LineDataSet set = mChart.getLineData().getDataSetByIndex(indices[i]
                    .getDataSetIndex());

            if (set == null)
                continue;

            mHighlightPaint.setColor(set.getHighLightColor());

            int xIndex = indices[i].getXIndex(); // get the
                                                 // x-position

            if (xIndex > mChart.getXChartMax() * mAnimator.getPhaseX())
                continue;

            float y = set.getYValForXIndex(xIndex) * mAnimator.getPhaseY(); // get
                                                                            // the
            // y-position

            float[] pts = new float[] {
                    xIndex, mChart.getYChartMax(), xIndex, mChart.getYChartMin(), 0, y,
                    mChart.getXChartMax(), y
            };

            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);
            // draw the highlight lines
            c.drawLines(pts, mHighlightPaint);
        }
    }
}
