
package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.CircleBuffer;
import com.github.mikephil.charting.buffer.LineBuffer;
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

    /**
     * Bitmap object used for drawing the paths (otherwise they are too long if
     * rendered directly on the canvas)
     */
    protected Bitmap mPathBitmap;

    /**
     * on this canvas, the paths are rendered, it is initialized with the
     * pathBitmap
     */
    protected Canvas mPathCanvas;
    
    protected Path cubicPath = new Path();
    protected Path cubicFillPath = new Path();

    protected LineBuffer[] mLineBuffers;

    protected CircleBuffer[] mCircleBuffers;

    public LineChartRenderer(LineDataProvider chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);
    }

    @Override
    public void initBuffers() {

        LineData lineData = mChart.getLineData();
        mLineBuffers = new LineBuffer[lineData.getDataSetCount()];

        for (int i = 0; i < mLineBuffers.length; i++) {
            LineDataSet set = lineData.getDataSetByIndex(i);
            int size = lineData.getXValCount() * 4 - 4;
            if (size < 4)
                size = 4;
            mLineBuffers[i] = new LineBuffer(size);
        }

        mCircleBuffers = new CircleBuffer[lineData.getDataSetCount()];

        for (int i = 0; i < mCircleBuffers.length; i++) {
            LineDataSet set = lineData.getDataSetByIndex(i);
            mCircleBuffers[i] = new CircleBuffer(set.getEntryCount() * 2);
        }
    }

    /**
     * Returns the correct buffer size depending on the DataSet setup.
     * 
     * @param set
     * @return
     */
    private int getBufferSize(LineDataSet set, int xValCount) {
        if (set.isDrawFilledEnabled()) {
            if (set.isDrawCubicEnabled()) {
                return 0;
            } else {
                return xValCount * 4 - 4;
            }
        } else {
            if (set.isDrawCubicEnabled()) {
                return 0;
            } else {
                return xValCount * 4 - 4;
            }
        }
    }

    @Override
    public void drawData(Canvas c) {

        if (mPathBitmap == null) {
            mPathBitmap = Bitmap.createBitmap((int) mViewPortHandler.getChartWidth(),
                    (int) mViewPortHandler.getChartHeight(), Bitmap.Config.ARGB_4444);
            mPathCanvas = new Canvas(mPathBitmap);
        }

        mPathBitmap.eraseColor(Color.TRANSPARENT);

        LineData lineData = mChart.getLineData();

        for (LineDataSet set : lineData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, LineDataSet dataSet) {

        ArrayList<Entry> entries = dataSet.getYVals();

        if (entries.size() < 1)
            return;

        calcXBounds(mChart.getTransformer(dataSet.getAxisDependency()));

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

        int minx = mMinX;
        int maxx = mMaxX + 2;

        if (maxx > entries.size())
            maxx = entries.size();

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float intensity = dataSet.getCubicIntensity();

        long start = System.currentTimeMillis();
        
        cubicPath.reset();

        float size = entries.size() * phaseX;

        if (entries.size() > 2) {

            float prevDx = 0f;
            float prevDy = 0f;
            float curDx = 0f;
            float curDy = 0f;

            Entry cur = entries.get(0);
            Entry next = entries.get(1);
            Entry prev = entries.get(0);
            Entry prevPrev = entries.get(0);

            // let the spline start
            cubicPath.moveTo(cur.getXIndex(), cur.getVal() * phaseY);

            prevDx = (next.getXIndex() - cur.getXIndex()) * intensity;
            prevDy = (next.getVal() - cur.getVal()) * intensity;

            cur = entries.get(1);
            next = entries.get(2);
            curDx = (next.getXIndex() - prev.getXIndex()) * intensity;
            curDy = (next.getVal() - prev.getVal()) * intensity;

            // the first cubic
            cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY,
                    cur.getXIndex() - curDx,
                    (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);

            for (int j = 2; j < size - 1; j++) {

                prevPrev = entries.get(j - 2);
                prev = entries.get(j - 1);
                cur = entries.get(j);
                next = entries.get(j + 1);

                prevDx = (cur.getXIndex() - prevPrev.getXIndex()) * intensity;
                prevDy = (cur.getVal() - prevPrev.getVal()) * intensity;
                curDx = (next.getXIndex() - prev.getXIndex()) * intensity;
                curDy = (next.getVal() - prev.getVal()) * intensity;

                cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY,
                        cur.getXIndex() - curDx,
                        (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);
            }

            if (size > entries.size() - 1) {

                cur = entries.get(entries.size() - 1);
                prev = entries.get(entries.size() - 2);
                prevPrev = entries.get(entries.size() - 3);
                next = cur;

                prevDx = (cur.getXIndex() - prevPrev.getXIndex()) * intensity;
                prevDy = (cur.getVal() - prevPrev.getVal()) * intensity;
                curDx = (next.getXIndex() - prev.getXIndex()) * intensity;
                curDy = (next.getVal() - prev.getVal()) * intensity;

                // the last cubic
                cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY,
                        cur.getXIndex() - curDx,
                        (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);
            // create a new path, this is bad for performance
            drawCubicFill(mPathCanvas, dataSet, cubicFillPath, trans, minx, maxx);
        }

        Log.i("", "perpare: " + (System.currentTimeMillis() - start));

        mRenderPaint.setColor(dataSet.getColor());

        mRenderPaint.setStyle(Paint.Style.STROKE);

        start = System.currentTimeMillis();
        trans.pathValueToPixel(cubicPath);

        Log.i("", "transform: " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();

        mPathCanvas.drawPath(cubicPath, mRenderPaint);
        Log.i("", "draw: " + (System.currentTimeMillis() - start));
        c.drawBitmap(mPathBitmap, 0, 0, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicFill(Canvas c, LineDataSet dataSet, Path spline, Transformer trans,
            int from, int to) {

        float fillMin = mChart.getFillFormatter()
                .getFillLinePosition(dataSet, mChart.getLineData(), mChart.getYChartMax(),
                        mChart.getYChartMin());

        Entry entryFrom = dataSet.getEntryForXIndex(mMinX);
        Entry entryTo = dataSet.getEntryForXIndex(mMaxX);

        spline.lineTo(entryTo.getXIndex(), fillMin);
        spline.lineTo(entryFrom.getXIndex(), fillMin);
        spline.close();

        mRenderPaint.setStyle(Paint.Style.FILL);

        mRenderPaint.setColor(dataSet.getFillColor());
        // filled is drawn with less alpha
        mRenderPaint.setAlpha(dataSet.getFillAlpha());

        trans.pathValueToPixel(spline);
        mPathCanvas.drawPath(spline, mRenderPaint);

        mRenderPaint.setAlpha(255);
    }

    protected void drawLinear(Canvas c, LineDataSet dataSet, ArrayList<Entry> entries) {

        int dataSetIndex = mChart.getLineData().getIndexOfDataSet(dataSet);

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        mRenderPaint.setStyle(Paint.Style.STROKE);

        LineBuffer buffer = mLineBuffers[dataSetIndex];
        buffer.setPhases(phaseX, phaseY);
        buffer.feed(entries);

        trans.pointValuesToPixel(buffer.buffer);

        // more than 1 color
        if (dataSet.getColors().size() > 1) {

            for (int j = 0; j < buffer.size() - 3; j += 2) {

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                // make sure the lines don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])
                        || (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1]) && !mViewPortHandler
                                .isInBoundsBottom(buffer.buffer[j + 3]))
                        || (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1]) && !mViewPortHandler
                                .isInBoundsBottom(buffer.buffer[j + 3])))
                    continue;

                // get the color that is set for this line-segment
                mRenderPaint.setColor(dataSet.getColor(j / 2));

                c.drawLine(buffer.buffer[j], buffer.buffer[j + 1],
                        buffer.buffer[j + 2], buffer.buffer[j + 3], mRenderPaint);
            }

        } else { // only one color per dataset

            int from = mMinX * 4;
            int range = (mMaxX * 4 - mMinX) + 4;
            int to = range + from;

            mRenderPaint.setColor(dataSet.getColor());

            c.drawLines(buffer.buffer, from, to >= buffer.size() ? buffer.size() - from : range,
                    mRenderPaint);
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

        Path filled = generateFilledPath(
                entries,
                mChart.getFillFormatter().getFillLinePosition(dataSet, mChart.getLineData(),
                        mChart.getYChartMax(), mChart.getYChartMin()), mMinX, mMaxX + 1);

        trans.pathValueToPixel(filled);

        c.drawPath(filled, mRenderPaint);

        // restore alpha
        mRenderPaint.setAlpha(255);
    }

    /**
     * Generates the path that is used for filled drawing.
     * 
     * @param entries
     * @return
     */
    private Path generateFilledPath(ArrayList<Entry> entries, float fillMin, int from, int to) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        Path filled = new Path();
        filled.moveTo(entries.get(from).getXIndex(), entries.get(from).getVal() * phaseY);

        if (to >= entries.size())
            to = entries.size() - 1;

        // create a new path
        for (int x = from + 1; x <= to * phaseX; x++) {

            Entry e = entries.get(x);
            filled.lineTo(e.getXIndex(), e.getVal() * phaseY);
        }

        // close up
        filled.lineTo(entries.get((int) (to * phaseX)).getXIndex(), fillMin);
        filled.lineTo(entries.get(from).getXIndex(), fillMin);
        filled.close();

        return filled;
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

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        ArrayList<LineDataSet> dataSets = mChart.getLineData().getDataSets();

        for (int i = 0; i < dataSets.size(); i++) {

            LineDataSet dataSet = dataSets.get(i);

            if (!dataSet.isVisible() || !dataSet.isDrawCirclesEnabled())
                continue;

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            ArrayList<Entry> entries = dataSet.getYVals();

            CircleBuffer buffer = mCircleBuffers[i];
            buffer.setPhases(phaseX, phaseY);
            buffer.feed(entries);

            trans.pointValuesToPixel(buffer.buffer);

            float halfsize = dataSet.getCircleSize() / 2f;

            for (int j = 0; j < buffer.size(); j += 2) {

                mRenderPaint.setColor(dataSet.getCircleColor(j / 2));

                float x = buffer.buffer[j];
                float y = buffer.buffer[j + 1];

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
                            halfsize,
                            mCirclePaintInner);
            }
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
