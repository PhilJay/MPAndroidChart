package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.ref.WeakReference;
import java.util.List;

public class LineChartRenderer extends LineRadarRenderer {

    protected LineDataProvider mChart;

    /**
     * paint for the inner circle of the value indicators
     */
    protected Paint mCirclePaintInner;

    /**
     * Bitmap object used for drawing the paths (otherwise they are too long if
     * rendered directly on the canvas)
     */
    protected WeakReference<Bitmap> mDrawBitmap;

    /**
     * on this canvas, the paths are rendered, it is initialized with the
     * pathBitmap
     */
    protected Canvas mBitmapCanvas;

    /**
     * the bitmap configuration to be used
     */
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;

    protected Path cubicPath = new Path();
    protected Path cubicFillPath = new Path();

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

    }

    @Override
    public void drawData(Canvas c) {

        int width = (int) mViewPortHandler.getChartWidth();
        int height = (int) mViewPortHandler.getChartHeight();

        if (mDrawBitmap == null
                || (mDrawBitmap.get().getWidth() != width)
                || (mDrawBitmap.get().getHeight() != height)) {

            if (width > 0 && height > 0) {

                mDrawBitmap = new WeakReference<Bitmap>(Bitmap.createBitmap(width, height, mBitmapConfig));
                mBitmapCanvas = new Canvas(mDrawBitmap.get());
            } else
                return;
        }

        mDrawBitmap.get().eraseColor(Color.TRANSPARENT);

        LineData lineData = mChart.getLineData();

        for (ILineDataSet set : lineData.getDataSets()) {

            if (set.isVisible() && set.getEntryCount() > 0)
                drawDataSet(c, set);
        }

        c.drawBitmap(mDrawBitmap.get(), 0, 0, mRenderPaint);
    }

    protected void drawDataSet(Canvas c, ILineDataSet dataSet) {

        if (dataSet.getEntryCount() < 1)
            return;

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setPathEffect(dataSet.getDashPathEffect());

        // if drawing cubic lines is enabled
        if (dataSet.isDrawCubicEnabled()) {

            drawCubic(c, dataSet);

            // draw normal (straight) lines
        } else {
            drawLinear(c, dataSet);
        }

        mRenderPaint.setPathEffect(null);
    }

    /**
     * Draws a cubic line.
     *
     * @param c
     * @param dataSet
     */
    protected void drawCubic(Canvas c, ILineDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        int entryCount = dataSet.getEntryCount();

        Entry entryFrom = dataSet.getEntryForXIndex((mMinX < 0) ? 0 : mMinX, DataSet.Rounding.DOWN);
        Entry entryTo = dataSet.getEntryForXIndex(mMaxX, DataSet.Rounding.UP);

        int diff = (entryFrom == entryTo) ? 1 : 0;
        int minx = Math.max(dataSet.getEntryIndex(entryFrom) - diff, 0);
        int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryIndex(entryTo) + 1), entryCount);

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float intensity = dataSet.getCubicIntensity();

        cubicPath.reset();

        int size = (int) Math.ceil((maxx - minx) * phaseX + minx);

        if (size - minx >= 2) {

            float prevDx = 0f;
            float prevDy = 0f;
            float curDx = 0f;
            float curDy = 0f;

            Entry prevPrev = dataSet.getEntryForIndex(minx);
            Entry prev = prevPrev;
            Entry cur = prev;
            Entry next = dataSet.getEntryForIndex(minx + 1);

            // let the spline start
            cubicPath.moveTo(cur.getXIndex(), cur.getVal() * phaseY);

            for (int j = minx + 1, count = Math.min(size, entryCount - 1); j < count; j++) {

                prevPrev = dataSet.getEntryForIndex(j == 1 ? 0 : j - 2);
                prev = dataSet.getEntryForIndex(j - 1);
                cur = dataSet.getEntryForIndex(j);
                next = dataSet.getEntryForIndex(j + 1);

                prevDx = (cur.getXIndex() - prevPrev.getXIndex()) * intensity;
                prevDy = (cur.getVal() - prevPrev.getVal()) * intensity;
                curDx = (next.getXIndex() - prev.getXIndex()) * intensity;
                curDy = (next.getVal() - prev.getVal()) * intensity;

                cubicPath.cubicTo(prev.getXIndex() + prevDx, (prev.getVal() + prevDy) * phaseY,
                        cur.getXIndex() - curDx,
                        (cur.getVal() - curDy) * phaseY, cur.getXIndex(), cur.getVal() * phaseY);
            }

            if (size > entryCount - 1) {

                prevPrev = dataSet.getEntryForIndex((entryCount >= 3) ? entryCount - 3
                        : entryCount - 2);
                prev = dataSet.getEntryForIndex(entryCount - 2);
                cur = dataSet.getEntryForIndex(entryCount - 1);
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
            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans,
                    minx, maxx);
        }

        mRenderPaint.setColor(dataSet.getColor());

        mRenderPaint.setStyle(Paint.Style.STROKE);

        trans.pathValueToPixel(cubicPath);

        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicFill(Canvas c, ILineDataSet dataSet, Path spline, Transformer trans,
                                 int from, int to) {

        if (to - from <= 1)
            return;

        float fillMin = dataSet.getFillFormatter()
                .getFillLinePosition(dataSet, mChart);
        
        // Take the from/to xIndex from the entries themselves,
        // so missing entries won't screw up the filling.
        // What we need to draw is line from points of the xIndexes - not arbitrary entry indexes!

        final Entry toEntry = dataSet.getEntryForIndex(to - 1);
        final Entry fromEntry = dataSet.getEntryForIndex(from);
        final float xTo = toEntry == null ? 0 : toEntry.getXIndex();
        final float xFrom = fromEntry == null ? 0 : fromEntry.getXIndex();

        spline.lineTo(xTo, fillMin);
        spline.lineTo(xFrom, fillMin);
        spline.close();

        trans.pathValueToPixel(spline);

        final Drawable drawable = dataSet.getFillDrawable();
        if (drawable != null) {

            drawFilledPath(c, spline, drawable);
        } else {

            drawFilledPath(c, spline, dataSet.getFillColor(), dataSet.getFillAlpha());
        }
    }

    private float[] mLineBuffer = new float[4];

    /**
     * Draws a normal line.
     *
     * @param c
     * @param dataSet
     */
    protected void drawLinear(Canvas c, ILineDataSet dataSet) {

        int entryCount = dataSet.getEntryCount();

        final boolean isDrawSteppedEnabled = dataSet.isDrawSteppedEnabled();
        final int pointsPerEntryPair = isDrawSteppedEnabled ? 4 : 2;

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        mRenderPaint.setStyle(Paint.Style.STROKE);

        Canvas canvas = null;

        // if the data-set is dashed, draw on bitmap-canvas
        if (dataSet.isDashedLineEnabled()) {
            canvas = mBitmapCanvas;
        } else {
            canvas = c;
        }

        Entry entryFrom = dataSet.getEntryForXIndex((mMinX < 0) ? 0 : mMinX, DataSet.Rounding.DOWN);
        Entry entryTo = dataSet.getEntryForXIndex(mMaxX, DataSet.Rounding.UP);

        int diff = (entryFrom == entryTo) ? 1 : 0;
        int minx = Math.max(dataSet.getEntryIndex(entryFrom) - diff, 0);
        int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryIndex(entryTo) + 1), entryCount);

        final int count = (int)(Math.ceil((float)(maxx - minx) * phaseX + (float)(minx)));

        // more than 1 color
        if (dataSet.getColors().size() > 1) {

            if (mLineBuffer.length != pointsPerEntryPair * 2)
                mLineBuffer = new float[pointsPerEntryPair * 2];

            for (int j = minx;
                 j < count;
                 j++) {

                if (count > 1 && j == count - 1) {
                    // Last point, we have already drawn a line to this point
                    break;
                }

                Entry e = dataSet.getEntryForIndex(j);
                if (e == null) continue;

                mLineBuffer[0] = e.getXIndex();
                mLineBuffer[1] = e.getVal() * phaseY;

                if (j + 1 < count) {

                    e = dataSet.getEntryForIndex(j + 1);

                    if (e == null) break;

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[2] = e.getXIndex();
                        mLineBuffer[3] = mLineBuffer[1];
                        mLineBuffer[4] = mLineBuffer[2];
                        mLineBuffer[5] = mLineBuffer[3];
                        mLineBuffer[6] = e.getXIndex();
                        mLineBuffer[7] = e.getVal() * phaseY;
                    } else {
                        mLineBuffer[2] = e.getXIndex();
                        mLineBuffer[3] = e.getVal() * phaseY;
                    }

                } else {
                    mLineBuffer[2] = mLineBuffer[0];
                    mLineBuffer[3] = mLineBuffer[1];
                }

                trans.pointValuesToPixel(mLineBuffer);

                if (!mViewPortHandler.isInBoundsRight(mLineBuffer[0]))
                    break;

                // make sure the lines don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(mLineBuffer[2])
                        || (!mViewPortHandler.isInBoundsTop(mLineBuffer[1]) && !mViewPortHandler
                        .isInBoundsBottom(mLineBuffer[3]))
                        || (!mViewPortHandler.isInBoundsTop(mLineBuffer[1]) && !mViewPortHandler
                        .isInBoundsBottom(mLineBuffer[3])))
                    continue;

                // get the color that is set for this line-segment
                mRenderPaint.setColor(dataSet.getColor(j));

                canvas.drawLines(mLineBuffer, 0, pointsPerEntryPair * 2, mRenderPaint);
            }

        } else { // only one color per dataset

            if (mLineBuffer.length != Math.max((entryCount - 1) * pointsPerEntryPair, pointsPerEntryPair) * 2)
                mLineBuffer = new float[Math.max((entryCount - 1) * pointsPerEntryPair, pointsPerEntryPair) * 2];

            Entry e1, e2;

            e1 = dataSet.getEntryForIndex(minx);

            if (e1 != null) {

                for (int x = count > 1 ? minx + 1 : minx, j = 0; x < count; x++) {

                    e1 = dataSet.getEntryForIndex(x == 0 ? 0 : (x - 1));
                    e2 = dataSet.getEntryForIndex(x);

                    if (e1 == null || e2 == null) continue;

                    mLineBuffer[j++] = e1.getXIndex();
                    mLineBuffer[j++] = e1.getVal() * phaseY;

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[j++] = e2.getXIndex();
                        mLineBuffer[j++] = e1.getVal() * phaseY;
                        mLineBuffer[j++] = e2.getXIndex();
                        mLineBuffer[j++] = e1.getVal() * phaseY;
                    }

                    mLineBuffer[j++] = e2.getXIndex();
                    mLineBuffer[j++] = e2.getVal() * phaseY;
                }

                trans.pointValuesToPixel(mLineBuffer);

                final int size = Math.max((count - minx - 1) * pointsPerEntryPair, pointsPerEntryPair) * 2;

                mRenderPaint.setColor(dataSet.getColor());

                canvas.drawLines(mLineBuffer, 0, size,
                        mRenderPaint);
            }
        }

        mRenderPaint.setPathEffect(null);

        // if drawing filled is enabled
        if (dataSet.isDrawFilledEnabled() && entryCount > 0) {
            drawLinearFill(c, dataSet, minx, maxx, trans);
        }
    }

    protected void drawLinearFill(Canvas c, ILineDataSet dataSet, int minx,
                                  int maxx,
                                  Transformer trans) {

        Path filled = generateFilledPath(
                dataSet, minx, maxx);

        trans.pathValueToPixel(filled);

        final Drawable drawable = dataSet.getFillDrawable();
        if (drawable != null) {

            drawFilledPath(c, filled, drawable);
        } else {

            drawFilledPath(c, filled, dataSet.getFillColor(), dataSet.getFillAlpha());
        }
    }

    /**
     * Generates the path that is used for filled drawing.
     *
     * @param dataSet
     * @return
     */
    private Path generateFilledPath(ILineDataSet dataSet, int from, int to) {

        float fillMin = dataSet.getFillFormatter().getFillLinePosition(dataSet, mChart);
        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();
        final boolean isDrawSteppedEnabled = dataSet.isDrawSteppedEnabled();

        Path filled = new Path();
        Entry entry = dataSet.getEntryForIndex(from);

        filled.moveTo(entry.getXIndex(), fillMin);
        filled.lineTo(entry.getXIndex(), entry.getVal() * phaseY);

        // create a new path
        for (int x = from + 1, count = (int) Math.ceil((to - from) * phaseX + from); x < count; x++) {

            Entry e = dataSet.getEntryForIndex(x);

            if (isDrawSteppedEnabled) {
                final Entry ePrev = dataSet.getEntryForIndex(x - 1);
                if (ePrev == null) continue;

                filled.lineTo(e.getXIndex(), ePrev.getVal() * phaseY);
            }

            filled.lineTo(e.getXIndex(), e.getVal() * phaseY);
        }

        // close up
        filled.lineTo(
                dataSet.getEntryForIndex(
                        Math.max(
                                Math.min((int) Math.ceil((to - from) * phaseX + from) - 1,
                                        dataSet.getEntryCount() - 1), 0)).getXIndex(), fillMin);

        filled.close();

        return filled;
    }

    @Override
    public void drawValues(Canvas c) {

        if (mChart.getLineData().getYValCount() < mChart.getMaxVisibleCount()
                * mViewPortHandler.getScaleX()) {

            List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();

            for (int i = 0; i < dataSets.size(); i++) {

                ILineDataSet dataSet = dataSets.get(i);

                if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                // make sure the values do not interfear with the circles
                int valOffset = (int) (dataSet.getCircleRadius() * 1.75f);

                if (!dataSet.isDrawCirclesEnabled())
                    valOffset = valOffset / 2;

                int entryCount = dataSet.getEntryCount();

                Entry entryFrom = dataSet.getEntryForXIndex((mMinX < 0) ? 0 : mMinX,
                        DataSet.Rounding.DOWN);
                Entry entryTo = dataSet.getEntryForXIndex(mMaxX, DataSet.Rounding.UP);

                int diff = (entryFrom == entryTo) ? 1 : 0;
                int minx = Math.max(dataSet.getEntryIndex(entryFrom) - diff, 0);
                int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryIndex(entryTo) + 1), entryCount);

                float[] positions = trans.generateTransformedValuesLine(
                        dataSet, mAnimator.getPhaseX(), mAnimator.getPhaseY(), minx, maxx);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    Entry entry = dataSet.getEntryForIndex(j / 2 + minx);

                    drawValue(c, dataSet.getValueFormatter(), entry.getVal(), entry, i, x,
                            y - valOffset, dataSet.getValueTextColor(j / 2));
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

        float[] circlesBuffer = new float[2];

        List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();

        for (int i = 0; i < dataSets.size(); i++) {

            ILineDataSet dataSet = dataSets.get(i);

            if (!dataSet.isVisible() || !dataSet.isDrawCirclesEnabled() ||
                    dataSet.getEntryCount() == 0)
                continue;

            mCirclePaintInner.setColor(dataSet.getCircleHoleColor());

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            int entryCount = dataSet.getEntryCount();

            Entry entryFrom = dataSet.getEntryForXIndex((mMinX < 0) ? 0 : mMinX,
                    DataSet.Rounding.DOWN);
            Entry entryTo = dataSet.getEntryForXIndex(mMaxX, DataSet.Rounding.UP);

            int diff = (entryFrom == entryTo) ? 1 : 0;
            int minx = Math.max(dataSet.getEntryIndex(entryFrom) - diff, 0);
            int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryIndex(entryTo) + 1), entryCount);

            float halfsize = dataSet.getCircleRadius() / 2f;

            for (int j = minx,
                 count = (int) Math.ceil((maxx - minx) * phaseX + minx);
                 j < count;
                 j ++) {

                Entry e = dataSet.getEntryForIndex(j);

                if (e == null) break;

                circlesBuffer[0] = e.getXIndex();
                circlesBuffer[1] = e.getVal() * phaseY;

                trans.pointValuesToPixel(circlesBuffer);

                if (!mViewPortHandler.isInBoundsRight(circlesBuffer[0]))
                    break;

                // make sure the circles don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(circlesBuffer[0]) ||
                        !mViewPortHandler.isInBoundsY(circlesBuffer[1]))
                    continue;

                int circleColor = dataSet.getCircleColor(j);

                mRenderPaint.setColor(circleColor);

                c.drawCircle(circlesBuffer[0], circlesBuffer[1], dataSet.getCircleRadius(),
                        mRenderPaint);

                if (dataSet.isDrawCircleHoleEnabled()
                        && circleColor != mCirclePaintInner.getColor())
                    c.drawCircle(circlesBuffer[0], circlesBuffer[1],
                            halfsize,
                            mCirclePaintInner);
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        for (int i = 0; i < indices.length; i++) {

            ILineDataSet set = mChart.getLineData().getDataSetByIndex(indices[i]
                    .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            int xIndex = indices[i].getXIndex(); // get the
            // x-position

            if (xIndex > mChart.getXChartMax() * mAnimator.getPhaseX())
                continue;

            final float yVal = set.getYValForXIndex(xIndex);
            if (yVal == Float.NaN)
                continue;

            float y = yVal * mAnimator.getPhaseY(); // get
            // the
            // y-position

            float[] pts = new float[]{
                    xIndex, y
            };

            mChart.getTransformer(set.getAxisDependency()).pointValuesToPixel(pts);

            // draw the lines
            drawHighlightLines(c, pts, set);
        }
    }

    /**
     * Sets the Bitmap.Config to be used by this renderer.
     * Default: Bitmap.Config.ARGB_8888
     * Use Bitmap.Config.ARGB_4444 to consume less memory.
     *
     * @param config
     */
    public void setBitmapConfig(Bitmap.Config config) {
        mBitmapConfig = config;
        releaseBitmap();
    }

    /**
     * Returns the Bitmap.Config that is used by this renderer.
     *
     * @return
     */
    public Bitmap.Config getBitmapConfig() {
        return mBitmapConfig;
    }

    /**
     * Releases the drawing bitmap. This should be called when {@link LineChart#onDetachedFromWindow()}.
     */
    public void releaseBitmap() {
        if (mDrawBitmap != null) {
            mDrawBitmap.get().recycle();
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }
}
