
package com.github.mikephil.charting.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XLabelPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarLineScatterCandleData;
import com.github.mikephil.charting.data.BarLineScatterCandleDataSet;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.interfaces.OnDrawListener;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.FillFormatter;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

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

    /** the width of the grid lines */
    protected float mGridWidth = 1f;

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

    /** if true, scaling is enabled for the chart */
    private boolean mScaleEnabled = true;

    /** if true, data filtering is enabled */
    protected boolean mFilterData = false;

    /** paint object for the (by default) lightgrey background of the grid */
    protected Paint mGridBackgroundPaint;

    /** paint for the line surrounding the chart */
    protected Paint mBorderPaint;

    /**
     * if set to true, the highlight indicator (lines for linechart, dark bar
     * for barchart) will be drawn upon selecting values.
     */
    protected boolean mHighLightIndicatorEnabled = true;

    /** flag indicating if the chart border rectangle should be drawn or not */
    protected boolean mDrawBorder = true;

    /** flag indicating if the grid background should be drawn or not */
    protected boolean mDrawGridBackground = true;

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

        mListener = new BarLineChartTouchListener(this, mViewPortHandler.getMatrixTouch());

        mBorderPaint = new Paint();
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setStrokeWidth(mGridWidth * 2f);
        mBorderPaint.setStyle(Style.STROKE);

        mGridBackgroundPaint = new Paint();
        mGridBackgroundPaint.setStyle(Style.FILL);
        // mGridBackgroundPaint.setColor(Color.WHITE);
        mGridBackgroundPaint.setColor(Color.rgb(240, 240, 240)); // light
        // grey
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataNotSet)
            return;

        long starttime = System.currentTimeMillis();

        // if data filtering is enabled
        if (mFilterData) {
            mData = getFilteredData();

            Log.i(LOG_TAG, "FilterTime: " + (System.currentTimeMillis() -
                    starttime) + " ms");
            starttime = System.currentTimeMillis();
        } else {
            mData = getData();
            // Log.i(LOG_TAG, "Filtering disabled.");
        }

        if (mXAxis.isAdjustXLabelsEnabled())
            calcModulus();

        // execute all drawing commands
        drawGridBackground();

        mAxisRendererLeft.computeAxis(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisMaximum);
        mAxisRendererRight.computeAxis(mAxisRight.mAxisMinimum, mAxisRight.mAxisMaximum);

        // make sure the graph values and grid cannot be drawn outside the
        // content-rect
        int clipRestoreCount = mDrawCanvas.save();
        mDrawCanvas.clipRect(mViewPortHandler.getContentRect());

        mXAxisRenderer.renderGridLines(mDrawCanvas);
        mAxisRendererLeft.renderGridLines(mDrawCanvas);
        mAxisRendererRight.renderGridLines(mDrawCanvas);

        mRenderer.drawData(mDrawCanvas);

        mAxisRendererLeft.renderLimitLines(mDrawCanvas, mValueFormatter);
        mAxisRendererRight.renderLimitLines(mDrawCanvas, mValueFormatter);

        // if highlighting is enabled
        if (mHighlightEnabled && mHighLightIndicatorEnabled && valuesToHighlight())
            mRenderer.drawHighlighted(mDrawCanvas, mIndicesToHightlight);

        // Removes clipping rectangle
        mDrawCanvas.restoreToCount(clipRestoreCount);

        mRenderer.drawExtras(mDrawCanvas);

        mXAxisRenderer.renderAxis(mDrawCanvas);

        mAxisRendererLeft.renderAxis(mDrawCanvas);

        mAxisRendererRight.renderAxis(mDrawCanvas);

        mRenderer.drawValues(mDrawCanvas);

        drawLegend();

        drawBorder();

        drawMarkers();

        drawDescription();

        canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);

        if (mLogEnabled)
            Log.i(LOG_TAG, "DrawTime: " + (System.currentTimeMillis() - starttime) + " ms");
    }

    private void prepareValuePxMatrix() {

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

        calcMinMax();

        mAxisRendererLeft.computeAxis(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisMaximum);
        mAxisRendererRight.computeAxis(mAxisRight.mAxisMinimum, mAxisRight.mAxisMaximum);

        mXAxisRenderer.computeAxis(mData.getXValAverageLength(), mData.getXVals());

        prepareLegend();

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

        float topSpaceLeft = leftRange / 100f * mAxisLeft.getSpaceTop();
        float topSpaceRight = rightRange / 100f * mAxisRight.getSpaceTop();
        float bottomSpaceLeft = leftRange / 100f * mAxisLeft.getSpaceBottom();
        float bottomSpaceRight = rightRange / 100f * mAxisRight.getSpaceBottom();

        Log.i(LOG_TAG, "minLeft: " + minLeft + ", maxLeft: " + maxLeft);

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

        if (mAxisLeft.isStartAtZeroEnabled())
            mAxisLeft.mAxisMinimum = 0f;
        if (mAxisRight.isStartAtZeroEnabled())
            mAxisRight.mAxisMinimum = 0f;

        mAxisLeft.mAxisRange = Math.abs(mAxisLeft.mAxisMaximum - mAxisLeft.mAxisMinimum);
        mAxisRight.mAxisRange = Math.abs(mAxisRight.mAxisMaximum - mAxisRight.mAxisMinimum);
        //
        // // only calculate values if not fixed values
        // if (!fixedValues) {
        // mYChartMin = mData.getYMin();
        // mYChartMax = mData.getYMax();
        // }
        //
        // // calc delta
        // mDeltaY = Math.abs(mYChartMax - mYChartMin);
        // mDeltaX = mData.getXVals().size() - 1;
        //
        // if (!fixedValues) {
        //
        // // additional handling for space (default 15% space)
        // // float space = Math.abs(mDeltaY / 100f * 15f);
        // float space = Math
        // .abs(Math.abs(Math.max(Math.abs(mYChartMax), Math.abs(mYChartMin))) /
        // 100f * 20f);
        //
        // if (Math.abs(mYChartMax - mYChartMin) < 0.00001f) {
        // if (Math.abs(mYChartMax) < 10f)
        // space = 1f;
        // else
        // space = Math.abs(mYChartMax / 100f * 20f);
        // }
        //
        // if (mStartAtZero) {
        //
        // if (mYChartMax < 0) {
        // mYChartMax = 0;
        // // calc delta
        // mYChartMin = mYChartMin - space;
        // } else {
        // mYChartMin = 0;
        // // calc delta
        // mYChartMax = mYChartMax + space;
        // }
        // } else {
        //
        // mYChartMin = mYChartMin - space / 2f;
        // mYChartMax = mYChartMax + space / 2f;
        // }
        // }
        //
        // mDeltaY = Math.abs(mYChartMax - mYChartMin);
    }

    @Override
    protected void calculateOffsets() {

        float legendRight = 0f, legendBottom = 0f;

        // setup offsets for legend
        if (mDrawLegend && mLegend != null && mLegend.getPosition() != LegendPosition.NONE) {

            if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART
                    || mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART_CENTER) {

                // this is the space between the legend and the chart
                float spacing = Utils.convertDpToPixel(12f);

                legendRight = mLegend.getMaximumEntryLength(mLegendLabelPaint)
                        + mLegend.getFormSize() + mLegend.getFormToTextSpace() + spacing;

                mLegendLabelPaint.setTextAlign(Align.LEFT);

            } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                    || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT
                    || mLegend.getPosition() == LegendPosition.BELOW_CHART_CENTER) {

                if (mXAxis.getPosition() == XLabelPosition.TOP)
                    legendBottom = mLegendLabelPaint.getTextSize() * 3.5f;
                else {
                    legendBottom = mLegendLabelPaint.getTextSize() * 2.5f;
                }
            }

            mLegend.setOffsetBottom(legendBottom);
            mLegend.setOffsetRight(legendRight);
        }

        float yleft = 0f, yright = 0f;

        // offsets for y-labels
        if (mAxisLeft.isEnabled()) {
            String label = mAxisLeft.getLongestLabel();

            // calculate the maximum y-label width (including eventual
            // offsets)
            float ylabelwidth = Utils.calcTextWidth(mAxisRendererLeft.getAxisPaint(),
                    label + (mAxisLeft.mAxisMinimum < 0 ? "----" : "+++")); // offsets
            yleft = ylabelwidth + mAxisRendererLeft.getXOffset() / 2f;
        }

        if (mAxisRight.isEnabled()) {
            String label = mAxisRight.getLongestLabel();

            // calculate the maximum y-label width (including eventual
            // offsets)
            float ylabelwidth = Utils.calcTextWidth(mAxisRendererRight.getAxisPaint(),
                    label + (mAxisLeft.mAxisMinimum < 0 ? "----" : "+++")); // offsets
            yright = ylabelwidth + mAxisRendererRight.getXOffset() / 2f;
        }

        float xtop = 0f, xbottom = 0f;

        float xlabelheight = Utils.calcTextHeight(mXAxisRenderer.getAxisPaint(), "Q") * 2f;

        if (mXAxis.isEnabled()) {

            // offsets for x-labels
            if (mXAxis.getPosition() == XLabelPosition.BOTTOM) {

                xbottom = xlabelheight;

            } else if (mXAxis.getPosition() == XLabelPosition.TOP) {

                xtop = xlabelheight;

            } else if (mXAxis.getPosition() == XLabelPosition.BOTH_SIDED) {

                xbottom = xlabelheight;
                xtop = xlabelheight;
            }
        }

        // all required offsets are calculated, now find largest and apply
        float min = Utils.convertDpToPixel(11f);

        float offsetBottom = Math.max(min, xbottom + legendBottom);
        float offsetTop = Math.max(min, xtop);

        float offsetLeft = Math.max(min, yleft);
        float offsetRight = Math.max(min, yright + legendRight);

        if (mLegend != null) {

            // those offsets are equal for legend and other chart, just apply
            // them
            mLegend.setOffsetTop(offsetTop + min / 3f);
            mLegend.setOffsetLeft(offsetLeft);
        }

        mViewPortHandler.restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom);
        if (mLogEnabled) {
            Log.i(LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop
                    + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom);
            Log.i(LOG_TAG, "Content: " + mViewPortHandler.getContentRect().toString());
        }

        prepareOffsetMatrix();
        prepareValuePxMatrix();
    }

    /**
     * Calculates the offsets that belong to the legend, this method is only
     * relevant when drawing into the chart. It can be used to refresh the
     * legend.
     */
    public void calculateLegendOffsets() {

        // setup offsets for legend
        if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART) {

            mLegend.setOffsetRight(mLegend.getMaximumEntryLength(mLegendLabelPaint));
            mLegendLabelPaint.setTextAlign(Align.LEFT);

        } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT) {

            if (mXAxis.getPosition() == XLabelPosition.TOP)
                mLegend.setOffsetBottom(mLegendLabelPaint.getTextSize() * 3.5f);
            else {
                mLegend.setOffsetBottom(mLegendLabelPaint.getTextSize() * 2.5f);
            }
        }
    }

    /**
     * calculates the modulus for x-labels and grid
     */
    protected void calcModulus() {

        float[] values = new float[9];
        mViewPortHandler.getMatrixTouch().getValues(values);

        mXAxis.mAxisLabelModulus = (int) Math
                .ceil((mData.getXValCount() * mXAxis.mLabelWidth)
                        / (mViewPortHandler.contentWidth() * values[Matrix.MSCALE_X]));

        if (mLogEnabled)
            Log.i(LOG_TAG, "X-Axis modulus: " + mXAxis.mAxisLabelModulus + ", x-axis label width: "
                    + mXAxis.mLabelWidth + ", content width: " + mViewPortHandler.contentWidth());
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

    /** enums for all different border styles */
    public enum BorderPosition {
        LEFT, RIGHT, TOP, BOTTOM
    }

    /**
     * array that holds positions where to draw the chart border lines
     */
    private BorderPosition[] mBorderPositions = new BorderPosition[] {
            BorderPosition.BOTTOM
    };

    /**
     * draws a line that surrounds the chart
     */
    protected void drawBorder() {

        if (!mDrawBorder || mBorderPositions == null)
            return;

        for (int i = 0; i < mBorderPositions.length; i++) {

            if (mBorderPositions[i] == null)
                continue;

            switch (mBorderPositions[i]) {
                case LEFT:
                    mDrawCanvas.drawLine(mViewPortHandler.contentLeft(),
                            mViewPortHandler.contentTop(), mViewPortHandler.contentLeft(),
                            mViewPortHandler.contentBottom(), mBorderPaint);
                    break;
                case RIGHT:
                    mDrawCanvas.drawLine(mViewPortHandler.contentRight(),
                            mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                            mViewPortHandler.contentBottom(), mBorderPaint);
                    break;
                case TOP:
                    mDrawCanvas.drawLine(mViewPortHandler.contentLeft(),
                            mViewPortHandler.contentTop(), mViewPortHandler.contentRight(),
                            mViewPortHandler.contentTop(), mBorderPaint);
                    break;
                case BOTTOM:
                    mDrawCanvas.drawLine(mViewPortHandler.contentLeft(),
                            mViewPortHandler.contentBottom(), mViewPortHandler.contentRight(),
                            mViewPortHandler.contentBottom(), mBorderPaint);
                    break;
            }
        }
    }

    /**
     * draws the grid background
     */
    protected void drawGridBackground() {

        if (!mDrawGridBackground)
            return;

        // Rect gridBackground = new Rect((int) mOffsetLeft + 1, (int)
        // mOffsetTop + 1, getWidth()
        // - (int) mOffsetRight,
        // getHeight() - (int) mOffsetBottom);

        // draw the grid background
        mDrawCanvas.drawRect(mViewPortHandler.getContentRect(), mGridBackgroundPaint);
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

    // /**
    // * Centers the viewport around the specified x-index and the specified
    // * y-value in the chart. Centering the viewport outside the bounds of the
    // * chart is not possible. Makes most sense in combination with the
    // * setScaleMinima(...) method. First set the scale minima, then center the
    // * viewport. SHOULD BE CALLED AFTER setting data for the chart.
    // *
    // * @param xIndex the index on the x-axis to center to
    // * @param yVal the value ont he y-axis to center to
    // */
    // public synchronized void centerViewPort(final int xIndex, final float
    // yVal, AxisDependency axis) {
    //
    // float indicesInView = mDeltaX / mViewPortHandler.getScaleX();
    // float valsInView = getDeltaY(axis) / mViewPortHandler.getScaleY();
    //
    // // Log.i(LOG_TAG, "indices: " + indicesInView + ", vals: " +
    // // valsInView);
    //
    // float[] pts = new float[] {
    // xIndex - indicesInView / 2f, yVal + valsInView / 2f
    // };
    //
    // getTransformer(axis).pointValuesToPixel(pts);
    // mViewPortHandler.centerViewPort(pts, this);
    // }

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

    // /**
    // * Sets the minimum scale values for both axes. This limits the extent to
    // * which the user can zoom-out. Scale 2f means the user cannot zoom out
    // * further than 2x zoom, ... Min = 1f
    // *
    // * @param scaleXmin
    // * @param scaleYmin
    // */
    // public void setScaleMinima(float scaleXmin, float scaleYmin) {
    // mViewPortHandler.setScaleMinima(scaleXmin, scaleYmin, this);
    // }

    // /**
    // * Sets the effective range of y-values the chart can display. If this is
    // * set, the y-range is fixed and cannot be changed. This means, no
    // * recalculation of the bounds of the chart concerning the y-axis will be
    // * done when adding new data. To disable this, provide Float.NaN as a
    // * parameter or call resetYRange();
    // *
    // * @param minY
    // * @param maxY
    // * @param invalidate if set to true, the chart will redraw itself after
    // * calling this method
    // */
    // public void setYRange(float minY, float maxY, AxisDependency axis,
    // boolean invalidate) {
    //
    // if (Float.isNaN(minY) || Float.isNaN(maxY)) {
    // resetYRange(invalidate);
    // return;
    // }
    //
    // mFixedYValues = true;
    //
    // mYChartMin = minY;
    // mYChartMax = maxY;
    // if (minY < 0) {
    // mStartAtZero = false;
    // }
    //
    // if (axis == AxisDependency.LEFT)
    // mDeltaYLeft = mYChartMax - mYChartMin;
    //
    // calcFormats();
    // prepareMatrix();
    // if (invalidate)
    // invalidate();
    // }
    //
    // /**
    // * Resets the previously set y range. If new data is added, the y-range
    // will
    // * be recalculated.
    // *
    // * @param invalidate if set to true, the chart will redraw itself after
    // * calling this method
    // */
    // public void resetYRange(boolean invalidate) {
    // mFixedYValues = false;
    // calcMinMax(mFixedYValues);
    //
    // prepareMatrix();
    // if (invalidate)
    // invalidate();
    // }

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

        if (this instanceof BarChart) {

            BarDataSet set = (BarDataSet) mData.getDataSetForEntry(e);
            if (set != null)
                vals[0] += set.getBarSpace() / 2f;
        }

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

    // /**
    // * enable this to force the y-axis labels to always start at zero
    // *
    // * @param enabled
    // */
    // public void setStartAtZero(boolean enabled) {
    // this.mStartAtZero = enabled;
    // prepare();
    // prepareMatrix();
    // }
    //
    // /**
    // * returns true if the chart is set to start at zero, false otherwise
    // *
    // * @return
    // */
    // @Override
    // public boolean isStartAtZeroEnabled() {
    // return mStartAtZero;
    // }

    /**
     * sets the width of the grid lines (min 0.1f, max = 3f)
     * 
     * @param width
     */
    public void setGridWidth(float width) {

        if (width < 0.1f)
            width = 0.1f;
        if (width > 3.0f)
            width = 3.0f;
        mGridWidth = width;
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
     * the chart (this does not effect dragging).
     * 
     * @param enabled
     */
    public void setScaleEnabled(boolean enabled) {
        this.mScaleEnabled = enabled;
    }

    /**
     * Returns true if scaling (zooming in and out by gesture) is enabled for
     * the chart, false if not.
     * 
     * @return
     */
    public boolean isScaleEnabled() {
        return mScaleEnabled;
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
     * set this to true to draw the border surrounding the chart, default: true
     * 
     * @param enabled
     */
    public void setDrawBorder(boolean enabled) {
        mDrawBorder = enabled;
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
     * Sets an array of positions where to draw the chart border lines (e.g. new
     * BorderStyle[] { BorderStyle.BOTTOM })
     * 
     * @param styles
     */
    public void setBorderPositions(BorderPosition[] styles) {
        mBorderPositions = styles;
    }

    /**
     * Returns the array of positions where the chart-border is drawn.
     * 
     * @return
     */
    public BorderPosition[] getBorderPositions() {
        return mBorderPositions;
    }

    /**
     * Sets the width of the border surrounding the chart in dp.
     * 
     * @param width
     */
    public void setBorderWidth(int width) {
        mBorderPaint.setStrokeWidth(Utils.convertDpToPixel(width));
    }

    /**
     * Sets the color of the border surrounding the chart.
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

        ArrayList<SelInfo> valsAtIndex = getYValsAtIndex(xIndex);

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
    public ArrayList<SelInfo> getYValsAtIndex(int xIndex) {

        ArrayList<SelInfo> vals = new ArrayList<SelInfo>();

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

        if (axis == AxisDependency.LEFT)
            mLeftAxisTransformer.pixelsToValue(pts);
        else
            mRightAxisTransformer.pixelsToValue(pts);

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

        if (axis == AxisDependency.LEFT)
            mLeftAxisTransformer.pointValuesToPixel(pts);
        else
            mRightAxisTransformer.pointValuesToPixel(pts);

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
     * Returns the left y-axis object.
     * 
     * @return
     */
    public YAxis getAxisLeft() {
        return mAxisLeft;
    }

    /**
     * Returns the right y-axis object.
     * 
     * @return
     */
    public YAxis getAxisRight() {
        return mAxisRight;
    }

    /**
     * Returns the y-axis object to the corresponding axisdependency.
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
     * returns the object representing all x-labels, this method can be used to
     * acquire the XLabels object and modify it (e.g. change the position of the
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
     * returns the filtered ChartData object depending on approximator settings,
     * current scale level and x- and y-axis ratio
     * 
     * @return
     */
    private T getFilteredData() {
        //
        // float deltaRatio = mDeltaY / mDeltaX;
        // float scaleRatio = mScaleY / mScaleX;
        //
        // // set the determined ratios
        // mApproximator.setRatios(deltaRatio, scaleRatio);
        //
        // // Log.i("Approximator", "DeltaRatio: " + deltaRatio +
        // ", ScaleRatio: "
        // // + scaleRatio);
        //
        // ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        //
        // for (int j = 0; j < mOriginalData.getDataSetCount(); j++) {
        //
        // DataSet old = mOriginalData.getDataSetByIndex(j);
        //
        // // do the filtering
        // ArrayList<Entry> approximated = mApproximator.filter(old.getYVals());
        //
        // DataSet set = new DataSet(approximated, old.getLabel());
        // dataSets.add(set);
        // }
        //
        // ChartData d = new ChartData(mOriginalData.getXVals(), dataSets);
        // return d;

        return null;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_GRID:
                break;
            case PAINT_GRID_BACKGROUND:
                mGridBackgroundPaint = p;
                break;
            case PAINT_BORDER:
                mBorderPaint = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        Paint p = super.getPaint(which);
        if (p != null)
            return p;

        switch (which) {
            case PAINT_GRID:
                return null;
            case PAINT_GRID_BACKGROUND:
                return mGridBackgroundPaint;
            case PAINT_BORDER:
                return mBorderPaint;
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
