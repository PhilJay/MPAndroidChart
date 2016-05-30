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
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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

        switch (dataSet.getMode()) {
            default:
            case LINEAR:
            case STEPPED:
                drawLinear(c, dataSet);
                break;

            case CUBIC_BEZIER:
                drawCubicBezier(c, dataSet);
                break;

            case HORIZONTAL_BEZIER:
                drawHorizontalBezier(c, dataSet);
                break;
        }

        mRenderPaint.setPathEffect(null);
    }

    /**
     * Draws a cubic line.
     *
     * @param c
     * @param dataSet
     */
    protected void drawHorizontalBezier(Canvas c, ILineDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        int entryCount = dataSet.getEntryCount();

        Entry entryFrom = dataSet.getEntryForXIndex((mMinX < 0) ? 0 : mMinX, DataSet.Rounding.DOWN);
        Entry entryTo = dataSet.getEntryForXIndex(mMaxX, DataSet.Rounding.UP);

        int diff = (entryFrom == entryTo) ? 1 : 0;
        int minx = Math.max(dataSet.getEntryIndex(entryFrom) - diff, 0);
        int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryIndex(entryTo) + 1), entryCount);

        float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
        float phaseY = mAnimator.getPhaseY();

        cubicPath.reset();

        int size = (int) Math.ceil((maxx - minx) * phaseX + minx);

        if (size - minx >= 2) {

            Entry prev = dataSet.getEntryForIndex(minx);
            Entry cur = prev;

            // let the spline start
            cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);

            for (int j = minx + 1, count = Math.min(size, entryCount); j < count; j++) {

                prev = dataSet.getEntryForIndex(j - 1);
                cur = dataSet.getEntryForIndex(j);

                final float cpx = (float)(prev.getX())
                        + (float)(cur.getX() - prev.getX()) / 2.0f;

                cubicPath.cubicTo(
                        cpx, prev.getY() * phaseY,
                        cpx, cur.getY() * phaseY,
                        cur.getX(), cur.getY() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);
            // create a new path, this is bad for performance
            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans,
                    minx, size);
        }

        mRenderPaint.setColor(dataSet.getColor());

        mRenderPaint.setStyle(Paint.Style.STROKE);

        trans.pathValueToPixel(cubicPath);

        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicBezier(Canvas c, ILineDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        int entryCount = dataSet.getEntryCount();

        Entry entryFrom = dataSet.getEntryForXIndex((mMinX < 0) ? 0 : mMinX, DataSet.Rounding.DOWN);
        Entry entryTo = dataSet.getEntryForXIndex(mMaxX, DataSet.Rounding.UP);

        int diff = (entryFrom == entryTo) ? 1 : 0;
        int minx = Math.max(dataSet.getEntryIndex(entryFrom) - diff - 1, 0);
        int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryIndex(entryTo) + 1), entryCount);

        float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
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
            cubicPath.moveTo(cur.getX(), cur.getY() * phaseY);

            for (int j = minx + 1, count = Math.min(size, entryCount); j < count; j++) {

                prevPrev = dataSet.getEntryForIndex(j == 1 ? 0 : j - 2);
                prev = dataSet.getEntryForIndex(j - 1);
                cur = dataSet.getEntryForIndex(j);
                next = entryCount > j + 1 ? dataSet.getEntryForIndex(j + 1) : cur;

                prevDx = (cur.getX() - prevPrev.getX()) * intensity;
                prevDy = (cur.getY() - prevPrev.getY()) * intensity;
                curDx = (next.getX() - prev.getX()) * intensity;
                curDy = (next.getY() - prev.getY()) * intensity;

                cubicPath.cubicTo(prev.getX() + prevDx, (prev.getY() + prevDy) * phaseY,
                        cur.getX() - curDx,
                        (cur.getY() - curDy) * phaseY, cur.getX(), cur.getY() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {

            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);
            // create a new path, this is bad for performance
            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans,
                    minx, size);
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
        final float xTo = toEntry == null ? 0 : toEntry.getX();
        final float xFrom = fromEntry == null ? 0 : fromEntry.getX();

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

        float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
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

                mLineBuffer[0] = e.getX();
                mLineBuffer[1] = e.getY() * phaseY;

                if (j + 1 < count) {

                    e = dataSet.getEntryForIndex(j + 1);

                    if (e == null) break;

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[2] = e.getX();
                        mLineBuffer[3] = mLineBuffer[1];
                        mLineBuffer[4] = mLineBuffer[2];
                        mLineBuffer[5] = mLineBuffer[3];
                        mLineBuffer[6] = e.getX();
                        mLineBuffer[7] = e.getY() * phaseY;
                    } else {
                        mLineBuffer[2] = e.getX();
                        mLineBuffer[3] = e.getY() * phaseY;
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

                int j = 0;
                for (int x = count > 1 ? minx + 1 : minx; x < count; x++) {

                    e1 = dataSet.getEntryForIndex(x == 0 ? 0 : (x - 1));
                    e2 = dataSet.getEntryForIndex(x);

                    if (e1 == null || e2 == null) continue;

                    mLineBuffer[j++] = e1.getX();
                    mLineBuffer[j++] = e1.getY() * phaseY;

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[j++] = e2.getX();
                        mLineBuffer[j++] = e1.getY() * phaseY;
                        mLineBuffer[j++] = e2.getX();
                        mLineBuffer[j++] = e1.getY() * phaseY;
                    }

                    mLineBuffer[j++] = e2.getX();
                    mLineBuffer[j++] = e2.getY() * phaseY;
                }

                if (j > 0) {
                    trans.pointValuesToPixel(mLineBuffer);

                    final int size =
                            Math.max((count - minx - 1) * pointsPerEntryPair, pointsPerEntryPair) *
                                    2;

                    mRenderPaint.setColor(dataSet.getColor());

                    canvas.drawLines(mLineBuffer, 0, size,
                            mRenderPaint);
                }
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
        float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
        float phaseY = mAnimator.getPhaseY();
        final boolean isDrawSteppedEnabled = dataSet.isDrawSteppedEnabled();

        Path filled = new Path();
        Entry entry = dataSet.getEntryForIndex(from);

        filled.moveTo(entry.getX(), fillMin);
        filled.lineTo(entry.getX(), entry.getY() * phaseY);

        // create a new path
        for (int x = from + 1, count = (int) Math.ceil((to - from) * phaseX + from); x < count; x++) {

            Entry e = dataSet.getEntryForIndex(x);

            if (isDrawSteppedEnabled) {
                final Entry ePrev = dataSet.getEntryForIndex(x - 1);
                if (ePrev == null) continue;

                filled.lineTo(e.getX(), ePrev.getY() * phaseY);
            }

            filled.lineTo(e.getX(), e.getY() * phaseY);
        }

        // close up
        filled.lineTo(
                dataSet.getEntryForIndex(
                        Math.max(
                                Math.min((int) Math.ceil((to - from) * phaseX + from) - 1,
                                        dataSet.getEntryCount() - 1), 0)).getX(), fillMin);

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
                if (dataSet.getMode() == LineDataSet.Mode.CUBIC_BEZIER)
                    diff += 1;

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

                    drawValue(c, dataSet.getValueFormatter(), entry.getY(), entry, i, x,
                            y - valOffset, dataSet.getValueTextColor(j / 2));
                }
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        drawCircles(c);
    }

    private Path mCirclePathBuffer = new Path();

    protected void drawCircles(Canvas c) {

        mRenderPaint.setStyle(Paint.Style.FILL);

        float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));
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
            if (dataSet.getMode() == LineDataSet.Mode.CUBIC_BEZIER)
                diff += 1;

            int minx = Math.max(dataSet.getEntryIndex(entryFrom) - diff, 0);
            int maxx = Math.min(Math.max(minx + 2, dataSet.getEntryIndex(entryTo) + 1), entryCount);

            float circleRadius = dataSet.getCircleRadius();
            float circleHoleRadius = dataSet.getCircleHoleRadius();
            boolean drawCircleHole = dataSet.isDrawCircleHoleEnabled() &&
                    circleHoleRadius < circleRadius &&
                    circleHoleRadius > 0.f;
            boolean drawTransparentCircleHole = drawCircleHole &&
                    dataSet.getCircleHoleColor() == ColorTemplate.COLOR_NONE;

            for (int j = minx,
                 count = (int) Math.ceil((maxx - minx) * phaseX + minx);
                 j < count;
                 j ++) {

                Entry e = dataSet.getEntryForIndex(j);

                if (e == null) break;

                circlesBuffer[0] = e.getX();
                circlesBuffer[1] = e.getY() * phaseY;

                trans.pointValuesToPixel(circlesBuffer);

                if (!mViewPortHandler.isInBoundsRight(circlesBuffer[0]))
                    break;

                // make sure the circles don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(circlesBuffer[0]) ||
                        !mViewPortHandler.isInBoundsY(circlesBuffer[1]))
                    continue;

                mRenderPaint.setColor(dataSet.getCircleColor(j));

                if (drawTransparentCircleHole) {

                    // Begin path for circle with hole
                    mCirclePathBuffer.reset();

                    mCirclePathBuffer.addCircle(circlesBuffer[0], circlesBuffer[1],
                            circleRadius,
                            Path.Direction.CW);

                    // Cut hole in path
                    mCirclePathBuffer.addCircle(circlesBuffer[0], circlesBuffer[1],
                            circleHoleRadius,
                            Path.Direction.CCW);

                    // Fill in-between
                    c.drawPath(mCirclePathBuffer, mRenderPaint);

                } else {

                    c.drawCircle(circlesBuffer[0], circlesBuffer[1],
                            circleRadius,
                            mRenderPaint);

                    if (drawCircleHole) {
                        c.drawCircle(circlesBuffer[0], circlesBuffer[1],
                                circleHoleRadius,
                                mCirclePaintInner);
                    }
                }
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        LineData lineData = mChart.getLineData();

        for (Highlight high : indices) {

            final int minDataSetIndex = high.getDataSetIndex() == -1
                    ? 0
                    : high.getDataSetIndex();
            final int maxDataSetIndex = high.getDataSetIndex() == -1
                    ? lineData.getDataSetCount()
                    : (high.getDataSetIndex() + 1);
            if (maxDataSetIndex - minDataSetIndex < 1) continue;

            for (int dataSetIndex = minDataSetIndex;
                 dataSetIndex < maxDataSetIndex;
                 dataSetIndex++) {

                ILineDataSet set = lineData.getDataSetByIndex(dataSetIndex);

                if (set == null || !set.isHighlightEnabled())
                    continue;

                int xIndex = high.getXIndex(); // get the
                // x-position

                if (xIndex > mChart.getXChartMax() * mAnimator.getPhaseX())
                    continue;

                final float yVal = set.getYValForXIndex(xIndex);
                if (Float.isNaN(yVal))
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
        if (mBitmapCanvas != null) {
            mBitmapCanvas.setBitmap(null);
            mBitmapCanvas = null;
        }
        if (mDrawBitmap != null) {
            mDrawBitmap.get().recycle();
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }
}
