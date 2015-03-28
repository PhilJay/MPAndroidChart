
package com.github.mikephil.charting.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarLineScatterCandleData;
import com.github.mikephil.charting.data.BarLineScatterCandleDataSet;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.OnDrawListener;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.FillFormatter;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Base-class of LineChart, BarChart, ScatterChart and CandleStickChart.
 * 
 * @author Philipp Jahoda
 */
@SuppressLint("RtlHardcoded")
public abstract class BarLineChartBase<T extends BarLineScatterCandleData<? extends BarLineScatterCandleDataSet<? extends Entry>>>
        extends Chart<T> {

    /** the maximum number of entried to which values will be drawn */
    protected int mMaxVisibleCount = 100;

    /**
     * flag that indicates if pinch-zoom is enabled. if true, both x and y axis
     * can be scaled with 2 fingers, if false, x and y axis can be scaled
     * separately
     */
    protected boolean mPinchZoomEnabled = false;

    /** flat that indicates if double tap zoom is enabled or not */
    protected boolean mDoubleTapToZoomEnabled = true;

    /** if true, dragging is enabled for the chart */
    private boolean mDragEnabled = true;

    private boolean mScaleXEnabled = true;
    private boolean mScaleYEnabled = true;

    /** if true, data filtering is enabled */
    protected boolean mFilterData = false;

    /** paint object for the (by default) lightgrey background of the grid */
    protected Paint mGridBackgroundPaint;

    protected Paint mBorderPaint;

    /**
     * if set to true, the highlight indicator (lines for linechart, dark bar
     * for barchart) will be drawn upon selecting values.
     */
    protected boolean mHighLightIndicatorEnabled = true;

    /** flag indicating if the grid background should be drawn or not */
    protected boolean mDrawGridBackground = true;

    protected boolean mDrawBorders = false;

    /** the listener for user drawing on the chart */
    protected OnDrawListener mDrawListener;

    /**
     * the object representing the labels on the y-axis, this object is prepared
     * in the pepareYLabels() method
     */
    protected YAxis mAxisLeft;
    protected YAxis mAxisRight;

    /** the object representing the labels on the x-axis */
    protected XAxis mXAxis;

    protected YAxisRenderer mAxisRendererLeft;
    protected YAxisRenderer mAxisRendererRight;

    protected Transformer mLeftAxisTransformer;
    protected Transformer mRightAxisTransformer;

    protected XAxisRenderer mXAxisRenderer;

    // /** the approximator object used for data filtering */
    // private Approximator mApproximator;

    /** Paint object for drawing marker text on the YAxis **/
    protected final Paint mAxisTextPaint = new Paint();

    /** Rect object for calculating marker text bounds **/
    protected final Rect mAxisTextBoundsRect = new Rect();

    /**
     * If set to true, chart continues to scroll after touch up, but with friction, and its speed
     * decrease over time, depend on mFlingFriction value
     */
    private boolean mFlingFrictionEnabled;

    /**
     * Fling friction coefficient in [0 ; 1] interval, higher values indicate that speed will
     * decrease slowly, for example if it set to 0, it will stop immediately, if set to 1, it will
     * scroll with constant speed, until the last point
     */
    private float mFlingFrictionCoef = 0.9f;

    public BarLineChartBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BarLineChartBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarLineChartBase(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();

        mAxisLeft = new YAxis(AxisDependency.LEFT);
        mAxisRight = new YAxis(AxisDependency.RIGHT);

        mXAxis = new XAxis();

        mLeftAxisTransformer = new Transformer(mViewPortHandler);
        mRightAxisTransformer = new Transformer(mViewPortHandler);

        mAxisRendererLeft = new YAxisRenderer(mViewPortHandler, mAxisLeft, mLeftAxisTransformer);
        mAxisRendererRight = new YAxisRenderer(mViewPortHandler, mAxisRight, mRightAxisTransformer);

        mXAxisRenderer = new XAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);

        mListener = new BarLineChartTouchListener<BarLineChartBase<? extends BarLineScatterCandleData<? extends BarLineScatterCandleDataSet<? extends Entry>>>>(
                this, mViewPortHandler.getMatrixTouch());

        mGridBackgroundPaint = new Paint();
        mGridBackgroundPaint.setStyle(Style.FILL);
        // mGridBackgroundPaint.setColor(Color.WHITE);
        mGridBackgroundPaint.setColor(Color.rgb(240, 240, 240)); // light
        // grey

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Style.STROKE);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setStrokeWidth(Utils.convertDpToPixel(1f));
    }

    // for performance tracking
    private long totalTime = 0;
    private long drawCycles = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataNotSet)
            return;

        long starttime = System.currentTimeMillis();

        // // if data filtering is enabled
        // if (mFilterData) {
        // mData = getFilteredData();
        //
        // Log.i(LOG_TAG, "FilterTime: " + (System.currentTimeMillis() -
        // starttime) + " ms");
        // starttime = System.currentTimeMillis();
        // } else {
        // mData = getData();
        // // Log.i(LOG_TAG, "Filtering disabled.");
        // }

        if (mXAxis.isAdjustXLabelsEnabled())
            calcModulus();

        // execute all drawing commands
        drawGridBackground(canvas);

        if (mAxisLeft.isEnabled())
            mAxisRendererLeft.computeAxis(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisMaximum);
        if (mAxisRight.isEnabled())
            mAxisRendererRight.computeAxis(mAxisRight.mAxisMinimum, mAxisRight.mAxisMaximum);

        mXAxisRenderer.renderAxisLine(canvas);
        mAxisRendererLeft.renderAxisLine(canvas);
        mAxisRendererRight.renderAxisLine(canvas);

        // make sure the graph values and grid cannot be drawn outside the
        // content-rect
        int clipRestoreCount = canvas.save();
        canvas.clipRect(mViewPortHandler.getContentRect());

        mXAxisRenderer.renderGridLines(canvas);
        mAxisRendererLeft.renderGridLines(canvas);
        mAxisRendererRight.renderGridLines(canvas);

        mRenderer.drawData(canvas);

        mAxisRendererLeft.renderLimitLines(canvas);
        mAxisRendererRight.renderLimitLines(canvas);

        // if highlighting is enabled
        if (mHighlightEnabled && mHighLightIndicatorEnabled && valuesToHighlight())
            mRenderer.drawHighlighted(canvas, mIndicesToHightlight);

        // Removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);

        mRenderer.drawExtras(canvas);

        mXAxisRenderer.renderAxisLabels(canvas);
        mAxisRendererLeft.renderAxisLabels(canvas);
        mAxisRendererRight.renderAxisLabels(canvas);

        mRenderer.drawValues(canvas);

        mLegendRenderer.renderLegend(canvas);
        // drawLegend();

        drawMarkers(canvas);

        drawDescription(canvas);

        // canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);

        if (true) {
            long drawtime = (System.currentTimeMillis() - starttime);
            totalTime += drawtime;
            drawCycles += 1;
            long average = totalTime / drawCycles;
            Log.i(LOG_TAG, "Drawtime: " + drawtime + " ms, average: " + average + " ms, cycles: "
                    + drawCycles);
        }
    }

    /**
     * RESET PERFORMANCE TRACKING FIELDS
     */
    public void resetTracking() {
        totalTime = 0;
        drawCycles = 0;
    }

    protected void prepareValuePxMatrix() {

        if (mLogEnabled)
            Log.i(LOG_TAG, "Preparing Value-Px Matrix, xmin: " + mXChartMin + ", xmax: "
                    + mXChartMax + ", xdelta: " + mDeltaX);

        mRightAxisTransformer.prepareMatrixValuePx(mXChartMin, mDeltaX, mAxisRight.mAxisRange,
                mAxisRight.mAxisMinimum);
        mLeftAxisTransformer.prepareMatrixValuePx(mXChartMin, mDeltaX, mAxisLeft.mAxisRange,
                mAxisLeft.mAxisMinimum);
    }

    protected void prepareOffsetMatrix() {

        mRightAxisTransformer.prepareMatrixOffset(mAxisRight.isInverted());
        mLeftAxisTransformer.prepareMatrixOffset(mAxisLeft.isInverted());
    }

    @Override
    public void notifyDataSetChanged() {

        if (mDataNotSet) {
            if (mLogEnabled)
                Log.i(LOG_TAG, "Preparing... DATA NOT SET.");
            return;
        } else {
            if (mLogEnabled)
                Log.i(LOG_TAG, "Preparing...");
        }

        if (mRenderer != null)
            mRenderer.initBuffers();

        calcMinMax();

        if (mAxisLeft.needsDefaultFormatter())
            mAxisLeft.setValueFormatter(mDefaultFormatter);
        if (mAxisRight.needsDefaultFormatter())
            mAxisRight.setValueFormatter(mDefaultFormatter);

        mAxisRendererLeft.computeAxis(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisMaximum);
        mAxisRendererRight.computeAxis(mAxisRight.mAxisMinimum, mAxisRight.mAxisMaximum);

        mXAxisRenderer.computeAxis(mData.getXValAverageLength(), mData.getXVals());

        mLegendRenderer.computeLegend(mData);

        calculateOffsets();
    }

    @Override
    protected void calcMinMax() {

        float minLeft = mData.getYMin(AxisDependency.LEFT);
        float maxLeft = mData.getYMax(AxisDependency.LEFT);
        float minRight = mData.getYMin(AxisDependency.RIGHT);
        float maxRight = mData.getYMax(AxisDependency.RIGHT);

        float leftRange = Math.abs(maxLeft - (mAxisLeft.isStartAtZeroEnabled() ? 0 : minLeft));
        float rightRange = Math.abs(maxRight - (mAxisRight.isStartAtZeroEnabled() ? 0 : minRight));
        
        // in case all values are equal
        if (leftRange == 0f)
            maxLeft = maxLeft + 1f;

        if (rightRange == 0f)
            maxRight = maxRight + 1f;

        float topSpaceLeft = leftRange / 100f * mAxisLeft.getSpaceTop();
        float topSpaceRight = rightRange / 100f * mAxisRight.getSpaceTop();
        float bottomSpaceLeft = leftRange / 100f * mAxisLeft.getSpaceBottom();
        float bottomSpaceRight = rightRange / 100f * mAxisRight.getSpaceBottom();

        mXChartMax = mData.getXVals().size() - 1;
        mDeltaX = Math.abs(mXChartMax - mXChartMin);

        mAxisLeft.mAxisMaximum = !Float.isNaN(mAxisLeft.getAxisMaxValue()) ? mAxisLeft
                .getAxisMaxValue() : maxLeft + topSpaceLeft;
        mAxisRight.mAxisMaximum = !Float.isNaN(mAxisRight.getAxisMaxValue()) ? mAxisRight
                .getAxisMaxValue() : maxRight + topSpaceRight;
        mAxisLeft.mAxisMinimum = !Float.isNaN(mAxisLeft.getAxisMinValue()) ? mAxisLeft
                .getAxisMinValue() : minLeft - bottomSpaceLeft;
        mAxisRight.mAxisMinimum = !Float.isNaN(mAxisRight.getAxisMinValue()) ? mAxisRight
                .getAxisMinValue() : minRight - bottomSpaceRight;

        // consider starting at zero (0)
        if (mAxisLeft.isStartAtZeroEnabled())
            mAxisLeft.mAxisMinimum = 0f;

        if (mAxisRight.isStartAtZeroEnabled())
            mAxisRight.mAxisMinimum = 0f;

        mAxisLeft.mAxisRange = Math.abs(mAxisLeft.mAxisMaximum - mAxisLeft.mAxisMinimum);
        mAxisRight.mAxisRange = Math.abs(mAxisRight.mAxisMaximum - mAxisRight.mAxisMinimum);
    }

    @Override
    protected void calculateOffsets() {

        if (!mCustomViewPortEnabled) {

            float offsetLeft = 0f, offsetRight = 0f, offsetTop = 0f, offsetBottom = 0f;

            // setup offsets for legend
            if (mLegend != null && mLegend.isEnabled()) {

                if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART
                        || mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART_CENTER) {

                    offsetRight += mLegend.mTextWidthMax + mLegend.getXOffset() * 2f;

                } else if (mLegend.getPosition() == LegendPosition.LEFT_OF_CHART
                        || mLegend.getPosition() == LegendPosition.LEFT_OF_CHART_CENTER) {

                    offsetLeft += mLegend.mTextWidthMax + mLegend.getXOffset() * 2f;

                } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                        || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT
                        || mLegend.getPosition() == LegendPosition.BELOW_CHART_CENTER) {

                    offsetBottom += mLegend.mTextHeightMax * 3f;
                }
            }

            // offsets for y-labels
            if (mAxisLeft.needsOffset()) {
                offsetLeft += mAxisLeft.getRequiredWidthSpace(mAxisRendererLeft
                        .getPaintAxisLabels());
            }

            if (mAxisRight.needsOffset()) {
                offsetRight += mAxisRight.getRequiredWidthSpace(mAxisRendererRight
                        .getPaintAxisLabels());
            }

            float xlabelheight = mXAxis.mLabelHeight * 2f;

            if (mXAxis.isEnabled()) {

                // offsets for x-labels
                if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {

                    offsetBottom += xlabelheight;

                } else if (mXAxis.getPosition() == XAxisPosition.TOP) {

                    offsetTop += xlabelheight;

                } else if (mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {

                    offsetBottom += xlabelheight;
                    offsetTop += xlabelheight;
                }
            }

            float min = Utils.convertDpToPixel(10f);

            mViewPortHandler.restrainViewPort(Math.max(min, offsetLeft), Math.max(min, offsetTop),
                    Math.max(min, offsetRight), Math.max(min, offsetBottom));

            if (mLogEnabled) {
                Log.i(LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop
                        + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom);
                Log.i(LOG_TAG, "Content: " + mViewPortHandler.getContentRect().toString());
            }
        }

        prepareOffsetMatrix();
        prepareValuePxMatrix();
    }

    /**
     * calculates the modulus for x-labels and grid
     */
    protected void calcModulus() {

        if (mXAxis == null)
            return;

        float[] values = new float[9];
        mViewPortHandler.getMatrixTouch().getValues(values);

        mXAxis.mAxisLabelModulus = (int) Math
                .ceil((mData.getXValCount() * mXAxis.mLabelWidth)
                        / (mViewPortHandler.contentWidth() * values[Matrix.MSCALE_X]));

        if (mLogEnabled)
            Log.i(LOG_TAG, "X-Axis modulus: " + mXAxis.mAxisLabelModulus + ", x-axis label width: "
                    + mXAxis.mLabelWidth + ", content width: " + mViewPortHandler.contentWidth());

        if (mXAxis.mAxisLabelModulus < 1)
            mXAxis.mAxisLabelModulus = 1;
    }

    @Override
    protected float[] getMarkerPosition(Entry e, int dataSetIndex) {

        float xPos = e.getXIndex();

        if (this instanceof BarChart) {

            BarData bd = (BarData) mData;
            float space = bd.getGroupSpace();
            float j = mData.getDataSetByIndex(dataSetIndex)
                    .getEntryPosition(e);

            float x = (j * (mData.getDataSetCount() - 1)) + dataSetIndex + space * j + space
                    / 2f;

            xPos += x;
        }

        // position of the marker depends on selected value index and value
        float[] pts = new float[] {
                xPos, e.getVal() * mAnimator.getPhaseY()
        };

        getTransformer(mData.getDataSetByIndex(dataSetIndex).getAxisDependency())
                .pointValuesToPixel(pts);

        return pts;
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        super.drawMarkers(canvas);
        // if there is no marker view or drawing marker is disabled
        if (mMarkerView == null || !mDrawMarkerViews || !valuesToHighlight() || !mMarkerView.isDrawAxisText()
                || mMarkerView.getAxisText() == null || mMarkerView.getAxisText().length() == 0)
            return;

        for (int i = 0; i < mIndicesToHightlight.length; i++) {
            int xIndex = mIndicesToHightlight[i].getXIndex();
            int dataSetIndex = mIndicesToHightlight[i].getDataSetIndex();

            if (xIndex <= mDeltaX && xIndex <= mDeltaX * mAnimator.getPhaseX()) {

                Entry e = mData.getEntryForHighlight(mIndicesToHightlight[i]);

                // make sure entry not null
                if (e == null)
                    continue;

                float[] pos = getMarkerPosition(e, dataSetIndex);

                // check bounds
                if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                    continue;

                String text = mMarkerView.getAxisText();
                mAxisTextPaint.setTextSize(Utils.convertDpToPixel(mMarkerView.getAxisTextSize()));
                mAxisTextPaint.setColor(mMarkerView.getAxisTextColor());
                mAxisTextPaint.getTextBounds(text, 0, text.length(), mAxisTextBoundsRect);
                float x;
                float y = pos[1] + mAxisTextBoundsRect.height() / 2;

                if(mMarkerView.getAxisDependency() == AxisDependency.LEFT) {
                    YAxis yAxis = getAxisLeft();
                    if(yAxis.getLabelPosition() == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                        mAxisTextPaint.setTextAlign(Paint.Align.RIGHT);
                        x = mViewPortHandler.offsetLeft() - getAxisLeft().getXOffset();
                    }
                    else {
                        mAxisTextPaint.setTextAlign(Paint.Align.LEFT);
                        x = mViewPortHandler.offsetLeft() + getAxisLeft().getXOffset();
                    }
                }
                else {
                    YAxis yAxis = getAxisRight();
                    if(yAxis.getLabelPosition() == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                        mAxisTextPaint.setTextAlign(Paint.Align.LEFT);
                        x = mViewPortHandler.contentRight() + mAxisRight.getXOffset();
                    }
                    else {
                        mAxisTextPaint.setTextAlign(Paint.Align.RIGHT);
                        x = mViewPortHandler.contentRight() - mAxisRight.getXOffset();
                    }
                }
                canvas.drawText(text, x, y, mAxisTextPaint);
            }
        }

    }

    /**
     * draws the grid background
     */
    protected void drawGridBackground(Canvas c) {

        if (mDrawGridBackground) {

            // draw the grid background
            c.drawRect(mViewPortHandler.getContentRect(), mGridBackgroundPaint);
        }

        if (mDrawBorders) {
            c.drawRect(mViewPortHandler.getContentRect(), mBorderPaint);
        }
    }

    /**
     * Returns the Transformer class that contains all matrices and is
     * responsible for transforming values into pixels on the screen and
     * backwards.
     *
     * @return
     */
    public Transformer getTransformer(AxisDependency which) {
        if (which == AxisDependency.LEFT)
            return mLeftAxisTransformer;
        else
            return mRightAxisTransformer;
    }

    /** touchlistener that handles touches and gestures on the chart */
    protected OnTouchListener mListener;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (mListener == null || mDataNotSet)
            return false;

        // check if touch gestures are enabled
        if (!mTouchEnabled)
            return false;
        else
            return mListener.onTouch(this, event);
    }

    /**
     * ################ ################ ################ ################
     */
    /** CODE BELOW THIS RELATED TO SCALING AND GESTURES */

    /**
     * Zooms in by 1.4f, into the charts center. center.
     */
    public void zoomIn() {
        Matrix save = mViewPortHandler.zoomIn(getWidth() / 2f, -(getHeight() / 2f));
        mViewPortHandler.refresh(save, this, true);
    }

    /**
     * Zooms out by 0.7f, from the charts center. center.
     */
    public void zoomOut() {
        Matrix save = mViewPortHandler.zoomOut(getWidth() / 2f, -(getHeight() / 2f));
        mViewPortHandler.refresh(save, this, true);
    }

    /**
     * Zooms in or out by the given scale factor. x and y are the coordinates
     * (in pixels) of the zoom center.
     * 
     * @param scaleX if < 1f --> zoom out, if > 1f --> zoom in
     * @param scaleY if < 1f --> zoom out, if > 1f --> zoom in
     * @param x
     * @param y
     */
    public void zoom(float scaleX, float scaleY, float x, float y) {
        Matrix save = mViewPortHandler.zoom(scaleX, scaleY, x, -y);
        mViewPortHandler.refresh(save, this, true);
    }

    /**
     * Resets all zooming and dragging and makes the chart fit exactly it's
     * bounds.
     */
    public void fitScreen() {
        Matrix save = mViewPortHandler.fitScreen();
        mViewPortHandler.refresh(save, this, true);
    }

    /**
     * Sets the minimum scale value to which can be zoomed out. 1f = fitScreen
     * 
     * @param scaleX
     * @param scaleY
     */
    public void setScaleMinima(float scaleX, float scaleY) {
        mViewPortHandler.setMinimumScaleX(scaleX);
        mViewPortHandler.setMinimumScaleY(scaleY);
    }

    /**
     * Sets the size of the area (range on the x-axis) that should be maximum
     * visible at once. If this is e.g. set to 10, no more than 10 values on the
     * x-axis can be viewed at once without scrolling.
     * 
     * @param xRange
     */
    public void setVisibleXRange(float xRange) {
        float xScale = mDeltaX / (xRange + 0.01f);
        mViewPortHandler.setMinimumScaleX(xScale);
    }

    /**
     * Sets the size of the area (range on the y-axis) that should be maximum
     * visible at once.
     * 
     * @param yRange
     * @param axis - the axis for which this limit should apply
     */
    public void setVisibleYRange(float yRange, AxisDependency axis) {
        float yScale = getDeltaY(axis) / yRange;
        mViewPortHandler.setMinimumScaleY(yScale);
    }

    /**
     * Moves the left side of the current viewport to the specified x-index.
     * 
     * @param xIndex
     */
    public void moveViewToX(int xIndex) {

        float[] pts = new float[] {
                xIndex, 0f
        };

        getTransformer(AxisDependency.LEFT).pointValuesToPixel(pts);
        mViewPortHandler.centerViewPort(pts, this);
    }

    /**
     * Centers the viewport to the specified y-value on the y-axis.
     * 
     * @param yValue
     * @param axis - which axis should be used as a reference for the y-axis
     */
    public void moveViewToY(float yValue, AxisDependency axis) {

        float valsInView = getDeltaY(axis) / mViewPortHandler.getScaleY();

        float[] pts = new float[] {
                0f, yValue + valsInView / 2f
        };

        getTransformer(axis).pointValuesToPixel(pts);
        mViewPortHandler.centerViewPort(pts, this);
    }

    /**
     * This will move the left side of the current viewport to the specified
     * x-index on the x-axis, and center the viewport to the specified y-value
     * on the y-axis.
     * 
     * @param xIndex
     * @param yValue
     * @param axis - which axis should be used as a reference for the y-axis
     */
    public void moveViewTo(int xIndex, float yValue, AxisDependency axis) {

        float valsInView = getDeltaY(axis) / mViewPortHandler.getScaleY();

        float[] pts = new float[] {
                xIndex, yValue + valsInView / 2f
        };

        getTransformer(axis).pointValuesToPixel(pts);
        mViewPortHandler.centerViewPort(pts, this);
    }

    /** flag that indicates if a custom viewport offset has been set */
    private boolean mCustomViewPortEnabled = false;

    /**
     * Sets custom offsets for the current ViewPort (the offsets on the sides of
     * the actual chart window). Setting this will prevent the chart from
     * automatically calculating it's offsets. Use resetViewPortOffsets() to
     * undo this.
     * 
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setViewPortOffsets(final float left, final float top,
            final float right, final float bottom) {

        mCustomViewPortEnabled = true;
        post(new Runnable() {

            @Override
            public void run() {

                mViewPortHandler.restrainViewPort(left, top, right, bottom);
            }
        });
    }

    /**
     * Resets all custom offsets set via setViewPortOffsets(...) method. Allows
     * the chart to again calculate all offsets automatically.
     */
    public void resetViewPortOffsets() {
        mCustomViewPortEnabled = false;
        calculateOffsets();
    }

    /**
     * ################ ################ ################ ################
     */
    /** CODE BELOW IS GETTERS AND SETTERS */

    /**
     * Returns the delta-y value (y-value range) of the specified axis.
     * 
     * @param axis
     * @return
     */
    public float getDeltaY(AxisDependency axis) {
        if (axis == AxisDependency.LEFT)
            return mAxisLeft.mAxisRange;
        else
            return mAxisRight.mAxisRange;
    }

    /**
     * set a new (e.g. custom) charttouchlistener NOTE: make sure to
     * setTouchEnabled(true); if you need touch gestures on the chart
     * 
     * @param l
     */
    public void setOnTouchListener(OnTouchListener l) {
        this.mListener = l;
    }

    /**
     * Sets the OnDrawListener
     * 
     * @param drawListener
     */
    public void setOnDrawListener(OnDrawListener drawListener) {
        this.mDrawListener = drawListener;
    }

    /**
     * Gets the OnDrawListener. May be null.
     * 
     * @return
     */
    public OnDrawListener getDrawListener() {
        return mDrawListener;
    }

    /**
     * Returns the position (in pixels) the provided Entry has inside the chart
     * view or null, if the provided Entry is null.
     * 
     * @param e
     * @return
     */
    public PointF getPosition(Entry e, AxisDependency axis) {

        if (e == null)
            return null;

        float[] vals = new float[] {
                e.getXIndex(), e.getVal()
        };

        getTransformer(axis).pointValuesToPixel(vals);

        return new PointF(vals[0], vals[1]);
    }

    /**
     * sets the number of maximum visible drawn values on the chart only active
     * when setDrawValues() is enabled
     * 
     * @param count
     */
    public void setMaxVisibleValueCount(int count) {
        this.mMaxVisibleCount = count;
    }

    public int getMaxVisibleCount() {
        return mMaxVisibleCount;
    }

    /**
     * If set to true, the highlight indicators (cross of two lines for
     * LineChart and ScatterChart, dark bar overlay for BarChart) that give
     * visual indication that an Entry has been selected will be drawn upon
     * selecting values. This does not depend on the MarkerView. Default: true
     * 
     * @param enabled
     */
    public void setHighlightIndicatorEnabled(boolean enabled) {
        mHighLightIndicatorEnabled = enabled;
    }

    /**
     * Sets the color for the background of the chart-drawing area (everything
     * behind the grid lines).
     * 
     * @param color
     */
    public void setGridBackgroundColor(int color) {
        mGridBackgroundPaint.setColor(color);
    }

    /**
     * Set this to true to enable dragging (moving the chart with the finger)
     * for the chart (this does not effect scaling).
     * 
     * @param enabled
     */
    public void setDragEnabled(boolean enabled) {
        this.mDragEnabled = enabled;
    }

    /**
     * Returns true if dragging is enabled for the chart, false if not.
     * 
     * @return
     */
    public boolean isDragEnabled() {
        return mDragEnabled;
    }

    /**
     * Set this to true to enable scaling (zooming in and out by gesture) for
     * the chart (this does not effect dragging) on both X- and Y-Axis.
     * 
     * @param enabled
     */
    public void setScaleEnabled(boolean enabled) {
        this.mScaleXEnabled = enabled;
        this.mScaleYEnabled = enabled;
    }

    public void setScaleXEnabled(boolean enabled) {
        mScaleXEnabled = enabled;
    }

    public void setScaleYEnabled(boolean enabled) {
        mScaleYEnabled = enabled;
    }

    public boolean isScaleXEnabled() {
        return mScaleXEnabled;
    }

    public boolean isScaleYEnabled() {
        return mScaleYEnabled;
    }

    /**
     * Set this to true to enable zooming in by double-tap on the chart.
     * Default: enabled
     * 
     * @param enabled
     */
    public void setDoubleTapToZoomEnabled(boolean enabled) {
        mDoubleTapToZoomEnabled = enabled;
    }

    /**
     * Returns true if zooming via double-tap is enabled false if not.
     * 
     * @return
     */
    public boolean isDoubleTapToZoomEnabled() {
        return mDoubleTapToZoomEnabled;
    }

    /**
     * set this to true to draw the grid background, false if not
     * 
     * @param enabled
     */
    public void setDrawGridBackground(boolean enabled) {
        mDrawGridBackground = enabled;
    }

    /**
     * Sets drawing the borders rectangle to true. If this is enabled, there is
     * no point drawing the axis-lines of x- and y-axis.
     * 
     * @param enabled
     */
    public void setDrawBorders(boolean enabled) {
        mDrawBorders = enabled;
    }

    /**
     * Sets the width of the border lines in dp.
     * 
     * @param width
     */
    public void setBorderWidth(float width) {
        mBorderPaint.setStrokeWidth(Utils.convertDpToPixel(width));
    }

    /**
     * Sets the color of the chart border lines.
     * 
     * @param color
     */
    public void setBorderColor(int color) {
        mBorderPaint.setColor(color);
    }

    /**
     * Returns the Highlight object (contains x-index and DataSet index) of the
     * selected value at the given touch point inside the Line-, Scatter-, or
     * CandleStick-Chart.
     * 
     * @param x
     * @param y
     * @return
     */
    public Highlight getHighlightByTouchPoint(float x, float y) {

        if (mDataNotSet || mData == null) {
            Log.e(LOG_TAG, "Can't select by touch. No data set.");
            return null;
        }

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;

        // take any transformer to determine the x-axis value
        mLeftAxisTransformer.pixelsToValue(pts);

        double xTouchVal = pts[0];
        double base = Math.floor(xTouchVal);

        double touchOffset = mDeltaX * 0.025;

        // touch out of chart
        if (xTouchVal < -touchOffset || xTouchVal > mDeltaX + touchOffset)
            return null;

        if (base < 0)
            base = 0;
        if (base >= mDeltaX)
            base = mDeltaX - 1;

        int xIndex = (int) base;

        // check if we are more than half of a x-value or not
        if (xTouchVal - base > 0.5) {
            xIndex = (int) base + 1;
        }

        List<SelInfo> valsAtIndex = getYValsAtIndex(xIndex);

        float leftdist = Utils.getMinimumDistance(valsAtIndex, y, AxisDependency.LEFT);
        float rightdist = Utils.getMinimumDistance(valsAtIndex, y, AxisDependency.RIGHT);

        if (mData.getFirstRight() == null)
            rightdist = Float.MAX_VALUE;
        if (mData.getFirstLeft() == null)
            leftdist = Float.MAX_VALUE;

        AxisDependency axis = leftdist < rightdist ? AxisDependency.LEFT : AxisDependency.RIGHT;

        int dataSetIndex = Utils.getClosestDataSetIndex(valsAtIndex, y, axis);

        if (dataSetIndex == -1)
            return null;

        return new Highlight(xIndex, dataSetIndex);
    }

    /**
     * Returns an array of SelInfo objects for the given x-index. The SelInfo
     * objects give information about the value at the selected index and the
     * DataSet it belongs to. INFORMATION: This method does calculations at
     * runtime. Do not over-use in performance critical situations.
     *
     * @return
     */
    public List<SelInfo> getYValsAtIndex(int xIndex) {

        List<SelInfo> vals = new ArrayList<SelInfo>();

        float[] pts = new float[2];

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            DataSet<?> dataSet = mData.getDataSetByIndex(i);

            // extract all y-values from all DataSets at the given x-index
            float yVal = dataSet.getYValForXIndex(xIndex);
            pts[1] = yVal;

            getTransformer(dataSet.getAxisDependency()).pointValuesToPixel(pts);

            if (!Float.isNaN(pts[1])) {
                vals.add(new SelInfo(pts[1], i, dataSet));
            }
        }

        return vals;
    }

    /**
     * Returns the x and y values in the chart at the given touch point
     * (encapsulated in a PointD). This method transforms pixel coordinates to
     * coordinates / values in the chart. This is the opposite method to
     * getPixelsForValues(...).
     * 
     * @param x
     * @param y
     * @return
     */
    public PointD getValuesByTouchPoint(float x, float y, AxisDependency axis) {

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;
        pts[1] = y;

        getTransformer(axis).pixelsToValue(pts);

        double xTouchVal = pts[0];
        double yTouchVal = pts[1];

        return new PointD(xTouchVal, yTouchVal);
    }

    /**
     * Transforms the given chart values into pixels. This is the opposite
     * method to getValuesByTouchPoint(...).
     * 
     * @param x
     * @param y
     * @return
     */
    public PointD getPixelsForValues(float x, float y, AxisDependency axis) {

        float[] pts = new float[] {
                x, y
        };

        getTransformer(axis).pointValuesToPixel(pts);

        return new PointD(pts[0], pts[1]);
    }

    /**
     * returns the y-value at the given touch position (must not necessarily be
     * a value contained in one of the datasets)
     * 
     * @param x
     * @param y
     * @return
     */
    public float getYValueByTouchPoint(float x, float y, AxisDependency axis) {
        return (float) getValuesByTouchPoint(x, y, axis).y;
    }

    /**
     * returns the Entry object displayed at the touched position of the chart
     * 
     * @param x
     * @param y
     * @return
     */
    public Entry getEntryByTouchPoint(float x, float y) {
        Highlight h = getHighlightByTouchPoint(x, y);
        if (h != null) {
            return mData.getEntryForHighlight(h);
        }
        return null;
    }

    /**
     * returns the DataSet object displayed at the touched position of the chart
     * 
     * @param x
     * @param y
     * @return
     */
    public BarLineScatterCandleDataSet<? extends Entry> getDataSetByTouchPoint(float x, float y) {
        Highlight h = getHighlightByTouchPoint(x, y);
        if (h != null) {
            return mData.getDataSetByIndex(h.getDataSetIndex());
        }
        return null;
    }

    /**
     * Returns the lowest x-index (value on the x-axis) that is still visible on
     * the chart.
     * 
     * @return
     */
    public int getLowestVisibleXIndex() {
        float[] pts = new float[] {
                mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom()
        };
        getTransformer(AxisDependency.LEFT).pixelsToValue(pts);
        return (pts[0] <= 0) ? 0 : (int) (pts[0] + 1);
    }

    /**
     * Returns the highest x-index (value on the x-axis) that is still visible
     * on the chart.
     * 
     * @return
     */
    public int getHighestVisibleXIndex() {
        float[] pts = new float[] {
                mViewPortHandler.contentRight(), mViewPortHandler.contentBottom()
        };
        getTransformer(AxisDependency.LEFT).pixelsToValue(pts);
        return (pts[0] >= mData.getXValCount()) ? mData.getXValCount() - 1 : (int) pts[0];
    }

    /**
     * returns the current x-scale factor
     */
    public float getScaleX() {
        return mViewPortHandler.getScaleX();
    }

    /**
     * returns the current y-scale factor
     */
    public float getScaleY() {
        return mViewPortHandler.getScaleY();
    }

    /**
     * if the chart is fully zoomed out, return true
     * 
     * @return
     */
    public boolean isFullyZoomedOut() {
        return mViewPortHandler.isFullyZoomedOut();
    }

    /**
     * Returns the left y-axis object. In the horizontal bar-chart, this is the
     * top axis.
     * 
     * @return
     */
    public YAxis getAxisLeft() {
        return mAxisLeft;
    }

    /**
     * Returns the right y-axis object. In the horizontal bar-chart, this is the
     * bottom axis.
     * 
     * @return
     */
    public YAxis getAxisRight() {
        return mAxisRight;
    }

    /**
     * Returns the y-axis object to the corresponding AxisDependency. In the
     * horizontal bar-chart, LEFT == top, RIGHT == BOTTOM
     * 
     * @param axis
     * @return
     */
    public YAxis getAxis(AxisDependency axis) {
        if (axis == AxisDependency.LEFT)
            return mAxisLeft;
        else
            return mAxisRight;
    }

    /**
     * Returns the object representing all x-labels, this method can be used to
     * acquire the XAxis object and modify it (e.g. change the position of the
     * labels)
     * 
     * @return
     */
    public XAxis getXAxis() {
        return mXAxis;
    }

    /**
     * Enables data filtering for the chart data, filtering will use the user
     * customized Approximator handed over to this method.
     * 
     * @param a
     */
    public void enableFiltering(Approximator a) {
        mFilterData = true;
        // mApproximator = a;
    }

    /**
     * Disables data filtering for the chart.
     */
    public void disableFiltering() {
        mFilterData = false;
    }

    /**
     * returns true if data filtering is enabled, false if not
     * 
     * @return
     */
    public boolean isFilteringEnabled() {
        return mFilterData;
    }

    /**
     * if set to true, both x and y axis can be scaled with 2 fingers, if false,
     * x and y axis can be scaled separately. default: false
     * 
     * @param enabled
     */
    public void setPinchZoom(boolean enabled) {
        mPinchZoomEnabled = enabled;
    }

    /**
     * returns true if pinch-zoom is enabled, false if not
     * 
     * @return
     */
    public boolean isPinchZoomEnabled() {
        return mPinchZoomEnabled;
    }

    /**
     * Set an offset in dp that allows the user to drag the chart over it's
     * bounds on the x-axis.
     * 
     * @param offset
     */
    public void setDragOffsetX(float offset) {
        mViewPortHandler.setDragOffsetX(offset);
    }

    /**
     * Set an offset in dp that allows the user to drag the chart over it's
     * bounds on the y-axis.
     * 
     * @param offset
     */
    public void setDragOffsetY(float offset) {
        mViewPortHandler.setDragOffsetY(offset);
    }

    /**
     * Returns true if both drag offsets (x and y) are zero or smaller.
     * 
     * @return
     */
    public boolean hasNoDragOffset() {
        return mViewPortHandler.hasNoDragOffset();
    }

    public XAxisRenderer getRendererXAxis() {
        return mXAxisRenderer;
    }

    public YAxisRenderer getRendererLeftYAxis() {
        return mAxisRendererLeft;
    }

    public YAxisRenderer getRendererRightYAxis() {
        return mAxisRendererRight;
    }

    public float getYChartMax() {
        return Math.max(mAxisLeft.mAxisMaximum, mAxisRight.mAxisMaximum);
    }

    public float getYChartMin() {
        return Math.min(mAxisLeft.mAxisMinimum, mAxisRight.mAxisMinimum);
    }

    /**
     * Returns fling friction coefficient
     * @return
     */
    public float getFlingFrictionCoef() {
        return mFlingFrictionCoef;
    }

    /**
     * Fling friction coefficient in [0 ; 1] interval, higher values indicate that speed will
     * decrease slowly, for example if it set to 0, it will stop immediately, if set to 1, it will
     * scroll with constant speed, until last point
     * @param flingFrictionCoef
     */
    public void setFlingFrictionCoef(float flingFrictionCoef) {
        if(flingFrictionCoef < 0) {
            flingFrictionCoef = 0;
        }
        else if(flingFrictionCoef > 1) {
            flingFrictionCoef = 1;
        }
        mFlingFrictionCoef = flingFrictionCoef;
    }

    /**
     * If fling friction enabled or not
     * @return
     */
    public boolean isFlingFrictionEnabled() {
        return mFlingFrictionEnabled;
    }

    /**
     * If set to true, chart continues to scroll after touch up, but with friction, and its speed
     * decrease over time, depend on getFlingFrictionCoef value
     * @param flingFrictionEnabled
     */
    public void setFlingFrictionEnabled(boolean flingFrictionEnabled) {
        mFlingFrictionEnabled = flingFrictionEnabled;
    }

    /**
     * Returns true if either the left or the right or both axes are inverted.
     * 
     * @return
     */
    public boolean isAnyAxisInverted() {
        if (mAxisLeft.isInverted())
            return true;
        if (mAxisRight.isInverted())
            return true;
        return false;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_GRID_BACKGROUND:
                mGridBackgroundPaint = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        Paint p = super.getPaint(which);
        if (p != null)
            return p;

        switch (which) {
            case PAINT_GRID_BACKGROUND:
                return mGridBackgroundPaint;
        }

        return null;
    }

    /**
     * Default formatter that calculates the position of the filled line.
     * 
     * @author Philipp Jahoda
     */
    protected class DefaultFillFormatter implements FillFormatter {

        @Override
        public float getFillLinePosition(LineDataSet dataSet, LineData data,
                float chartMaxY, float chartMinY) {

            float fillMin = 0f;

            if (dataSet.getYMax() > 0 && dataSet.getYMin() < 0) {
                fillMin = 0f;
            } else {

                if (!getAxis(dataSet.getAxisDependency()).isStartAtZeroEnabled()) {

                    float max, min;

                    if (data.getYMax() > 0)
                        max = 0f;
                    else
                        max = chartMaxY;
                    if (data.getYMin() < 0)
                        min = 0f;
                    else
                        min = chartMinY;

                    fillMin = dataSet.getYMin() >= 0 ? min : max;
                } else {
                    fillMin = 0f;
                }

            }

            return fillMin;
        }
    }
}
