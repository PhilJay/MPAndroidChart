
package com.github.mikephil.charting.charts;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.animation.EasingFunction;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.ChartHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.LegendRenderer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Baseclass of all Chart-Views.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("NewApi")
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
     * will stop immediately. 1 is an invalid yValue, and will be converted to
     * 0.999f automatically.
     */
    private float mDragDecelerationFrictionCoef = 0.9f;

    /**
     * default yValue-formatter, number of digits depends on provided chart-data
     */
    protected ValueFormatter mDefaultFormatter;

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
     * description text that appears in the bottom right corner of the chart
     */
    protected String mDescription = "Description";

    /**
     * the object representing the labels on the xPx-axis
     */
    protected XAxis mXAxis;

    /**
     * if true, touch gestures are enabled on the chart
     */
    protected boolean mTouchEnabled = true;

    /**
     * the legend object containing all data associated with the legend
     */
    protected Legend mLegend;

    /**
     * listener that is called when a yValue on the chart is selected
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

    /**
     * text that is displayed when the chart is empty that describes why the
     * chart is empty
     */
    private String mNoDataTextDescription;

    protected LegendRenderer mLegendRenderer;

    /**
     * object responsible for rendering the data
     */
    protected DataRenderer mRenderer;

    protected ChartHighlighter mHighlighter;

    /**
     * object that manages the bounds and drawing constraints of the chart
     */
    protected ViewPortHandler mViewPortHandler;

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

        if (android.os.Build.VERSION.SDK_INT < 11)
            mAnimator = new ChartAnimator();
        else
            mAnimator = new ChartAnimator(new AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // ViewCompat.postInvalidateOnAnimation(Chart.this);
                    postInvalidate();
                }
            });

        // initialize the utils
        Utils.init(getContext());
        mMaxHighlightDistance = Utils.convertDpToPixel(70f);

        mDefaultFormatter = new DefaultValueFormatter(1);

        mViewPortHandler = new ViewPortHandler();

        mLegend = new Legend();

        mLegendRenderer = new LegendRenderer(mViewPortHandler, mLegend);

        mXAxis = new XAxis();

        mDescPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDescPaint.setColor(Color.BLACK);
        mDescPaint.setTextAlign(Align.RIGHT);
        mDescPaint.setTextSize(Utils.convertDpToPixel(9f));

        mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInfoPaint.setColor(Color.rgb(247, 189, 51)); // orange
        mInfoPaint.setTextAlign(Align.CENTER);
        mInfoPaint.setTextSize(Utils.convertDpToPixel(12f));

        mDrawPaint = new Paint(Paint.DITHER_FLAG);

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

        if (data == null) {
            Log.e(LOG_TAG,
                    "Cannot set data for chart. Provided data object is null.");
            return;
        }

        // LET THE CHART KNOW THERE IS DATA
        mOffsetsCalculated = false;
        mData = data;

        // calculate how many digits are needed
        calculateFormatter(data.getYMin(), data.getYMax());

        for (IDataSet set : mData.getDataSets()) {
            if (Utils.needsDefaultFormatter(set.getValueFormatter()))
                set.setValueFormatter(mDefaultFormatter);
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
        mIndicesToHighlight = null;
        invalidate();
    }

    /**
     * Removes all DataSets (and thereby Entries) from the chart. Does not
     * remove the xPx-values. Also refreshes the chart by calling invalidate().
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

            if (mData.getYValCount() <= 0)
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
     * calculates the offsets of the chart to the border depending on the
     * position of an eventual legend or depending on the length of the yPx-axis
     * and xPx-axis labels and their position
     */
    protected abstract void calculateOffsets();

    /**
     * calcualtes the yPx-min and yPx-max yValue and the yPx-delta and xPx-delta yValue
     */
    protected abstract void calcMinMax();

    /**
     * calculates the required number of digits for the values that might be
     * drawn in the chart (if enabled), and creates the default-yValue-formatter
     */
    protected void calculateFormatter(float min, float max) {

        float reference = 0f;

        if (mData == null || mData.getEntryCount() < 2) {

            reference = Math.max(Math.abs(min), Math.abs(max));
        } else {
            reference = Math.abs(max - min);
        }

        int digits = Utils.getDecimals(reference);
        mDefaultFormatter = new DefaultValueFormatter(digits);
    }

    /**
     * flag that indicates if offsets calculation has already been done or not
     */
    private boolean mOffsetsCalculated = false;

    /**
     * paint object used for drawing the bitmap
     */
    protected Paint mDrawPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        if (mData == null) {

            boolean hasText = !TextUtils.isEmpty(mNoDataText);
            boolean hasDescription = !TextUtils.isEmpty(mNoDataTextDescription);
            float line1height = hasText ? Utils.calcTextHeight(mInfoPaint, mNoDataText) : 0.f;
            float line2height = hasDescription ? Utils.calcTextHeight(mInfoPaint, mNoDataTextDescription) : 0.f;
            float lineSpacing = (hasText && hasDescription) ?
                    (mInfoPaint.getFontSpacing() - line1height) : 0.f;

            // if no data, inform the user

            float y = (getHeight() -
                    (line1height + lineSpacing + line2height)) / 2.f
                    + line1height;

            if (hasText) {
                canvas.drawText(mNoDataText, getWidth() / 2, y, mInfoPaint);

                if (hasDescription) {
                    y = y + line1height + lineSpacing;
                }
            }

            if (hasDescription) {
                canvas.drawText(mNoDataTextDescription, getWidth() / 2, y, mInfoPaint);
            }
            return;
        }

        if (!mOffsetsCalculated) {

            calculateOffsets();
            mOffsetsCalculated = true;
        }
    }

    /**
     * the custom position of the description text
     */
    private PointF mDescriptionPosition;

    /**
     * draws the description text in the bottom right corner of the chart
     */
    protected void drawDescription(Canvas c) {

        if (!mDescription.equals("")) {

            if (mDescriptionPosition == null) {

                c.drawText(mDescription, getWidth() - mViewPortHandler.offsetRight() - 10,
                        getHeight() - mViewPortHandler.offsetBottom()
                                - 10, mDescPaint);
            } else {
                c.drawText(mDescription, mDescriptionPosition.x, mDescriptionPosition.y, mDescPaint);
            }
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
     * The maximum distance in screen pixels away from an entry causing it to highlight.
     */
    protected float mMaxHighlightDistance = 0f;

    @Override
    public float getMaxHighlightDistance() {
        return mMaxHighlightDistance;
    }

    /**
     * Sets the maximum distance in screen dp a touch can be away from an entry to cause it to get highlighted.
     * Default: 70dp
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
     * Highlights the values at the given indices in the given DataSets. Provide
     * null or an empty array to undo all highlighting. This should be used to
     * programmatically highlight values. This DOES NOT generate a callback to
     * the OnChartValueSelectedListener.
     *
     * @param highs
     */
    public void highlightValues(Highlight[] highs) {

        // set the indices to highlight
        mIndicesToHighlight = highs;

        if (highs == null || highs.length <= 0 || highs[0] == null) {
            mChartTouchListener.setLastHighlighted(null);
        } else {
            mChartTouchListener.setLastHighlighted(highs[0]);
        }

        // redraw the chart
        invalidate();
    }

    /**
     * Highlights the yValue at the given xPx-index in the given DataSet. Provide
     * -1 as the xPx-index or dataSetIndex to undo all highlighting.
     *
     * @param xIndex
     * @param dataSetIndex
     */
    public void highlightValue(int xIndex, int dataSetIndex) {
        highlightValue(xIndex, dataSetIndex, true);
    }

    /**
     * Highlights the yValue at the given x position in the given DataSet. Provide
     * -1 as the dataSetIndex to undo all highlighting.
     *
     * @param x
     * @param dataSetIndex
     */
    public void highlightValue(float x, int dataSetIndex, boolean callListener) {

        if (dataSetIndex < 0 || dataSetIndex >= mData.getDataSetCount()) {
            highlightValue(null, callListener);
        } else {
            highlightValue(new Highlight(x, dataSetIndex), callListener);
        }
    }

    /**
     * Highlights the values represented by the provided Highlight object
     * This DOES NOT generate a callback to the OnChartValueSelectedListener.
     *
     * @param highlight contains information about which entry should be highlighted
     */
    public void highlightValue(Highlight highlight) {
        highlightValue(highlight, false);
    }

    /**
     * Highlights the yValue selected by touch gesture. Unlike
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
                if (this instanceof BarLineChartBase
                        && ((BarLineChartBase) this).isHighlightFullBarEnabled())
                    high = new Highlight(high.getX(), Float.NaN, -1, -1, -1);

                // set the indices to highlight
                mIndicesToHighlight = new Highlight[]{
                        high
                };
            }
        }

        if (callListener && mSelectionListener != null) {

            if (!valuesToHighlight())
                mSelectionListener.onNothingSelected();
            else {
                // notify the listener
                mSelectionListener.onValueSelected(e, high.getDataSetIndex(), high);
            }
        }
        // redraw the chart
        invalidate();
    }

    /**
     * Deprecated. Calls highlightValue(high, true)
     */
    @Deprecated
    public void highlightTouch(Highlight high) {
        highlightValue(high, true);
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
     * ################ ################ ################ ################
     */
    /** BELOW CODE IS FOR THE MARKER VIEW */

    /**
     * if set to true, the marker view is drawn when a yValue is clicked
     */
    protected boolean mDrawMarkerViews = true;

    /**
     * the view that represents the marker
     */
    protected MarkerView mMarkerView;

    /**
     * draws all MarkerViews on the highlighted positions
     */
    protected void drawMarkers(Canvas canvas) {

        // if there is no marker view or drawing marker is disabled
        if (mMarkerView == null || !mDrawMarkerViews || !valuesToHighlight())
            return;

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];

            Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);

            // make sure entry not null
            if (e == null || e.getX() != mIndicesToHighlight[i].getX())
                continue;

            float[] pos = getMarkerPosition(e, highlight);

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                continue;

            // callbacks to update the content
            mMarkerView.refreshContent(e, highlight);

            // mMarkerView.measure(MeasureSpec.makeMeasureSpec(0,
            // MeasureSpec.UNSPECIFIED),
            // MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            // mMarkerView.layout(0, 0, mMarkerView.getMeasuredWidth(),
            // mMarkerView.getMeasuredHeight());
            // mMarkerView.draw(mDrawCanvas, pos[0], pos[1]);

            mMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            mMarkerView.layout(0, 0, mMarkerView.getMeasuredWidth(),
                    mMarkerView.getMeasuredHeight());

            if (pos[1] - mMarkerView.getHeight() <= 0) {
                float y = mMarkerView.getHeight() - pos[1];
                mMarkerView.draw(canvas, pos[0], pos[1] + y);
            } else {
                mMarkerView.draw(canvas, pos[0], pos[1]);
            }
        }
    }

    /**
     * Returns the actual position in pixels of the MarkerView for the given
     * Entry in the given DataSet.
     *
     * @param e
     * @param highlight
     * @return
     */
    protected abstract float[] getMarkerPosition(Entry e, Highlight highlight);

    /**
     * ################ ################ ################ ################
     * ANIMATIONS ONLY WORK FOR API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
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
     * will stop immediately. 1 is an invalid yValue, and will be converted to
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
     * ANIMATIONS ONLY WORK FOR API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     */
    /** CODE BELOW FOR PROVIDING EASING FUNCTIONS */

    /**
     * Animates the drawing / rendering of the chart on both xPx- and yPx-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart. ANIMATIONS
     * ONLY WORK FOR API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     *
     * @param durationMillisX
     * @param durationMillisY
     * @param easingX         a custom easing function to be used on the animation phase
     * @param easingY         a custom easing function to be used on the animation phase
     */
    public void animateXY(int durationMillisX, int durationMillisY, EasingFunction easingX,
                          EasingFunction easingY) {
        mAnimator.animateXY(durationMillisX, durationMillisY, easingX, easingY);
    }

    /**
     * Animates the rendering of the chart on the xPx-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     *
     * @param durationMillis
     * @param easing         a custom easing function to be used on the animation phase
     */
    public void animateX(int durationMillis, EasingFunction easing) {
        mAnimator.animateX(durationMillis, easing);
    }

    /**
     * Animates the rendering of the chart on the yPx-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     *
     * @param durationMillis
     * @param easing         a custom easing function to be used on the animation phase
     */
    public void animateY(int durationMillis, EasingFunction easing) {
        mAnimator.animateY(durationMillis, easing);
    }

    /**
     * ################ ################ ################ ################
     * ANIMATIONS ONLY WORK FOR API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     */
    /** CODE BELOW FOR PREDEFINED EASING OPTIONS */

    /**
     * Animates the drawing / rendering of the chart on both xPx- and yPx-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart. ANIMATIONS
     * ONLY WORK FOR API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     *
     * @param durationMillisX
     * @param durationMillisY
     * @param easingX         a predefined easing option
     * @param easingY         a predefined easing option
     */
    public void animateXY(int durationMillisX, int durationMillisY, Easing.EasingOption easingX,
                          Easing.EasingOption easingY) {
        mAnimator.animateXY(durationMillisX, durationMillisY, easingX, easingY);
    }

    /**
     * Animates the rendering of the chart on the xPx-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     *
     * @param durationMillis
     * @param easing         a predefined easing option
     */
    public void animateX(int durationMillis, Easing.EasingOption easing) {
        mAnimator.animateX(durationMillis, easing);
    }

    /**
     * Animates the rendering of the chart on the yPx-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     *
     * @param durationMillis
     * @param easing         a predefined easing option
     */
    public void animateY(int durationMillis, Easing.EasingOption easing) {
        mAnimator.animateY(durationMillis, easing);
    }

    /**
     * ################ ################ ################ ################
     * ANIMATIONS ONLY WORK FOR API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     */
    /** CODE BELOW FOR ANIMATIONS WITHOUT EASING */

    /**
     * Animates the rendering of the chart on the xPx-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     *
     * @param durationMillis
     */
    public void animateX(int durationMillis) {
        mAnimator.animateX(durationMillis);
    }

    /**
     * Animates the rendering of the chart on the yPx-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     *
     * @param durationMillis
     */
    public void animateY(int durationMillis) {
        mAnimator.animateY(durationMillis);
    }

    /**
     * Animates the drawing / rendering of the chart on both xPx- and yPx-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart. ANIMATIONS
     * ONLY WORK FOR API LEVEL 11 (Android 3.0.xPx) AND HIGHER.
     *
     * @param durationMillisX
     * @param durationMillisY
     */
    public void animateXY(int durationMillisX, int durationMillisY) {
        mAnimator.animateXY(durationMillisX, durationMillisY);
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS ONLY GETTERS AND SETTERS */


    /**
     * Returns the object representing all xPx-labels, this method can be used to
     * acquire the XAxis object and modify it (e.g. change the position of the
     * labels, styling, etc.)
     *
     * @return
     */
    public XAxis getXAxis() {
        return mXAxis;
    }

    /**
     * Returns the default ValueFormatter that has been determined by the chart
     * considering the provided minimum and maximum values.
     *
     * @return
     */
    public ValueFormatter getDefaultValueFormatter() {
        return mDefaultFormatter;
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
     * returns the current yPx-max yValue across all DataSets
     *
     * @return
     */
    public float getYMax() {
        return mData.getYMax();
    }

    /**
     * returns the current yPx-min yValue across all DataSets
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
     * Returns the total number of (yPx) values the chart holds (across all DataSets).
     *
     * @return
     */
    public int getValueCount() {
        return mData.getYValCount();
    }

    /**
     * Returns the center point of the chart (the whole View) in pixels.
     *
     * @return
     */
    public PointF getCenter() {
        return new PointF(getWidth() / 2f, getHeight() / 2f);
    }

    /**
     * Returns the center of the chart taking offsets under consideration.
     * (returns the center of the content rectangle)
     *
     * @return
     */
    @Override
    public PointF getCenterOffsets() {
        return mViewPortHandler.getContentCenter();
    }

    /**
     * set a description text that appears in the bottom right corner of the
     * chart, size = Y-legend text size
     *
     * @param desc
     */
    public void setDescription(String desc) {
        if (desc == null)
            desc = "";
        this.mDescription = desc;
    }

    /**
     * Sets a custom position for the description text in pixels on the screen.
     *
     * @param x - xcoordinate
     * @param y - ycoordinate
     */
    public void setDescriptionPosition(float x, float y) {
        mDescriptionPosition = new PointF(x, y);
    }

    /**
     * sets the typeface for the description paint
     *
     * @param t
     */
    public void setDescriptionTypeface(Typeface t) {
        mDescPaint.setTypeface(t);
    }

    /**
     * sets the size of the description text in pixels, min 6f, max 16f
     *
     * @param size
     */
    public void setDescriptionTextSize(float size) {

        if (size > 16f)
            size = 16f;
        if (size < 6f)
            size = 6f;

        mDescPaint.setTextSize(Utils.convertDpToPixel(size));
    }

    /**
     * Sets the color of the description text.
     *
     * @param color
     */
    public void setDescriptionColor(int color) {
        mDescPaint.setColor(color);
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
     * Sets descriptive text to explain to the user why there is no chart
     * available Defaults to empty if not set
     *
     * @param text
     */
    public void setNoDataTextDescription(String text) {
        mNoDataTextDescription = text;
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
     * sets the view that is displayed when a yValue is clicked on the chart
     *
     * @param v
     */
    public void setMarkerView(MarkerView v) {
        mMarkerView = v;
    }

    /**
     * returns the view that is set as a marker view for the chart
     *
     * @return
     */
    public MarkerView getMarkerView() {
        return mMarkerView;
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
     * Returns the rectangle that defines the borders of the chart-yValue surface
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

    /**
     * returns true if drawing the marker-view is enabled when tapping on values
     * (use the setMarkerView(View v) method to specify a marker view)
     *
     * @return
     */
    public boolean isDrawMarkerViewEnabled() {
        return mDrawMarkerViews;
    }

    /**
     * Set this to true to draw a user specified marker-view when tapping on
     * chart values (use the setMarkerView(MarkerView mv) method to specify a
     * marker view). Default: true
     *
     * @param enabled
     */
    public void setDrawMarkerViews(boolean enabled) {
        mDrawMarkerViews = enabled;
    }

    /**
     * Get all Entry objects at the given index across all DataSets.
     * INFORMATION: This method does calculations at runtime. Do not over-use in
     * performance critical situations.
     *
     * @param xIndex
     * @return
     */
    public List<Entry> getEntriesAtIndex(int xIndex) {

        List<Entry> vals = new ArrayList<Entry>();

        for (int i = 0; i < mData.getDataSetCount(); i++) {

            IDataSet set = mData.getDataSetByIndex(i);

            Entry e = set.getEntryForXPos(xIndex);

            if (e != null) {
                vals.add(e);
            }
        }

        return vals;
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

    public ChartHighlighter getHighlighter() {
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

    @Override
    public PointF getCenterOfView() {
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
        return saveToGallery(fileName, "", "MPAndroidChart-Library Save", Bitmap.CompressFormat.JPEG, quality);
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

            mViewPortHandler.setChartDimens(w, h);

            if (mLogEnabled)
                Log.i(LOG_TAG, "Setting chart dimens, width: " + w + ", height: " + h);

            for (Runnable r : mJobs) {
                post(r);
            }

            mJobs.clear();
        }

        notifyDataSetChanged();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Setting this to true will set the layer-type HARDWARE for the view, false
     * will set layer-type SOFTWARE.
     *
     * @param enabled
     */
    public void setHardwareAccelerationEnabled(boolean enabled) {

        if (android.os.Build.VERSION.SDK_INT >= 11) {

            if (enabled)
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            else
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            Log.e(LOG_TAG,
                    "Cannot enable/disable hardware acceleration for devices below API level 11.");
        }
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
