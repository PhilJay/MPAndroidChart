
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
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarLineScatterCandleData;
import com.github.mikephil.charting.data.BarLineScatterCandleRadarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.interfaces.OnDrawListener;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.LimitLine;
import com.github.mikephil.charting.utils.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.github.mikephil.charting.utils.YLabels;
import com.github.mikephil.charting.utils.YLabels.YLabelPosition;

import java.util.ArrayList;

/**
 * Base-class of LineChart, BarChart, ScatterChart and CandleStickChart.
 * 
 * @author Philipp Jahoda
 */
@SuppressLint("RtlHardcoded")
public abstract class BarLineChartBase<T extends BarLineScatterCandleData<? extends BarLineScatterCandleRadarDataSet<? extends Entry>>>
        extends Chart<T> {

    /** if set to true, the y-axis is inverted and low values start at the top */
    private boolean mInvertYAxis = false;

    /** the maximum number of entried to which values will be drawn */
    protected int mMaxVisibleCount = 100;

    /** minimum scale value on the y-axis */
    private float mMinScaleY = 1f;

    /** minimum scale value on the x-axis */
    private float mMinScaleX = 1f;

    /** contains the current scale factor of the x-axis */
    protected float mScaleX = 1f;

    /** contains the current scale factor of the y-axis */
    protected float mScaleY = 1f;

    /** holds the maximum scale factor of the y-axis, default 10f */
    protected float mMaxScaleY = 10f;

    /** the width of the grid lines */
    protected float mGridWidth = 1f;

    /** offset that allows the chart to be dragged over its bounds on the x-axis */
    private float mTransOffsetX = 0f;

    /** offset that allows the chart to be dragged over its bounds on the x-axis */
    private float mTransOffsetY = 0f;

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

    /** if true, the y range is predefined */
    protected boolean mFixedYValues = false;

    /** if true, the y-label entries will always start at zero */
    protected boolean mStartAtZero = true;

    /** if true, data filtering is enabled */
    protected boolean mFilterData = false;

    /** paint object for the grid lines */
    protected Paint mGridPaint;

    /** paint object for the (by default) lightgrey background of the grid */
    protected Paint mGridBackgroundPaint;

    /** paint for the line surrounding the chart */
    protected Paint mBorderPaint;

    /**
     * if set to true, the highlight indicator (lines for linechart, dark bar
     * for barchart) will be drawn upon selecting values.
     */
    protected boolean mHighLightIndicatorEnabled = true;

    /** flag indicating if the vertical grid should be drawn or not */
    protected boolean mDrawVerticalGrid = true;

    /** flag indicating if the horizontal grid should be drawn or not */
    protected boolean mDrawHorizontalGrid = true;

    /** flag indicating if the y-labels should be drawn or not */
    protected boolean mDrawYLabels = true;

    /** flag indicating if the x-labels should be drawn or not */
    protected boolean mDrawXLabels = true;

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
    protected YLabels mYLabels = new YLabels();

    /** the object representing the labels on the x-axis */
    protected XLabels mXLabels = new XLabels();

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

        mListener = new BarLineChartTouchListener(this, mMatrixTouch);

        mGridPaint = new Paint();
        mGridPaint.setColor(Color.GRAY);
        mGridPaint.setStrokeWidth(mGridWidth);
        mGridPaint.setStyle(Style.STROKE);
        mGridPaint.setAlpha(90);

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
            mCurrentData = getFilteredData();

            Log.i(LOG_TAG, "FilterTime: " + (System.currentTimeMillis() -
                    starttime) + " ms");
            starttime = System.currentTimeMillis();
        } else {
            mCurrentData = getDataOriginal();
            // Log.i(LOG_TAG, "Filtering disabled.");
        }

        if (mXLabels.isAdjustXLabelsEnabled())
            calcModulus();

        // execute all drawing commands
        drawGridBackground();

        prepareYLabels();

        // make sure the graph values and grid cannot be drawn outside the
        // content-rect
        int clipRestoreCount = mDrawCanvas.save();
        mDrawCanvas.clipRect(mContentRect);

        drawHorizontalGrid();

        drawVerticalGrid();

        drawData();

        drawLimitLines();

        // if highlighting is enabled
        if (mHighlightEnabled && mHighLightIndicatorEnabled && valuesToHighlight())
            drawHighlights();

        // Removes clipping rectangle
        mDrawCanvas.restoreToCount(clipRestoreCount);

        drawAdditional();

        drawXLabels();

        drawYLabels();

        drawValues();

        drawLegend();

        drawBorder();

        drawMarkers();

        drawDescription();

        canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);

        Log.i(LOG_TAG, "DrawTime: " + (System.currentTimeMillis() - starttime) + " ms");
    }

    /**
     * does all necessary preparations, needed when data is changed or flags
     * that effect the data are changed
     */
    @Override
    public void prepare() {

        if (mDataNotSet)
            return;

        calcMinMax(mFixedYValues);

        prepareYLabels();

        prepareXLabels();

        prepareLegend();

        calculateOffsets();
    }

    /**
     * Sets up all the matrices that will be used for scaling the coordinates to
     * the display. Offset and Value-px.
     */
    private void prepareMatrix() {

        prepareMatrixValuePx();

        prepareMatrixOffset();

        Log.i(LOG_TAG, "Matrices prepared.");
    }

    /**
     * Prepares the matrix that transforms values to pixels.
     */
    private void prepareMatrixValuePx() {

        float scaleX = (float) ((getWidth() - mOffsetRight - mOffsetLeft) / mDeltaX);
        float scaleY = (float) ((getHeight() - mOffsetTop - mOffsetBottom) / mDeltaY);

        // setup all matrices
        mMatrixValueToPx.reset();
        mMatrixValueToPx.postTranslate(0, -mYChartMin);
        mMatrixValueToPx.postScale(scaleX, -scaleY);
    }

    /**
     * Prepares the matrix that contains all offsets.
     */
    private void prepareMatrixOffset() {

        mMatrixOffset.reset();

        // offset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);

        if (!mInvertYAxis)
            mMatrixOffset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);
        else {
            mMatrixOffset.setTranslate(mOffsetLeft, -mOffsetTop);
            mMatrixOffset.postScale(1.0f, -1.0f);
        }

        // mMatrixOffset.set(offset);

        // mMatrixOffset.reset();
        //
        // mMatrixOffset.postTranslate(mOffsetLeft, getHeight() -
        // mOffsetBottom);
    }

    @Override
    public void notifyDataSetChanged() {
        if (!mFixedYValues) {
            prepare();
            // prepareContentRect();
            prepareMatrixValuePx();
        } else {
            calcMinMax(mFixedYValues);
        }
    }

    @Override
    protected void calculateOffsets() {

        float legendRight = 0f, legendBottom = 0f;

        // setup offsets for legend
        if (mDrawLegend) {

            if (mLegend == null)
                return;

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

                if (mXLabels.getPosition() == XLabelPosition.TOP)
                    legendBottom = mLegendLabelPaint.getTextSize() * 3.5f;
                else {
                    legendBottom = mLegendLabelPaint.getTextSize() * 2.5f;
                }
            }

            mLegend.setOffsetBottom(legendBottom);
            mLegend.setOffsetRight(legendRight);
        }

        float yleft = 0f, yright = 0f;

        // String label = mYLabels.getFormattedLabel(mYLabels.mEntryCount - 1);
        String label = mYLabels.getLongestLabel();

        // calculate the maximum y-label width (including eventual offsets)
        float ylabelwidth = Utils.calcTextWidth(mYLabelPaint,
                label + mUnit + (mYChartMin < 0 ? "----" : "+++")); // offsets

        if (mDrawYLabels) {

            // offsets for y-labels
            if (mYLabels.getPosition() == YLabelPosition.LEFT) {

                yleft = ylabelwidth;
                mYLabelPaint.setTextAlign(Align.RIGHT);

            } else if (mYLabels.getPosition() == YLabelPosition.RIGHT) {

                yright = ylabelwidth;
                mYLabelPaint.setTextAlign(Align.LEFT);

            } else if (mYLabels.getPosition() == YLabelPosition.BOTH_SIDED) {

                yright = ylabelwidth;
                yleft = ylabelwidth;
            }
        }

        float xtop = 0f, xbottom = 0f;

        float xlabelheight = Utils.calcTextHeight(mXLabelPaint, "Q") * 2f;

        if (mDrawXLabels) {

            // offsets for x-labels
            if (mXLabels.getPosition() == XLabelPosition.BOTTOM) {

                xbottom = xlabelheight;

            } else if (mXLabels.getPosition() == XLabelPosition.TOP) {

                xtop = xlabelheight;

            } else if (mXLabels.getPosition() == XLabelPosition.BOTH_SIDED) {

                xbottom = xlabelheight;
                xtop = xlabelheight;
            }
        }

        // all required offsets are calculated, now find largest and apply
        float min = Utils.convertDpToPixel(11f);

        mOffsetBottom = Math.max(min, xbottom + legendBottom);
        mOffsetTop = Math.max(min, xtop);

        mOffsetLeft = Math.max(min, yleft);
        mOffsetRight = Math.max(min, yright + legendRight);

        // those offsets are equal for legend and other chart, just apply them
        mLegend.setOffsetTop(mOffsetTop);
        mLegend.setOffsetLeft(mOffsetLeft);

        prepareContentRect();

        prepareMatrix();
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

            if (mXLabels.getPosition() == XLabelPosition.TOP)
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
        mMatrixTouch.getValues(values);

        mXLabels.mXAxisLabelModulus = (int) Math
                .ceil((mCurrentData.getXValCount() * mXLabels.mLabelWidth)
                        / (mContentRect.width() * values[Matrix.MSCALE_X]));
    }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues); // calc min and max in the super class

        if (!fixedValues) {

            // additional handling for space (default 15% space)
            // float space = Math.abs(mDeltaY / 100f * 15f);
            float space = Math
                    .abs(Math.max(Math.abs(mYChartMax), Math.abs(mYChartMin)) / 100f * 15f);
            if (Math.abs(mYChartMax) - Math.abs(mYChartMin) < 0.00001f) {
                if (Math.abs(mYChartMax) < 10f)
                    space = 1f;
                else
                    space = Math.abs(mYChartMax / 100f * 15f);
            }

            if (mStartAtZero) {

                if (mYChartMax < 0) {
                    mYChartMax = 0;
                    // calc delta
                    mYChartMin = mYChartMin - space;
                } else {
                    mYChartMin = 0;
                    // calc delta
                    mYChartMax = mYChartMax + space;
                }
            } else {

                mYChartMin = mYChartMin - space / 2f;
                mYChartMax = mYChartMax + space / 2f;
            }
        }

        mDeltaY = Math.abs(mYChartMax - mYChartMin);
    }

    /**
     * setup the x-axis labels
     */
    protected void prepareXLabels() {

        StringBuffer a = new StringBuffer();

        int max = (int) Math.round(mCurrentData.getXValAverageLength()
                + mXLabels.getSpaceBetweenLabels());

        for (int i = 0; i < max; i++) {
            a.append("h");
        }

        mXLabels.mLabelWidth = Utils.calcTextWidth(mXLabelPaint, a.toString());
        mXLabels.mLabelHeight = Utils.calcTextHeight(mXLabelPaint, "Q");
    }

    /**
     * Sets up the y-axis labels. Computes the desired number of labels between
     * the two given extremes. Unlike the papareXLabels() method, this method
     * needs to be called upon every refresh of the view.
     * 
     * @return
     */
    private void prepareYLabels() {

        float yMin = 0f;
        float yMax = 0f;

        // calculate the starting and entry point of the y-labels (depending on
        // zoom / contentrect bounds)
        if (mContentRect.width() > 10 && !isFullyZoomedOutY()) {

            PointD p1 = getValuesByTouchPoint(mContentRect.left, mContentRect.top);
            PointD p2 = getValuesByTouchPoint(mContentRect.left, mContentRect.bottom);

            if (!mInvertYAxis) {
                yMin = (float) p2.y;
                yMax = (float) p1.y;
            } else {

                if (!mStartAtZero)
                    yMin = (float) Math.min(p1.y, p2.y);
                else
                    yMin = 0;
                yMax = (float) Math.max(p1.y, p2.y);
            }

        } else {

            if (!mInvertYAxis) {
                yMin = mYChartMin;
                yMax = mYChartMax;
            } else {

                if (!mStartAtZero)
                    yMin = (float) Math.min(mYChartMax, mYChartMin);
                else
                    yMin = 0;
                yMax = (float) Math.max(mYChartMax, mYChartMin);
            }
        }

        int labelCount = mYLabels.getLabelCount();
        double range = Math.abs(yMax - yMin);

        if (labelCount == 0 || range <= 0) {
            mYLabels.mEntries = new float[] {};
            mYLabels.mEntryCount = 0;
            return;
        }

        double rawInterval = range / labelCount;
        double interval = Utils.roundToNextSignificant(rawInterval);
        double intervalMagnitude = Math.pow(10, (int) Math.log10(interval));
        int intervalSigDigit = (int) (interval / intervalMagnitude);
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = Math.floor(10 * intervalMagnitude);
        }

        // if the labels should only show min and max
        if (mYLabels.isShowOnlyMinMaxEnabled()) {

            mYLabels.mEntryCount = 2;
            mYLabels.mEntries = new float[2];
            mYLabels.mEntries[0] = mYChartMin;
            mYLabels.mEntries[1] = mYChartMax;

        } else {

            double first = Math.ceil(yMin / interval) * interval;
            double last = Utils.nextUp(Math.floor(yMax / interval) * interval);

            double f;
            int i;
            int n = 0;
            for (f = first; f <= last; f += interval) {
                ++n;
            }

            mYLabels.mEntryCount = n;

            if (mYLabels.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                mYLabels.mEntries = new float[n];
            }

            for (f = first, i = 0; i < n; f += interval, ++i) {
                mYLabels.mEntries[i] = (float) f;
            }
        }

        if (interval < 1) {
            mYLabels.mDecimals = (int) Math.ceil(-Math.log10(interval));
        } else {
            mYLabels.mDecimals = 0;
        }
    }

    /**
     * draws the x-axis labels to the screen depending on their position
     */
    private void drawXLabels() {

        if (!mDrawXLabels)
            return;

        float yoffset = Utils.convertDpToPixel(4f);

        mXLabelPaint.setTypeface(mXLabels.getTypeface());
        mXLabelPaint.setTextSize(mXLabels.getTextSize());
        mXLabelPaint.setColor(mXLabels.getTextColor());

        if (mXLabels.getPosition() == XLabelPosition.TOP) {

            drawXLabels(getOffsetTop() - yoffset);

        } else if (mXLabels.getPosition() == XLabelPosition.BOTTOM) {

            drawXLabels(getHeight() - mOffsetBottom + mXLabels.mLabelHeight + yoffset * 1.5f);

        } else if (mXLabels.getPosition() == XLabelPosition.BOTTOM_INSIDE) {

            drawXLabels(getHeight() - getOffsetBottom() - yoffset);

        } else if (mXLabels.getPosition() == XLabelPosition.TOP_INSIDE) {

            drawXLabels(getOffsetTop() + yoffset + mXLabels.mLabelHeight);

        } else { // BOTH SIDED

            drawXLabels(getOffsetTop() - 7);
            drawXLabels(getHeight() - mOffsetBottom + mXLabels.mLabelHeight + yoffset * 1.6f);
        }
    }

    /**
     * draws the x-labels on the specified y-position
     * 
     * @param yPos
     */
    protected void drawXLabels(float yPos) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[] {
                0f, 0f
        };

        for (int i = 0; i < mCurrentData.getXValCount(); i += mXLabels.mXAxisLabelModulus) {

            position[0] = i;

            // center the text
            if (mXLabels.isCenterXLabelsEnabled())
                position[0] += 0.5f;

            transformPointArray(position);

            if (position[0] >= mOffsetLeft && position[0] <= getWidth() - mOffsetRight) {

                String label = mCurrentData.getXVals().get(i);

                if (mXLabels.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i == mCurrentData.getXValCount() - 1) {
                        float width = Utils.calcTextWidth(mXLabelPaint, label);

                        if (width > getOffsetRight() * 2 && position[0] + width > getWidth())
                            position[0] -= width / 2;

                        // avoid clipping of the first
                    } else if (i == 0) {

                        float width = Utils.calcTextWidth(mXLabelPaint, label);
                        position[0] += width / 2;
                    }
                }

                mDrawCanvas.drawText(label, position[0],
                        yPos,
                        mXLabelPaint);
            }
        }
    }

    /**
     * draws the y-axis labels to the screen
     */
    private void drawYLabels() {

        if (!mDrawYLabels)
            return;

        float[] positions = new float[mYLabels.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {
            // only fill y values, x values are not needed since the y-labels
            // are
            // static on the x-axis
            positions[i + 1] = mYLabels.mEntries[i / 2];
        }

        transformPointArray(positions);

        mYLabelPaint.setTypeface(mYLabels.getTypeface());
        mYLabelPaint.setTextSize(mYLabels.getTextSize());
        mYLabelPaint.setColor(mYLabels.getTextColor());

        float xoffset = Utils.convertDpToPixel(5f);
        float yoffset = Utils.calcTextHeight(mYLabelPaint, "A") / 2.5f;

        // determine position and draw adequately
        if (mYLabels.getPosition() == YLabelPosition.LEFT) {

            mYLabelPaint.setTextAlign(Align.RIGHT);
            drawYLabels(mOffsetLeft - xoffset, positions, yoffset);

        } else if (mYLabels.getPosition() == YLabelPosition.RIGHT) {

            mYLabelPaint.setTextAlign(Align.LEFT);
            drawYLabels(getWidth() - mOffsetRight + xoffset, positions, yoffset);

        } else if (mYLabels.getPosition() == YLabelPosition.RIGHT_INSIDE) {

            mYLabelPaint.setTextAlign(Align.RIGHT);
            drawYLabels(getWidth() - mOffsetRight - xoffset, positions, yoffset);

        } else if (mYLabels.getPosition() == YLabelPosition.LEFT_INSIDE) {

            mYLabelPaint.setTextAlign(Align.LEFT);
            drawYLabels(mOffsetLeft + xoffset, positions, yoffset);

        } else { // BOTH SIDED Y-AXIS LABELS

            // draw left legend
            mYLabelPaint.setTextAlign(Align.RIGHT);
            drawYLabels(mOffsetLeft - xoffset, positions, yoffset);

            // draw right legend
            mYLabelPaint.setTextAlign(Align.LEFT);
            drawYLabels(getWidth() - mOffsetRight + xoffset, positions, yoffset);
        }
    }

    /**
     * draws the y-labels on the specified x-position
     * 
     * @param xPos
     * @param positions
     */
    private void drawYLabels(float xPos, float[] positions, float yOffset) {

        // draw
        for (int i = 0; i < mYLabels.mEntryCount; i++) {

            String text = mYLabels.getFormattedLabel(i);

            if (!mYLabels.isDrawTopYLabelEntryEnabled() && i >= mYLabels.mEntryCount - 1)
                return;

            if (mYLabels.isDrawUnitsInYLabelEnabled()) {
                mDrawCanvas.drawText(text + mUnit, xPos, positions[i * 2 + 1] + yOffset,
                        mYLabelPaint);
            } else {
                mDrawCanvas.drawText(text, xPos, positions[i * 2 + 1] + yOffset, mYLabelPaint);
            }
        }
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

            switch (mBorderPositions[i]) {
                case LEFT:
                    mDrawCanvas.drawLine(mOffsetLeft, mOffsetTop, mOffsetLeft, getHeight()
                            - mOffsetBottom, mBorderPaint);
                    break;
                case RIGHT:
                    mDrawCanvas.drawLine(getWidth() - mOffsetRight, mOffsetTop, getWidth()
                            - mOffsetRight, getHeight()
                            - mOffsetBottom, mBorderPaint);
                    break;
                case TOP:
                    mDrawCanvas.drawLine(mOffsetLeft, mOffsetTop, getWidth() - mOffsetRight,
                            mOffsetTop, mBorderPaint);
                    break;
                case BOTTOM:
                    mDrawCanvas.drawLine(mOffsetLeft, getHeight()
                            - mOffsetBottom, getWidth() - mOffsetRight, getHeight()
                            - mOffsetBottom, mBorderPaint);
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

        Rect gridBackground = new Rect((int) mOffsetLeft + 1, (int) mOffsetTop + 1, getWidth()
                - (int) mOffsetRight,
                getHeight() - (int) mOffsetBottom);

        // draw the grid background
        mDrawCanvas.drawRect(gridBackground, mGridBackgroundPaint);
    }

    /**
     * draws the horizontal grid
     */
    protected void drawHorizontalGrid() {

        if (!mDrawHorizontalGrid)
            return;

        // pre alloc
        float[] position = new float[2];

        // draw the horizontal grid
        for (int i = 0; i < mYLabels.mEntryCount; i++) {

            position[1] = mYLabels.mEntries[i];
            transformPointArray(position);

            mDrawCanvas.drawLine(mOffsetLeft, position[1], getWidth() - mOffsetRight, position[1],
                    mGridPaint);
        }
    }

    /**
     * draws the vertical grid
     */
    protected void drawVerticalGrid() {

        if (!mDrawVerticalGrid || mCurrentData == null)
            return;

        float[] position = new float[] {
                0f, 0f
        };

        for (int i = 0; i < mCurrentData.getXValCount(); i += mXLabels.mXAxisLabelModulus) {

            position[0] = i;

            transformPointArray(position);

            if (position[0] >= mOffsetLeft && position[0] <= getWidth()) {

                mDrawCanvas.drawLine(position[0], mOffsetTop, position[0], getHeight()
                        - mOffsetBottom, mGridPaint);
            }
        }
    }

    /**
     * Draws the limit lines if there are one.
     */
    private void drawLimitLines() {

        ArrayList<LimitLine> limitLines = mOriginalData.getLimitLines();

        if (limitLines == null)
            return;
        
        float [] pts = new float[4];

        for (int i = 0; i < limitLines.size(); i++) {

            LimitLine l = limitLines.get(i);
            
            pts[1] = l.getLimit();
            pts[3] = l.getLimit();

            transformPointArray(pts);
            
            pts[0] = 0;
            pts[2] = getWidth();

            mLimitLinePaint.setColor(l.getLineColor());
            mLimitLinePaint.setPathEffect(l.getDashPathEffect());
            mLimitLinePaint.setStrokeWidth(l.getLineWidth());

            mDrawCanvas.drawLines(pts, mLimitLinePaint);

            // if drawing the limit-value is enabled
            if (l.isDrawValueEnabled()) {

                PointF pos = getPosition(new Entry(l.getLimit(), 0));

                // save text align
                Align align = mValuePaint.getTextAlign();

                float xOffset = Utils.convertDpToPixel(4f);
                float yOffset = l.getLineWidth() + xOffset;
                String label = mValueFormatter.getFormattedValue(l.getLimit());

                if (mDrawUnitInChart)
                    label += mUnit;

                if (l.getLabelPosition() == LimitLabelPosition.RIGHT) {

                    mValuePaint.setTextAlign(Align.RIGHT);
                    mDrawCanvas.drawText(label, getWidth() - mOffsetRight
                            - xOffset,
                            pos.y - yOffset, mValuePaint);

                } else {
                    mValuePaint.setTextAlign(Align.LEFT);
                    mDrawCanvas.drawText(label, mOffsetLeft
                            + xOffset,
                            pos.y - yOffset, mValuePaint);
                }

                mValuePaint.setTextAlign(align);
            }
        }
    }

    /**
     * returns true if the specified point (x-axis) exceeds the limits of what
     * is visible to the right side
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentRight(float p) {
        if (p > mContentRect.right)
            return true;
        else
            return false;
    }

    /**
     * returns true if the specified point (x-axis) exceeds the limits of what
     * is visible to the left side
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentLeft(float p) {
        if (p < mContentRect.left)
            return true;
        else
            return false;
    }

    /**
     * returns true if the specified point (y-axis) exceeds the limits of what
     * is visible on the top
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentTop(float p) {
        if (p < mContentRect.top)
            return true;
        else
            return false;
    }

    /**
     * returns true if the specified point (y-axis) exceeds the limits of what
     * is visible on the bottom
     * 
     * @param v
     * @return
     */
    protected boolean isOffContentBottom(float p) {
        if (p > mContentRect.bottom)
            return true;
        else
            return false;
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
     * Zooms in by 1.4f, x and y are the coordinates (in pixels) of the zoom
     * center.
     * 
     * @param x
     * @param y
     */
    public void zoomIn(float x, float y) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        save.postScale(1.4f, 1.4f, x, y);

        refreshTouch(save);
    }

    /**
     * Zooms out by 0.7f, x and y are the coordinates (in pixels) of the zoom
     * center.
     */
    public void zoomOut(float x, float y) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        save.postScale(0.7f, 0.7f, x, y);

        refreshTouch(save);
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

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        // Log.i(LOG_TAG, "Zooming, x: " + x + ", y: " + y);

        save.postScale(scaleX, scaleY, x, -y);

        refreshTouch(save);
    }

    /**
     * Resets all zooming and dragging and makes the chart fit exactly it's
     * bounds.
     */
    public void fitScreen() {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        float[] vals = new float[9];

        save.getValues(vals);

        // reset all translations and scaling
        vals[Matrix.MTRANS_X] = 0f;
        vals[Matrix.MTRANS_Y] = 0f;
        vals[Matrix.MSCALE_X] = 1f;
        vals[Matrix.MSCALE_Y] = 1f;

        save.setValues(vals);

        refreshTouch(save);
    }

    /**
     * If this is set to true, the y-axis is inverted which means that low
     * values are on top of the chart, high values on bottom.
     * 
     * @param enabled
     */
    public void setInvertYAxisEnabled(boolean enabled) {
        mInvertYAxis = enabled;
    }

    /**
     * If this returns true, the y-axis is inverted.
     * 
     * @return
     */
    public boolean isInvertYAxisEnabled() {
        return mInvertYAxis;
    }

    /**
     * Centers the viewport around the specified x-index and the specified
     * y-value in the chart. Centering the viewport outside the bounds of the
     * chart is not possible. Makes most sense in combination with the
     * setScaleMinima(...) method. First set the scale minima, then center the
     * viewport. SHOULD BE CALLED AFTER setting data for the chart.
     * 
     * @param xIndex the index on the x-axis to center to
     * @param yVal the value ont he y-axis to center to
     */
    public synchronized void centerViewPort(final int xIndex, final float yVal) {

        // the post makes it possible that this call waits until the view has
        // finisted setting up
        post(new Runnable() {

            @Override
            public void run() {

                float indicesInView = mDeltaX / mScaleX;
                float valsInView = mDeltaY / mScaleY;

                // Log.i(LOG_TAG, "indices: " + indicesInView + ", vals: " +
                // valsInView);

                float[] pts = new float[] {
                        xIndex - indicesInView / 2f, yVal + valsInView / 2f
                };

                Matrix save = new Matrix();
                save.set(mMatrixTouch);

                transformPointArray(pts);

                final float x = -pts[0] + getOffsetLeft();
                final float y = -pts[1] - getOffsetTop();

                save.postTranslate(x, y);

                refreshTouch(save);

                // Log.i(LOG_TAG, "ViewPort centered, xIndex: " + xIndex +
                // ", yVal: " + yVal
                // + ", transX: " + x + ", transY: " + y);
            }
        });
    }

    /**
     * call this method to refresh the graph with a given touch matrix
     * 
     * @param newTouchMatrix
     * @return
     */
    public Matrix refreshTouch(Matrix newTouchMatrix) {

        mMatrixTouch.set(newTouchMatrix);

        // make sure scale and translation are within their bounds
        limitTransAndScale(mMatrixTouch);

        // redraw
        invalidate();

        newTouchMatrix.set(mMatrixTouch);
        return newTouchMatrix;
    }

    /**
     * limits the maximum scale and X translation of the given matrix
     * 
     * @param matrix
     */
    protected void limitTransAndScale(Matrix matrix) {

        float[] vals = new float[9];
        matrix.getValues(vals);

        float curTransX = vals[Matrix.MTRANS_X];
        float curScaleX = vals[Matrix.MSCALE_X];

        float curTransY = vals[Matrix.MTRANS_Y];
        float curScaleY = vals[Matrix.MSCALE_Y];

        // Log.i(LOG_TAG, "curTransX: " + curTransX + ", curScaleX: " +
        // curScaleX);
        // Log.i(LOG_TAG, "curTransY: " + curTransY + ", curScaleY: " +
        // curScaleY);

        // min scale-x is 1f
        mScaleX = Math.max(mMinScaleX, Math.min(getMaxScaleX(), curScaleX));

        // min scale-y is 1f
        mScaleY = Math.max(mMinScaleY, Math.min(getMaxScaleY(), curScaleY));

        if (mContentRect == null)
            return;

        float maxTransX = -(float) mContentRect.width() * (mScaleX - 1f);
        float newTransX = Math.min(Math.max(curTransX, maxTransX - mTransOffsetX), mTransOffsetX);

        float maxTransY = (float) mContentRect.height() * (mScaleY - 1f);
        float newTransY = Math.max(Math.min(curTransY, maxTransY + mTransOffsetY), -mTransOffsetY);

        // Log.i(LOG_TAG, "scale-X: " + mScaleX + ", maxTransX: " + maxTransX +
        // ", newTransX: "
        // + newTransX);
        // Log.i(LOG_TAG, "scale-Y: " + mScaleY + ", maxTransY: " + maxTransY +
        // ", newTransY: "
        // + newTransY);

        vals[Matrix.MTRANS_X] = newTransX;
        vals[Matrix.MSCALE_X] = mScaleX;

        vals[Matrix.MTRANS_Y] = newTransY;
        vals[Matrix.MSCALE_Y] = mScaleY;

        matrix.setValues(vals);
    }

    /**
     * ################ ################ ################ ################
     */
    /** CODE BELOW IS GETTERS AND SETTERS */

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

    // /**
    // * set if the user should be allowed to draw onto the chart
    // *
    // * @param drawingEnabled
    // */
    // public void setDrawingEnabled(boolean drawingEnabled) {
    // if (mListener instanceof BarLineChartTouchListener) {
    // ((BarLineChartTouchListener<?>)
    // mListener).setDrawingEnabled(drawingEnabled);
    // }
    // }
    //
    // /**
    // * boolean to indicate if user drawing on chart should automatically be
    // * finished
    // */
    // protected boolean mAutoFinishDrawing;
    //
    // /**
    // * Set to true to auto finish user drawing. THis means that the value that
    // * has been drawn into the chart is filled up to the maximum x-index
    // * automatically.
    // *
    // * @param enabled
    // */
    // public void setAutoFinish(boolean enabled) {
    // this.mAutoFinishDrawing = enabled;
    // }
    //
    // /**
    // * True if auto finish user drawing is enabled
    // *
    // * @return
    // */
    // public boolean isAutoFinishEnabled() {
    // return mAutoFinishDrawing;
    // }

    /**
     * Gets the OnDrawListener. May be null.
     * 
     * @return
     */
    public OnDrawListener getDrawListener() {
        return mDrawListener;
    }

    /**
     * Sets the minimum scale values for both axes. This limits the extent to
     * which the user can zoom-out. Scale 2f means the user cannot zoom out
     * further than 2x zoom, ... Min = 1f
     * 
     * @param scaleXmin
     * @param scaleYmin
     */
    public void setScaleMinima(float scaleXmin, float scaleYmin) {

        if (scaleXmin < 1f)
            scaleXmin = 1f;
        if (scaleYmin < 1f)
            scaleYmin = 1f;

        mMinScaleX = scaleXmin;
        mMinScaleY = scaleYmin;

        zoom(mMinScaleX, mMinScaleY, 0f, 0f);
    }

    /**
     * Sets the effective range of y-values the chart can display. If this is
     * set, the y-range is fixed and cannot be changed. This means, no
     * recalculation of the bounds of the chart concerning the y-axis will be
     * done when adding new data. To disable this, provide Float.NaN as a
     * parameter or call resetYRange();
     * 
     * @param minY
     * @param maxY
     * @param invalidate if set to true, the chart will redraw itself after
     *            calling this method
     */
    public void setYRange(float minY, float maxY, boolean invalidate) {

        if (Float.isNaN(minY) || Float.isNaN(maxY)) {
            resetYRange(invalidate);
            return;
        }

        mFixedYValues = true;

        mYChartMin = minY;
        mYChartMax = maxY;
        if (minY < 0) {
            mStartAtZero = false;
        }
        mDeltaY = mYChartMax - mYChartMin;

        calcFormats();
        prepareMatrix();
        if (invalidate)
            invalidate();
    }

    /**
     * Resets the previously set y range. If new data is added, the y-range will
     * be recalculated.
     * 
     * @param invalidate if set to true, the chart will redraw itself after
     *            calling this method
     */
    public void resetYRange(boolean invalidate) {
        mFixedYValues = false;
        calcMinMax(mFixedYValues);

        prepareMatrix();
        if (invalidate)
            invalidate();
    }

    /**
     * if this returns true, the chart has a fixed range on the y-axis that is
     * not dependant on the actual data in the chart
     * 
     * @return
     */
    public boolean hasFixedYValues() {
        return mFixedYValues;
    }

    /**
     * Returns the position (in pixels) the provided Entry has inside the chart
     * view or null, if the provided Entry is null.
     * 
     * @param e
     * @return
     */
    public PointF getPosition(Entry e) {

        if (e == null)
            return null;

        float[] vals = new float[] {
                e.getXIndex(), e.getVal()
        };

        if (this instanceof BarChart) {

            BarDataSet set = (BarDataSet) mOriginalData.getDataSetForEntry(e);
            if (set != null)
                vals[0] += set.getBarSpace() / 2f;
        }

        transformPointArray(vals);

        return new PointF(vals[0], vals[1]);
    }

    /**
     * sets the color for the grid lines
     * 
     * @param color
     */
    public void setGridColor(int color) {
        mGridPaint.setColor(color);
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
     * enable this to force the y-axis labels to always start at zero
     * 
     * @param enabled
     */
    public void setStartAtZero(boolean enabled) {
        this.mStartAtZero = enabled;
        prepare();
        prepareMatrix();
    }

    /**
     * returns true if the chart is set to start at zero, false otherwise
     * 
     * @return
     */
    public boolean isStartAtZeroEnabled() {
        return mStartAtZero;
    }

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
     * if set to true, the vertical grid will be drawn, default: true
     * 
     * @param enabled
     */
    public void setDrawVerticalGrid(boolean enabled) {
        mDrawVerticalGrid = enabled;
    }

    /**
     * if set to true, the horizontal grid will be drawn, default: true
     * 
     * @param enabled
     */
    public void setDrawHorizontalGrid(boolean enabled) {
        mDrawHorizontalGrid = enabled;
    }

    /**
     * returns true if drawing the vertical grid is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawVerticalGridEnabled() {
        return mDrawVerticalGrid;
    }

    /**
     * returns true if drawing the horizontal grid is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawHorizontalGridEnabled() {
        return mDrawHorizontalGrid;
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
     * set this to true to enable drawing the x-labels, false if not
     * 
     * @param enabled
     */
    public void setDrawXLabels(boolean enabled) {
        mDrawXLabels = enabled;
    }

    /**
     * set this to true to enable drawing the y-labels, false if not
     * 
     * @param enabled
     */
    public void setDrawYLabels(boolean enabled) {
        mDrawYLabels = enabled;
    }

    /**
     * Returns true if drawing y-labels is enabled, false if not.
     * 
     * @return
     */
    public boolean isDrawYLabelsEnabled() {
        return mDrawYLabels;
    }

    /**
     * Returns true if drawing x-labels is enabled, false if not.
     * 
     * @return
     */
    public boolean isDrawXLabelsEnabled() {
        return mDrawXLabels;
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
     * Returns the Highlight object (contains x-index and DataSet index) of the
     * selected value at the given touch point inside the Line-, Scatter-, or
     * CandleStick-Chart.
     * 
     * @param x
     * @param y
     * @return
     */
    public Highlight getHighlightByTouchPoint(float x, float y) {

        if (mDataNotSet || mCurrentData == null) {
            Log.e(LOG_TAG, "Can't select by touch. No data set.");
            return null;
        }

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;
        pts[1] = y;

        Matrix tmp = new Matrix();

        // invert all matrixes to convert back to the original value
        mMatrixOffset.invert(tmp);
        tmp.mapPoints(pts);

        mMatrixTouch.invert(tmp);
        tmp.mapPoints(pts);

        mMatrixValueToPx.invert(tmp);
        tmp.mapPoints(pts);

        double xTouchVal = pts[0];
        double yTouchVal = pts[1];
        double base = Math.floor(xTouchVal);

        double touchOffset = mDeltaX * 0.025;
        // Log.i(LOG_TAG, "touchindex x: " + xTouchVal + ", touchindex y: " +
        // yTouchVal + ", offset: "
        // + touchOffset);
        // Toast.makeText(getContext(), "touchindex x: " + xTouchVal +
        // ", touchindex y: " + yTouchVal + ", offset: " + touchOffset,
        // Toast.LENGTH_SHORT).show();

        // touch out of chart
        if (xTouchVal < -touchOffset || xTouchVal > mDeltaX + touchOffset)
            return null;

        if (this instanceof CandleStickChart)
            base -= 0.5;

        if (base < 0)
            base = 0;
        if (base >= mDeltaX)
            base = mDeltaX - 1;

        int xIndex = (int) base;

        int dataSetIndex = 0; // index of the DataSet inside the ChartData
                              // object

        // check if we are more than half of a x-value or not
        if (xTouchVal - base > 0.5) {
            xIndex = (int) base + 1;
        }

        ArrayList<SelInfo> valsAtIndex = getYValsAtIndex(xIndex);

        dataSetIndex = Utils.getClosestDataSetIndex(valsAtIndex, (float) yTouchVal);

        if (dataSetIndex == -1)
            return null;

        // Toast.makeText(getContext(), "xindex: " + xIndex + ", dataSetIndex: "
        // + dataSetIndex,
        // Toast.LENGTH_SHORT).show();

        return new Highlight(xIndex, dataSetIndex);
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
    public PointD getValuesByTouchPoint(float x, float y) {

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;
        pts[1] = y;

        Matrix tmp = new Matrix();

        // invert all matrixes to convert back to the original value
        mMatrixOffset.invert(tmp);
        tmp.mapPoints(pts);

        mMatrixTouch.invert(tmp);
        tmp.mapPoints(pts);

        mMatrixValueToPx.invert(tmp);
        tmp.mapPoints(pts);

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
    public PointD getPixelsForValues(float x, float y) {

        float[] pts = new float[] {
                x, y
        };

        transformPointArray(pts);

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
    public float getYValueByTouchPoint(float x, float y) {
        return (float) getValuesByTouchPoint(x, y).y;
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
            return mCurrentData.getEntryForHighlight(h);
        }
        return null;
    }

    /**
     * returns the current x-scale factor
     */
    public float getScaleX() {
        return mScaleX;
    }

    /**
     * returns the current y-scale factor
     */
    public float getScaleY() {
        return mScaleY;
    }

    /**
     * if the chart is fully zoomed out, return true
     * 
     * @return
     */
    public boolean isFullyZoomedOut() {

        if (isFullyZoomedOutX() && isFullyZoomedOutY())
            return true;
        else
            return false;
    }

    /**
     * Returns true if the chart is fully zoomed out on it's y-axis (vertical).
     * 
     * @return
     */
    public boolean isFullyZoomedOutY() {
        if (mScaleY <= mMinScaleY)
            return true;
        else
            return false;
    }

    /**
     * Returns true if the chart is fully zoomed out on it's x-axis
     * (horizontal).
     * 
     * @return
     */
    public boolean isFullyZoomedOutX() {
        if (mScaleX <= mMinScaleX)
            return true;
        else
            return false;
    }

    /**
     * calcualtes the maximum x-scale value depending on the number of x-values,
     * maximum scale is numberOfXvals / 2
     * 
     * @return
     */
    public float getMaxScaleX() {
        return mDeltaX / 2f;
    }

    /**
     * Returns the maximum y-scale factor. Default 10f
     * 
     * @return
     */
    public float getMaxScaleY() {
        return mMaxScaleY;
    }

    /**
     * sets the maximum scale factor for the y-axis. Default 7f, min 1f, max 20f
     * 
     * @param factor
     */
    public void setMaxScaleY(float factor) {

        if (factor < 1f)
            factor = 1f;
        if (factor > 20f)
            factor = 20f;

        mMaxScaleY = factor;
    }

    /**
     * returns the object representing all y-labels, this method can be used to
     * acquire the YLabels object and modify it (e.g. change the position of the
     * labels)
     * 
     * @return
     */
    public YLabels getYLabels() {
        return mYLabels;
    }

    /**
     * returns the object representing all x-labels, this method can be used to
     * acquire the XLabels object and modify it (e.g. change the position of the
     * labels)
     * 
     * @return
     */
    public XLabels getXLabels() {
        return mXLabels;
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
        mTransOffsetX = Utils.convertDpToPixel(offset);
    }

    /**
     * Set an offset in dp that allows the user to drag the chart over it's
     * bounds on the y-axis.
     * 
     * @param offset
     */
    public void setDragOffsetY(float offset) {
        mTransOffsetY = Utils.convertDpToPixel(offset);
    }

    /**
     * Returns true if both drag offsets (x and y) are zero or smaller.
     * 
     * @return
     */
    public boolean hasNoDragOffset() {
        return mTransOffsetX <= 0 && mTransOffsetY <= 0 ? true : false;
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
                mGridPaint = p;
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
                return mGridPaint;
            case PAINT_GRID_BACKGROUND:
                return mGridBackgroundPaint;
            case PAINT_BORDER:
                return mBorderPaint;
        }

        return null;
    }
}
