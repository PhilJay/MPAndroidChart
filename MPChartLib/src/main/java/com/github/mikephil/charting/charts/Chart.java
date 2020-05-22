package com.github.mikephil.charting.charts;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore.Images;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.animation.Easing.EasingFunction;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.ChartHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.IHighlighter;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.LegendRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Baseclass of all Chart-Views.
 *
 * @author Philipp Jahoda
 */
public abstract class Chart<T extends ChartData<? extends IDataSet<? extends Entry>>> extends
        ViewGroup
        implements ChartInterface {

    public static final String LOG_TAG = "MPAndroidChart";

    /**
     * flag that indicates if logging is enabled or not
     */
    protected boolean mLogEnabled = false;

    /**
     * object that holds all data that was originally set for the chart, before
     * it was modified or any filtering algorithms had been applied
     */
    protected T mData = null;

    /**
     * Flag that indicates if highlighting per tap (touch) is enabled
     */
    protected boolean mHighLightPerTapEnabled = true;

    /**
     * If set to true, chart continues to scroll after touch up
     */
    private boolean mDragDecelerationEnabled = true;

    /**
     * Deceleration friction coefficient in [0 ; 1] interval, higher values
     * indicate that speed will decrease slowly, for example if it set to 0, it
     * will stop immediately. 1 is an invalid value, and will be converted to
     * 0.999f automatically.
     */
    private float mDragDecelerationFrictionCoef = 0.9f;

    /**
     * default value-formatter, number of digits depends on provided chart-data
     */
    protected DefaultValueFormatter mDefaultValueFormatter = new DefaultValueFormatter(0);

    /**
     * paint object used for drawing the description text in the bottom right
     * corner of the chart
     */
    protected Paint mDescPaint;

    /**
     * paint object for drawing the information text when there are no values in
     * the chart
     */
    protected Paint mInfoPaint;

    /**
     * the object representing the labels on the x-axis
     */
    protected XAxis mXAxis;

    /**
     * if true, touch gestures are enabled on the chart
     */
    protected boolean mTouchEnabled = true;

    /**
     * the object responsible for representing the description text
     */
    protected Description mDescription;

    /**
     * the legend object containing all data associated with the legend
     */
    protected Legend mLegend;

    /**
     * listener that is called when a value on the chart is selected
     */
    protected OnChartValueSelectedListener mSelectionListener;

    protected ChartTouchListener mChartTouchListener;

    /**
     * text that is displayed when the chart is empty
     */
    private String mNoDataText = "No chart data available.";

    /**
     * Gesture listener for custom callbacks when making gestures on the chart.
     */
    private OnChartGestureListener mGestureListener;

    protected LegendRenderer mLegendRenderer;

    /**
     * object responsible for rendering the data
     */
    protected DataRenderer mRenderer;

    protected IHighlighter mHighlighter;

    /**
     * object that manages the bounds and drawing constraints of the chart
     */
    protected ViewPortHandler mViewPortHandler = new ViewPortHandler();

    /**
     * object responsible for animations
     */
    protected ChartAnimator mAnimator;

    /**
     * Extra offsets to be appended to the viewport
     */
    private float mExtraTopOffset = 0.f,
            mExtraRightOffset = 0.f,
            mExtraBottomOffset = 0.f,
            mExtraLeftOffset = 0.f;

    /**
     * default constructor for initialization in code
     */
    public Chart(Context context) {
        super(context);
        init();
    }

    /**
     * constructor for initialization in xml
     */
    public Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * even more awesome constructor
     */
    public Chart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * initialize all paints and stuff
     */
    protected void init() {

        setWillNotDraw(false);
        // setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mAnimator = new ChartAnimator(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // ViewCompat.postInvalidateOnAnimation(Chart.this);
                postInvalidate();
            }
        });

        // initialize the utils
        Utils.init(getContext());
        mMaxHighlightDistance = Utils.convertDpToPixel(500f);

        mDescription = new Description();
        mLegend = new Legend();

        mLegendRenderer = new LegendRenderer(mViewPortHandler, mLegend);

        mXAxis = new XAxis();

        mDescPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInfoPaint.setColor(Color.rgb(247, 189, 51)); // orange
        mInfoPaint.setTextAlign(Align.CENTER);
        mInfoPaint.setTextSize(Utils.convertDpToPixel(12f));

        if (mLogEnabled)
            Log.i("", "Chart.init()");
    }

    // public void initWithDummyData() {
    // ColorTemplate template = new ColorTemplate();
    // template.addColorsForDataSets(ColorTemplate.COLORFUL_COLORS,
    // getContext());
    //
    // setColorTemplate(template);
    // setDrawYValues(false);
    //
    // ArrayList<String> xVals = new ArrayList<String>();
    // Calendar calendar = Calendar.getInstance();
    // for (int i = 0; i < 12; i++) {
    // xVals.add(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
    // Locale.getDefault()));
    // }
    //
    // ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
    // for (int i = 0; i < 3; i++) {
    //
    // ArrayList<Entry> yVals = new ArrayList<Entry>();
    //
    // for (int j = 0; j < 12; j++) {
    // float val = (float) (Math.random() * 100);
    // yVals.add(new Entry(val, j));
    // }
    //
    // DataSet set = new DataSet(yVals, "DataSet " + i);
    // dataSets.add(set); // add the datasets
    // }
    // // create a data object with the datasets
    // ChartData data = new ChartData(xVals, dataSets);
    // setData(data);
    // invalidate();
    // }

    /**
     * Sets a new data object for the chart. The data object contains all values
     * and information needed for displaying.
     *
     * @param data
     */
    public void setData(T data) {

        mData = data;
        mOffsetsCalculated = false;

        if (data == null) {
            return;
        }

        // calculate how many digits are needed
        setupDefaultFormatter(data.getYMin(), data.getYMax());

        for (IDataSet set : mData.getDataSets()) {
            if (set.needsFormatter() || set.getValueFormatter() == mDefaultValueFormatter)
                set.setValueFormatter(mDefaultValueFormatter);
        }

        // let the chart know there is new data
        notifyDataSetChanged();

        if (mLogEnabled)
            Log.i(LOG_TAG, "Data is set.");
    }

    /**
     * Clears the chart from all data (sets it to null) and refreshes it (by
     * calling invalidate()).
     */
    public void clear() {
        mData = null;
        mOffsetsCalculated = false;
        mIndicesToHighlight = null;
        mChartTouchListener.setLastHighlighted(null);
        invalidate();
    }

    /**
     * Removes all DataSets (and thereby Entries) from the chart. Does not set the data object to null. Also refreshes the
     * chart by calling invalidate().
     */
    public void clearValues() {
        mData.clearValues();
        invalidate();
    }

    /**
     * Returns true if the chart is empty (meaning it's data object is either
     * null or contains no entries).
     *
     * @return
     */
    public boolean isEmpty() {

        if (mData == null)
            return true;
        else {

            if (mData.getEntryCount() <= 0)
                return true;
            else
                return false;
        }
    }

    /**
     * Lets the chart know its underlying data has changed and performs all
     * necessary recalculations. It is crucial that this method is called
     * everytime data is changed dynamically. Not calling this method can lead
     * to crashes or unexpected behaviour.
     */
    public abstract void notifyDataSetChanged();

    /**
     * Calculates the offsets of the chart to the border depending on the
     * position of an eventual legend or depending on the length of the y-axis
     * and x-axis labels and their position
     */
    protected abstract void calculateOffsets();

    /**
     * Calculates the y-min and y-max value and the y-delta and x-delta value
     */
    protected abstract void calcMinMax();

    /**
     * Calculates the required number of digits for the values that might be
     * drawn in the chart (if enabled), and creates the default-value-formatter
     */
    protected void setupDefaultFormatter(float min, float max) {

        float reference = 0f;

        if (mData == null || mData.getEntryCount() < 2) {

            reference = Math.max(Math.abs(min), Math.abs(max));
        } else {
            reference = Math.abs(max - min);
        }

        int digits = Utils.getDecimals(reference);

        // setup the formatter with a new number of digits
        mDefaultValueFormatter.setup(digits);
    }

    /**
     * flag that indicates if offsets calculation has already been done or not
     */
    private boolean mOffsetsCalculated = false;

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        if (mData == null) {

            boolean hasText = !TextUtils.isEmpty(mNoDataText);

            if (hasText) {
                MPPointF pt = getCenter();

                switch (mInfoPaint.getTextAlign()) {
                    case LEFT:
                        pt.x = 0;
                        canvas.drawText(mNoDataText, pt.x, pt.y, mInfoPaint);
                        break;

                    case RIGHT:
                        pt.x *= 2.0;
                        canvas.drawText(mNoDataText, pt.x, pt.y, mInfoPaint);
                        break;

                    default:
                        canvas.drawText(mNoDataText, pt.x, pt.y, mInfoPaint);
                        break;
                }
            }

            return;
        }

        if (!mOffsetsCalculated) {

            calculateOffsets();
            mOffsetsCalculated = true;
        }
    }

    /**
     * Draws the description text in the bottom right corner of the chart (per default)
     */
    protected void drawDescription(Canvas c) {

        // check if description should be drawn
        if (mDescription != null && mDescription.isEnabled()) {

            MPPointF position = mDescription.getPosition();

            mDescPaint.setTypeface(mDescription.getTypeface());
            mDescPaint.setTextSize(mDescription.getTextSize());
            mDescPaint.setColor(mDescription.getTextColor());
            mDescPaint.setTextAlign(mDescription.getTextAlign());

            float x, y;

            // if no position specified, draw on default position
            if (position == null) {
                x = getWidth() - mViewPortHandler.offsetRight() - mDescription.getXOffset();
                y = getHeight() - mViewPortHandler.offsetBottom() - mDescription.getYOffset();
            } else {
                x = position.x;
                y = position.y;
            }

            c.drawText(mDescription.getText(), x, y, mDescPaint);
        }
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS CODE FOR HIGHLIGHTING */

    /**
     * array of Highlight objects that reference the highlighted slices in the
     * chart
     */
    protected Highlight[] mIndicesToHighlight;

    /**
     * The maximum distance in dp away from an entry causing it to highlight.
     */
    protected float mMaxHighlightDistance = 0f;

    @Override
    public float getMaxHighlightDistance() {
        return mMaxHighlightDistance;
    }

    /**
     * Sets the maximum distance in screen dp a touch can be away from an entry to cause it to get highlighted.
     * Default: 500dp
     *
     * @param distDp
     */
    public void setMaxHighlightDistance(float distDp) {
        mMaxHighlightDistance = Utils.convertDpToPixel(distDp);
    }

    /**
     * Returns the array of currently highlighted values. This might a null or
     * empty array if nothing is highlighted.
     *
     * @return
     */
    public Highlight[] getHighlighted() {
        return mIndicesToHighlight;
    }

    /**
     * Returns true if values can be highlighted via tap gesture, false if not.
     *
     * @return
     */
    public boolean isHighlightPerTapEnabled() {
        return mHighLightPerTapEnabled;
    }

    /**
     * Set this to false to prevent values from being highlighted by tap gesture.
     * Values can still be highlighted via drag or programmatically. Default: true
     *
     * @param enabled
     */
    public void setHighlightPerTapEnabled(boolean enabled) {
        mHighLightPerTapEnabled = enabled;
    }

    /**
     * Returns true if there are values to highlight, false if there are no
     * values to highlight. Checks if the highlight array is null, has a length
     * of zero or if the first object is null.
     *
     * @return
     */
    public boolean valuesToHighlight() {
        return mIndicesToHighlight == null || mIndicesToHighlight.length <= 0
                || mIndicesToHighlight[0] == null ? false
                : true;
    }

    /**
     * Sets the last highlighted value for the touchlistener.
     *
     * @param highs
     */
    protected void setLastHighlighted(Highlight[] highs) {

        if (highs == null || highs.length <= 0 || highs[0] == null) {
            mChartTouchListener.setLastHighlighted(null);
        } else {
            mChartTouchListener.setLastHighlighted(highs[0]);
        }
    }

    /**
     * Highlights the values at the given indices in the given DataSets. Provide
     * null or an empty array to undo all highlighting. This should be used to
     * programmatically highlight values.
     * This method *will not* call the listener.
     *
     * @param highs
     */
    public void highlightValues(Highlight[] highs) {

        // set the indices to highlight
        mIndicesToHighlight = highs;

        setLastHighlighted(highs);

        // redraw the chart
        invalidate();
    }

    /**
     * Highlights any y-value at the given x-value in the given DataSet.
     * Provide -1 as the dataSetIndex to undo all highlighting.
     * This method will call the listener.
     * @param x The x-value to highlight
     * @param dataSetIndex The dataset index to search in
     * @param dataIndex The data index to search in (only used in CombinedChartView currently)
     */
    public void highlightValue(float x, int dataSetIndex, int dataIndex) {
        highlightValue(x, dataSetIndex, dataIndex, true);
    }

    /**
     * Highlights any y-value at the given x-value in the given DataSet.
     * Provide -1 as the dataSetIndex to undo all highlighting.
     * This method will call the listener.
     * @param x The x-value to highlight
     * @param dataSetIndex The dataset index to search in
     */
    public void highlightValue(float x, int dataSetIndex) {
        highlightValue(x, dataSetIndex, -1, true);
    }

    /**
     * Highlights the value at the given x-value and y-value in the given DataSet.
     * Provide -1 as the dataSetIndex to undo all highlighting.
     * This method will call the listener.
     * @param x The x-value to highlight
     * @param y The y-value to highlight. Supply `NaN` for "any"
     * @param dataSetIndex The dataset index to search in
     * @param dataIndex The data index to search in (only used in CombinedChartView currently)
     */
    public void highlightValue(float x, float y, int dataSetIndex, int dataIndex) {
        highlightValue(x, y, dataSetIndex, dataIndex, true);
    }

    /**
     * Highlights the value at the given x-value and y-value in the given DataSet.
     * Provide -1 as the dataSetIndex to undo all highlighting.
     * This method will call the listener.
     * @param x The x-value to highlight
     * @param y The y-value to highlight. Supply `NaN` for "any"
     * @param dataSetIndex The dataset index to search in
     */
    public void highlightValue(float x, float y, int dataSetIndex) {
        highlightValue(x, y, dataSetIndex, -1, true);
    }

    /**
     * Highlights any y-value at the given x-value in the given DataSet.
     * Provide -1 as the dataSetIndex to undo all highlighting.
     * @param x The x-value to highlight
     * @param dataSetIndex The dataset index to search in
     * @param dataIndex The data index to search in (only used in CombinedChartView currently)
     * @param callListener Should the listener be called for this change
     */
    public void highlightValue(float x, int dataSetIndex, int dataIndex, boolean callListener) {
        highlightValue(x, Float.NaN, dataSetIndex, dataIndex, callListener);
    }

    /**
     * Highlights any y-value at the given x-value in the given DataSet.
     * Provide -1 as the dataSetIndex to undo all highlighting.
     * @param x The x-value to highlight
     * @param dataSetIndex The dataset index to search in
     * @param callListener Should the listener be called for this change
     */
    public void highlightValue(float x, int dataSetIndex, boolean callListener) {
        highlightValue(x, Float.NaN, dataSetIndex, -1, callListener);
    }

    /**
     * Highlights any y-value at the given x-value in the given DataSet.
     * Provide -1 as the dataSetIndex to undo all highlighting.
     * @param x The x-value to highlight
     * @param y The y-value to highlight. Supply `NaN` for "any"
     * @param dataSetIndex The dataset index to search in
     * @param dataIndex The data index to search in (only used in CombinedChartView currently)
     * @param callListener Should the listener be called for this change
     */
    public void highlightValue(float x, float y, int dataSetIndex, int dataIndex, boolean callListener) {

        if (dataSetIndex < 0 || dataSetIndex >= mData.getDataSetCount()) {
            highlightValue(null, callListener);
        } else {
            highlightValue(new Highlight(x, y, dataSetIndex, dataIndex), callListener);
        }
    }

    /**
     * Highlights any y-value at the given x-value in the given DataSet.
     * Provide -1 as the dataSetIndex to undo all highlighting.
     * @param x The x-value to highlight
     * @param y The y-value to highlight. Supply `NaN` for "any"
     * @param dataSetIndex The dataset index to search in
     * @param callListener Should the listener be called for this change
     */
    public void highlightValue(float x, float y, int dataSetIndex, boolean callListener) {
        highlightValue(x, y, dataSetIndex, -1, callListener);
    }

    /**
     * Highlights the values represented by the provided Highlight object
     * This method *will not* call the listener.
     *
     * @param highlight contains information about which entry should be highlighted
     */
    public void highlightValue(Highlight highlight) {
        highlightValue(highlight, false);
    }

    /**
     * Highlights the value selected by touch gesture. Unlike
     * highlightValues(...), this generates a callback to the
     * OnChartValueSelectedListener.
     *
     * @param high         - the highlight object
     * @param callListener - call the listener
     */
    public void highlightValue(Highlight high, boolean callListener) {

        Entry e = null;

        if (high == null)
            mIndicesToHighlight = null;
        else {

            if (mLogEnabled)
                Log.i(LOG_TAG, "Highlighted: " + high.toString());

            e = mData.getEntryForHighlight(high);
            if (e == null) {
                mIndicesToHighlight = null;
                high = null;
            } else {

                // set the indices to highlight
                mIndicesToHighlight = new Highlight[]{
                        high
                };
            }
        }

        setLastHighlighted(mIndicesToHighlight);

        if (callListener && mSelectionListener != null) {

            if (!valuesToHighlight())
                mSelectionListener.onNothingSelected();
            else {
                // notify the listener
                mSelectionListener.onValueSelected(e, high);
            }
        }

        // redraw the chart
        invalidate();
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

        if (mData == null) {
            Log.e(LOG_TAG, "Can't select by touch. No data set.");
            return null;
        } else
            return getHighlighter().getHighlight(x, y);
    }

    /**
     * Set a new (e.g. custom) ChartTouchListener NOTE: make sure to
     * setTouchEnabled(true); if you need touch gestures on the chart
     *
     * @param l
     */
    public void setOnTouchListener(ChartTouchListener l) {
        this.mChartTouchListener = l;
    }

    /**
     * Returns an instance of the currently active touch listener.
     *
     * @return
     */
    public ChartTouchListener getOnTouchListener() {
        return mChartTouchListener;
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW CODE IS FOR THE MARKER VIEW */

    /**
     * if set to true, the marker view is drawn when a value is clicked
     */
    protected boolean mDrawMarkers = true;

    /**
     * the view that represents the marker
     */
    protected IMarker mMarker;

    /**
     * draws all MarkerViews on the highlighted positions
     */
    protected void drawMarkers(Canvas canvas) {

        // if there is no marker view or drawing marker is disabled
        if (mMarker == null || !isDrawMarkersEnabled() || !valuesToHighlight())
            return;

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];

            IDataSet set = mData.getDataSetByIndex(highlight.getDataSetIndex());

            Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);
            int entryIndex = set.getEntryIndex(e);

            // make sure entry not null
            if (e == null || entryIndex > set.getEntryCount() * mAnimator.getPhaseX())
                continue;

            float[] pos = getMarkerPosition(highlight);

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                continue;

            // callbacks to update the content
            mMarker.refreshContent(e, highlight);

            // draw the marker
            mMarker.draw(canvas, pos[0], pos[1]);
        }
    }

    /**
     * Returns the actual position in pixels of the MarkerView for the given
     * Highlight object.
     *
     * @param high
     * @return
     */
    protected float[] getMarkerPosition(Highlight high) {
        return new float[]{high.getDrawX(), high.getDrawY()};
    }

    /**
     * ################ ################ ################ ################
     * ANIMATIONS ONLY WORK FOR API LEVEL 11 (Android 3.0.x) AND HIGHER.
     */
    /** CODE BELOW THIS RELATED TO ANIMATION */

    /**
     * Returns the animator responsible for animating chart values.
     *
     * @return
     */
    public ChartAnimator getAnimator() {
        return mAnimator;
    }

    /**
     * If set to true, chart continues to scroll after touch up default: true
     */
    public boolean isDragDecelerationEnabled() {
        return mDragDecelerationEnabled;
    }

    /**
     * If set to true, chart continues to scroll after touch up. Default: true.
     *
     * @param enabled
     */
    public void setDragDecelerationEnabled(boolean enabled) {
        mDragDecelerationEnabled = enabled;
    }

    /**
     * Returns drag deceleration friction coefficient
     *
     * @return
     */
    public float getDragDecelerationFrictionCoef() {
        return mDragDecelerationFrictionCoef;
    }

    /**
     * Deceleration friction coefficient in [0 ; 1] interval, higher values
     * indicate that speed will decrease slowly, for example if it set to 0, it
     * will stop immediately. 1 is an invalid value, and will be converted to
     * 0.999f automatically.
     *
     * @param newValue
     */
    public void setDragDecelerationFrictionCoef(float newValue) {

        if (newValue < 0.f)
            newValue = 0.f;

        if (newValue >= 1f)
            newValue = 0.999f;

        mDragDecelerationFrictionCoef = newValue;
    }

    /**
     * ################ ################ ################ ################
     * ANIMATIONS ONLY WORK FOR API LEVEL 11 (Android 3.0.x) AND HIGHER.
     */
    /** CODE BELOW FOR PROVIDING EASING FUNCTIONS */

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart. ANIMATIONS
     * ONLY WORK FOR API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillisX
     * @param durationMillisY
     * @param easingX         a custom easing function to be used on the animation phase
     * @param easingY         a custom easing function to be used on the animation phase
     */
    @RequiresApi(11)
    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easingX,
                          EasingFunction easingY) {
        mAnimator.animateXY(durationMillisX, durationMillisY, easingX, easingY);
    }

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart. ANIMATIONS
     * ONLY WORK FOR API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillisX
     * @param durationMillisY
     * @param easing         a custom easing function to be used on the animation phase
     */
    @RequiresApi(11)
    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easing) {
        mAnimator.animateXY(durationMillisX, durationMillisY, easing);
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillis
     * @param easing         a custom easing function to be used on the animation phase
     */
    @RequiresApi(11)
    public void animateX(int durationMillis, EasingFunction easing) {
        mAnimator.animateX(durationMillis, easing);
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillis
     * @param easing         a custom easing function to be used on the animation phase
     */
    @RequiresApi(11)
    public void animateY(int durationMillis, EasingFunction easing) {
        mAnimator.animateY(durationMillis, easing);
    }

    /**
     * ################ ################ ################ ################
     * ANIMATIONS ONLY WORK FOR API LEVEL 11 (Android 3.0.x) AND HIGHER.
     */
    /** CODE BELOW FOR PREDEFINED EASING OPTIONS */

    /**
     * ################ ################ ################ ################
     * ANIMATIONS ONLY WORK FOR API LEVEL 11 (Android 3.0.x) AND HIGHER.
     */
    /** CODE BELOW FOR ANIMATIONS WITHOUT EASING */

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillis
     */
    @RequiresApi(11)
    public void animateX(int durationMillis) {
        mAnimator.animateX(durationMillis);
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillis
     */
    @RequiresApi(11)
    public void animateY(int durationMillis) {
        mAnimator.animateY(durationMillis);
    }

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart. ANIMATIONS
     * ONLY WORK FOR API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillisX
     * @param durationMillisY
     */
    @RequiresApi(11)
    public void animateXY(int durationMillisX, int durationMillisY) {
        mAnimator.animateXY(durationMillisX, durationMillisY);
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS ONLY GETTERS AND SETTERS */


    /**
     * Returns the object representing all x-labels, this method can be used to
     * acquire the XAxis object and modify it (e.g. change the position of the
     * labels, styling, etc.)
     *
     * @return
     */
    public XAxis getXAxis() {
        return mXAxis;
    }

    /**
     * Returns the default IValueFormatter that has been determined by the chart
     * considering the provided minimum and maximum values.
     *
     * @return
     */
    public ValueFormatter getDefaultValueFormatter() {
        return mDefaultValueFormatter;
    }

    /**
     * set a selection listener for the chart
     *
     * @param l
     */
    public void setOnChartValueSelectedListener(OnChartValueSelectedListener l) {
        this.mSelectionListener = l;
    }

    /**
     * Sets a gesture-listener for the chart for custom callbacks when executing
     * gestures on the chart surface.
     *
     * @param l
     */
    public void setOnChartGestureListener(OnChartGestureListener l) {
        this.mGestureListener = l;
    }

    /**
     * Returns the custom gesture listener.
     *
     * @return
     */
    public OnChartGestureListener getOnChartGestureListener() {
        return mGestureListener;
    }

    /**
     * returns the current y-max value across all DataSets
     *
     * @return
     */
    public float getYMax() {
        return mData.getYMax();
    }

    /**
     * returns the current y-min value across all DataSets
     *
     * @return
     */
    public float getYMin() {
        return mData.getYMin();
    }

    @Override
    public float getXChartMax() {
        return mXAxis.mAxisMaximum;
    }

    @Override
    public float getXChartMin() {
        return mXAxis.mAxisMinimum;
    }

    @Override
    public float getXRange() {
        return mXAxis.mAxisRange;
    }

    /**
     * Returns a recyclable MPPointF instance.
     * Returns the center point of the chart (the whole View) in pixels.
     *
     * @return
     */
    public MPPointF getCenter() {
        return MPPointF.getInstance(getWidth() / 2f, getHeight() / 2f);
    }

    /**
     * Returns a recyclable MPPointF instance.
     * Returns the center of the chart taking offsets under consideration.
     * (returns the center of the content rectangle)
     *
     * @return
     */
    @Override
    public MPPointF getCenterOffsets() {
        return mViewPortHandler.getContentCenter();
    }

    /**
     * Sets extra offsets (around the chart view) to be appended to the
     * auto-calculated offsets.
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setExtraOffsets(float left, float top, float right, float bottom) {
        setExtraLeftOffset(left);
        setExtraTopOffset(top);
        setExtraRightOffset(right);
        setExtraBottomOffset(bottom);
    }

    /**
     * Set an extra offset to be appended to the viewport's top
     */
    public void setExtraTopOffset(float offset) {
        mExtraTopOffset = Utils.convertDpToPixel(offset);
    }

    /**
     * @return the extra offset to be appended to the viewport's top
     */
    public float getExtraTopOffset() {
        return mExtraTopOffset;
    }

    /**
     * Set an extra offset to be appended to the viewport's right
     */
    public void setExtraRightOffset(float offset) {
        mExtraRightOffset = Utils.convertDpToPixel(offset);
    }

    /**
     * @return the extra offset to be appended to the viewport's right
     */
    public float getExtraRightOffset() {
        return mExtraRightOffset;
    }

    /**
     * Set an extra offset to be appended to the viewport's bottom
     */
    public void setExtraBottomOffset(float offset) {
        mExtraBottomOffset = Utils.convertDpToPixel(offset);
    }

    /**
     * @return the extra offset to be appended to the viewport's bottom
     */
    public float getExtraBottomOffset() {
        return mExtraBottomOffset;
    }

    /**
     * Set an extra offset to be appended to the viewport's left
     */
    public void setExtraLeftOffset(float offset) {
        mExtraLeftOffset = Utils.convertDpToPixel(offset);
    }

    /**
     * @return the extra offset to be appended to the viewport's left
     */
    public float getExtraLeftOffset() {
        return mExtraLeftOffset;
    }

    /**
     * Set this to true to enable logcat outputs for the chart. Beware that
     * logcat output decreases rendering performance. Default: disabled.
     *
     * @param enabled
     */
    public void setLogEnabled(boolean enabled) {
        mLogEnabled = enabled;
    }

    /**
     * Returns true if log-output is enabled for the chart, fals if not.
     *
     * @return
     */
    public boolean isLogEnabled() {
        return mLogEnabled;
    }

    /**
     * Sets the text that informs the user that there is no data available with
     * which to draw the chart.
     *
     * @param text
     */
    public void setNoDataText(String text) {
        mNoDataText = text;
    }

    /**
     * Sets the color of the no data text.
     *
     * @param color
     */
    public void setNoDataTextColor(int color) {
        mInfoPaint.setColor(color);
    }

    /**
     * Sets the typeface to be used for the no data text.
     *
     * @param tf
     */
    public void setNoDataTextTypeface(Typeface tf) {
        mInfoPaint.setTypeface(tf);
    }

    /**
     * alignment of the no data text
     *
     * @param align
     */
    public void setNoDataTextAlignment(Align align) {
        mInfoPaint.setTextAlign(align);
    }

    /**
     * Set this to false to disable all gestures and touches on the chart,
     * default: true
     *
     * @param enabled
     */
    public void setTouchEnabled(boolean enabled) {
        this.mTouchEnabled = enabled;
    }

    /**
     * sets the marker that is displayed when a value is clicked on the chart
     *
     * @param marker
     */
    public void setMarker(IMarker marker) {
        mMarker = marker;
    }

    /**
     * returns the marker that is set as a marker view for the chart
     *
     * @return
     */
    public IMarker getMarker() {
        return mMarker;
    }

    @Deprecated
    public void setMarkerView(IMarker v) {
        setMarker(v);
    }

    @Deprecated
    public IMarker getMarkerView() {
        return getMarker();
    }

    /**
     * Sets a new Description object for the chart.
     *
     * @param desc
     */
    public void setDescription(Description desc) {
        this.mDescription = desc;
    }

    /**
     * Returns the Description object of the chart that is responsible for holding all information related
     * to the description text that is displayed in the bottom right corner of the chart (by default).
     *
     * @return
     */
    public Description getDescription() {
        return mDescription;
    }

    /**
     * Returns the Legend object of the chart. This method can be used to get an
     * instance of the legend in order to customize the automatically generated
     * Legend.
     *
     * @return
     */
    public Legend getLegend() {
        return mLegend;
    }

    /**
     * Returns the renderer object responsible for rendering / drawing the
     * Legend.
     *
     * @return
     */
    public LegendRenderer getLegendRenderer() {
        return mLegendRenderer;
    }

    /**
     * Returns the rectangle that defines the borders of the chart-value surface
     * (into which the actual values are drawn).
     *
     * @return
     */
    @Override
    public RectF getContentRect() {
        return mViewPortHandler.getContentRect();
    }

    /**
     * disables intercept touchevents
     */
    public void disableScroll() {
        ViewParent parent = getParent();
        if (parent != null)
            parent.requestDisallowInterceptTouchEvent(true);
    }

    /**
     * enables intercept touchevents
     */
    public void enableScroll() {
        ViewParent parent = getParent();
        if (parent != null)
            parent.requestDisallowInterceptTouchEvent(false);
    }

    /**
     * paint for the grid background (only line and barchart)
     */
    public static final int PAINT_GRID_BACKGROUND = 4;

    /**
     * paint for the info text that is displayed when there are no values in the
     * chart
     */
    public static final int PAINT_INFO = 7;

    /**
     * paint for the description text in the bottom right corner
     */
    public static final int PAINT_DESCRIPTION = 11;

    /**
     * paint for the hole in the middle of the pie chart
     */
    public static final int PAINT_HOLE = 13;

    /**
     * paint for the text in the middle of the pie chart
     */
    public static final int PAINT_CENTER_TEXT = 14;

    /**
     * paint used for the legend
     */
    public static final int PAINT_LEGEND_LABEL = 18;

    /**
     * set a new paint object for the specified parameter in the chart e.g.
     * Chart.PAINT_VALUES
     *
     * @param p     the new paint object
     * @param which Chart.PAINT_VALUES, Chart.PAINT_GRID, Chart.PAINT_VALUES,
     *              ...
     */
    public void setPaint(Paint p, int which) {

        switch (which) {
            case PAINT_INFO:
                mInfoPaint = p;
                break;
            case PAINT_DESCRIPTION:
                mDescPaint = p;
                break;
        }
    }

    /**
     * Returns the paint object associated with the provided constant.
     *
     * @param which e.g. Chart.PAINT_LEGEND_LABEL
     * @return
     */
    public Paint getPaint(int which) {
        switch (which) {
            case PAINT_INFO:
                return mInfoPaint;
            case PAINT_DESCRIPTION:
                return mDescPaint;
        }

        return null;
    }

    @Deprecated
    public boolean isDrawMarkerViewsEnabled() {
        return isDrawMarkersEnabled();
    }

    @Deprecated
    public void setDrawMarkerViews(boolean enabled) {
        setDrawMarkers(enabled);
    }

    /**
     * returns true if drawing the marker is enabled when tapping on values
     * (use the setMarker(IMarker marker) method to specify a marker)
     *
     * @return
     */
    public boolean isDrawMarkersEnabled() {
        return mDrawMarkers;
    }

    /**
     * Set this to true to draw a user specified marker when tapping on
     * chart values (use the setMarker(IMarker marker) method to specify a
     * marker). Default: true
     *
     * @param enabled
     */
    public void setDrawMarkers(boolean enabled) {
        mDrawMarkers = enabled;
    }

    /**
     * Returns the ChartData object that has been set for the chart.
     *
     * @return
     */
    public T getData() {
        return mData;
    }

    /**
     * Returns the ViewPortHandler of the chart that is responsible for the
     * content area of the chart and its offsets and dimensions.
     *
     * @return
     */
    public ViewPortHandler getViewPortHandler() {
        return mViewPortHandler;
    }

    /**
     * Returns the Renderer object the chart uses for drawing data.
     *
     * @return
     */
    public DataRenderer getRenderer() {
        return mRenderer;
    }

    /**
     * Sets a new DataRenderer object for the chart.
     *
     * @param renderer
     */
    public void setRenderer(DataRenderer renderer) {

        if (renderer != null)
            mRenderer = renderer;
    }

    public IHighlighter getHighlighter() {
        return mHighlighter;
    }

    /**
     * Sets a custom highligher object for the chart that handles / processes
     * all highlight touch events performed on the chart-view.
     *
     * @param highlighter
     */
    public void setHighlighter(ChartHighlighter highlighter) {
        mHighlighter = highlighter;
    }

    /**
     * Returns a recyclable MPPointF instance.
     *
     * @return
     */
    @Override
    public MPPointF getCenterOfView() {
        return getCenter();
    }

    /**
     * Returns the bitmap that represents the chart.
     *
     * @return
     */
    public Bitmap getChartBitmap() {
        // Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        // Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        // Get the view's background
        Drawable bgDrawable = getBackground();
        if (bgDrawable != null)
            // has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            // does not have background drawable, then draw white background on
            // the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        draw(canvas);
        // return the bitmap
        return returnedBitmap;
    }

    /**
     * Saves the current chart state with the given name to the given path on
     * the sdcard leaving the path empty "" will put the saved file directly on
     * the SD card chart is saved as a PNG image, example:
     * saveToPath("myfilename", "foldername1/foldername2");
     *
     * @param title
     * @param pathOnSD e.g. "folder1/folder2/folder3"
     * @return returns true on success, false on error
     */
    public boolean saveToPath(String title, String pathOnSD) {



        Bitmap b = getChartBitmap();

        OutputStream stream = null;
        try {
            stream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()
                    + pathOnSD + "/" + title
                    + ".png");

            /*
             * Write bitmap to file using JPEG or PNG and 40% quality hint for
             * JPEG.
             */
            b.compress(CompressFormat.PNG, 40, stream);

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Saves the current state of the chart to the gallery as an image type. The
     * compression must be set for JPEG only. 0 == maximum compression, 100 = low
     * compression (high quality). NOTE: Needs permission WRITE_EXTERNAL_STORAGE
     *
     * @param fileName        e.g. "my_image"
     * @param subFolderPath   e.g. "ChartPics"
     * @param fileDescription e.g. "Chart details"
     * @param format          e.g. Bitmap.CompressFormat.PNG
     * @param quality         e.g. 50, min = 0, max = 100
     * @return returns true if saving was successful, false if not
     */
    public boolean saveToGallery(String fileName, String subFolderPath, String fileDescription, Bitmap.CompressFormat
            format, int quality) {
        // restrain quality
        if (quality < 0 || quality > 100)
            quality = 50;

        long currentTime = System.currentTimeMillis();

        File extBaseDir = Environment.getExternalStorageDirectory();
        File file = new File(extBaseDir.getAbsolutePath() + "/DCIM/" + subFolderPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
        }

        String mimeType = "";
        switch (format) {
            case PNG:
                mimeType = "image/png";
                if (!fileName.endsWith(".png"))
                    fileName += ".png";
                break;
            case WEBP:
                mimeType = "image/webp";
                if (!fileName.endsWith(".webp"))
                    fileName += ".webp";
                break;
            case JPEG:
            default:
                mimeType = "image/jpeg";
                if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")))
                    fileName += ".jpg";
                break;
        }

        String filePath = file.getAbsolutePath() + "/" + fileName;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);

            Bitmap b = getChartBitmap();
            b.compress(format, quality, out);

            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        long size = new File(filePath).length();

        ContentValues values = new ContentValues(8);

        // store the details
        values.put(Images.Media.TITLE, fileName);
        values.put(Images.Media.DISPLAY_NAME, fileName);
        values.put(Images.Media.DATE_ADDED, currentTime);
        values.put(Images.Media.MIME_TYPE, mimeType);
        values.put(Images.Media.DESCRIPTION, fileDescription);
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, filePath);
        values.put(Images.Media.SIZE, size);

        return getContext().getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values) != null;
    }

    /**
     * Saves the current state of the chart to the gallery as a JPEG image. The
     * filename and compression can be set. 0 == maximum compression, 100 = low
     * compression (high quality). NOTE: Needs permission WRITE_EXTERNAL_STORAGE
     *
     * @param fileName e.g. "my_image"
     * @param quality  e.g. 50, min = 0, max = 100
     * @return returns true if saving was successful, false if not
     */
    public boolean saveToGallery(String fileName, int quality) {
        return saveToGallery(fileName, "", "MPAndroidChart-Library Save", Bitmap.CompressFormat.PNG, quality);
    }

    /**
     * Saves the current state of the chart to the gallery as a PNG image.
     * NOTE: Needs permission WRITE_EXTERNAL_STORAGE
     *
     * @param fileName e.g. "my_image"
     * @return returns true if saving was successful, false if not
     */
    public boolean saveToGallery(String fileName) {
        return saveToGallery(fileName, "", "MPAndroidChart-Library Save", Bitmap.CompressFormat.PNG, 40);
    }

    /**
     * tasks to be done after the view is setup
     */
    protected ArrayList<Runnable> mJobs = new ArrayList<Runnable>();

    public void removeViewportJob(Runnable job) {
        mJobs.remove(job);
    }

    public void clearAllViewportJobs() {
        mJobs.clear();
    }

    /**
     * Either posts a job immediately if the chart has already setup it's
     * dimensions or adds the job to the execution queue.
     *
     * @param job
     */
    public void addViewportJob(Runnable job) {

        if (mViewPortHandler.hasChartDimens()) {
            post(job);
        } else {
            mJobs.add(job);
        }
    }

    /**
     * Returns all jobs that are scheduled to be executed after
     * onSizeChanged(...).
     *
     * @return
     */
    public ArrayList<Runnable> getJobs() {
        return mJobs;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = (int) Utils.convertDpToPixel(50f);
        setMeasuredDimension(
                Math.max(getSuggestedMinimumWidth(),
                        resolveSize(size,
                                widthMeasureSpec)),
                Math.max(getSuggestedMinimumHeight(),
                        resolveSize(size,
                                heightMeasureSpec)));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mLogEnabled)
            Log.i(LOG_TAG, "OnSizeChanged()");

        if (w > 0 && h > 0 && w < 10000 && h < 10000) {
            if (mLogEnabled)
                Log.i(LOG_TAG, "Setting chart dimens, width: " + w + ", height: " + h);
            mViewPortHandler.setChartDimens(w, h);
        } else {
            if (mLogEnabled)
                Log.w(LOG_TAG, "*Avoiding* setting chart dimens! width: " + w + ", height: " + h);
        }

        // This may cause the chart view to mutate properties affecting the view port --
        //   lets do this before we try to run any pending jobs on the view port itself
        notifyDataSetChanged();

        for (Runnable r : mJobs) {
            post(r);
        }

        mJobs.clear();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Setting this to true will set the layer-type HARDWARE for the view, false
     * will set layer-type SOFTWARE.
     *
     * @param enabled
     */
    public void setHardwareAccelerationEnabled(boolean enabled) {

        if (enabled)
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //Log.i(LOG_TAG, "Detaching...");

        if (mUnbind)
            unbindDrawables(this);
    }

    /**
     * unbind flag
     */
    private boolean mUnbind = false;

    /**
     * Unbind all drawables to avoid memory leaks.
     * Link: http://stackoverflow.com/a/6779164/1590502
     *
     * @param view
     */
    private void unbindDrawables(View view) {

        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    /**
     * Set this to true to enable "unbinding" of drawables. When a View is detached
     * from a window. This helps avoid memory leaks.
     * Default: false
     * Link: http://stackoverflow.com/a/6779164/1590502
     *
     * @param enabled
     */
    public void setUnbindEnabled(boolean enabled) {
        this.mUnbind = enabled;
    }
}
