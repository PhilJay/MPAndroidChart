
package com.github.mikephil.charting.charts;

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

import com.github.mikephil.charting.animation.AnimationEasing;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.ChartInterface;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.LegendRenderer;
import com.github.mikephil.charting.utils.DefaultValueFormatter;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ValueFormatter;
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
public abstract class Chart<T extends ChartData<? extends DataSet<? extends Entry>>> extends
        ViewGroup
        implements ChartInterface {

    public static final String LOG_TAG = "MPAndroidChart";

    /** flag that indicates if logging is enabled or not */
    protected boolean mLogEnabled = false;

    /**
     * object that holds all data that was originally set for the chart, before
     * it was modified or any filtering algorithms had been applied
     */
    protected T mData = null;

    /** default value-formatter, number of digits depends on provided chart-data */
    protected ValueFormatter mDefaultFormatter;

    /** the canvas that is used for drawing on the bitmap */
    // protected Canvas mDrawCanvas;

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

    /** description text that appears in the bottom right corner of the chart */
    protected String mDescription = "Description";

    /** flag that indicates if the chart has been fed with data yet */
    protected boolean mDataNotSet = true;

    /** if true, units are drawn next to the values in the chart */
    protected boolean mDrawUnitInChart = false;

    /** the number of x-values the chart displays */
    protected float mDeltaX = 1f;

    protected float mXChartMin = 0f;
    protected float mXChartMax = 0f;

    /** if true, touch gestures are enabled on the chart */
    protected boolean mTouchEnabled = true;

    /** if true, value highlightning is enabled */
    protected boolean mHighlightEnabled = true;

    /** the legend object containing all data associated with the legend */
    protected Legend mLegend;

    /** listener that is called when a value on the chart is selected */
    protected OnChartValueSelectedListener mSelectionListener;

    /** text that is displayed when the chart is empty */
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

    /** object responsible for rendering the data */
    protected DataRenderer mRenderer;

    /** object that manages the bounds and drawing constraints of the chart */
    protected ViewPortHandler mViewPortHandler;

    /** object responsible for animations */
    protected ChartAnimator mAnimator;

    /** default constructor for initialization in code */
    public Chart(Context context) {
        super(context);
        init();
    }

    /** constructor for initialization in xml */
    public Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /** even more awesome constructor */
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

        mAnimator = new ChartAnimator(new ChartAnimator.UpdateListener() {

            @Override
            public void onAnimationUpdate() {
                // ViewCompat.postInvalidateOnAnimation(Chart.this);
                postInvalidate();
            }
        });

        // initialize the utils
        Utils.init(getContext().getResources());

        mDefaultFormatter = new DefaultValueFormatter(1);

        mViewPortHandler = new ViewPortHandler();

        mLegend = new Legend();

        mLegendRenderer = new LegendRenderer(mViewPortHandler, mLegend);

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
        mDataNotSet = false;
        mOffsetsCalculated = false;
        mData = data;

        // calculate how many digits are needed
        calculateFormatter(data.getYMin(), data.getYMax());

        for (DataSet<?> set : mData.getDataSets()) {
            if (set.needsDefaultFormatter())
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
        mDataNotSet = true;
        invalidate();
    }

    /**
     * Removes all DataSets (and thereby Entries) from the chart. Does not
     * remove the x-values. Also refreshes the chart by calling invalidate().
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
     * necessary recalculations.
     */
    public abstract void notifyDataSetChanged();

    /**
     * calculates the offsets of the chart to the border depending on the
     * position of an eventual legend or depending on the length of the y-axis
     * and x-axis labels and their position
     */
    protected abstract void calculateOffsets();

    /**
     * calcualtes the y-min and y-max value and the y-delta and x-delta value
     */
    protected abstract void calcMinMax();

    /**
     * calculates the required number of digits for the values that might be
     * drawn in the chart (if enabled), and creates the default-value-formatter
     */
    protected void calculateFormatter(float min, float max) {

        float reference = 0f;

        if (mData == null || mData.getXValCount() < 2) {

            reference = Math.max(Math.abs(min), Math.abs(max));
        } else {
            reference = Math.abs(max - min);
        }

        int digits = Utils.getDecimals(reference);
        mDefaultFormatter = new DefaultValueFormatter(digits);
    }

    /** flag that indicates if offsets calculation has already been done or not */
    private boolean mOffsetsCalculated = false;

    /**
     * Bitmap object used for drawing. This is necessary because hardware
     * acceleration uses OpenGL which only allows a specific texture size to be
     * drawn on the canvas directly.
     **/
    protected Bitmap mDrawBitmap;

    /** paint object used for drawing the bitmap */
    protected Paint mDrawPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        if (mDataNotSet || mData == null || mData.getYValCount() <= 0) { // check
                                                                         // if
                                                                         // there
                                                                         // is
                                                                         // data

            // if no data, inform the user
            canvas.drawText(mNoDataText, getWidth() / 2, getHeight() / 2, mInfoPaint);

            if (!TextUtils.isEmpty(mNoDataTextDescription)) {
                float textOffset = -mInfoPaint.ascent() + mInfoPaint.descent();
                canvas.drawText(mNoDataTextDescription, getWidth() / 2, (getHeight() / 2)
                        + textOffset, mInfoPaint);
            }
            return;
        }

        if (!mOffsetsCalculated) {

            calculateOffsets();
            mOffsetsCalculated = true;
        }

        // if (mDrawCanvas == null) {
        // mDrawCanvas = new Canvas(mDrawBitmap);
        // }

        // clear everything
        // mDrawBitmap.eraseColor(Color.TRANSPARENT);
    }

    /**
     * draws the description text in the bottom right corner of the chart
     */
    protected void drawDescription(Canvas c) {

        if (!mDescription.equals("")) {

            c.drawText(mDescription, getWidth() - mViewPortHandler.offsetRight() - 10,
                    getHeight() - mViewPortHandler.offsetBottom()
                            - 10, mDescPaint);
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
    protected Highlight[] mIndicesToHightlight = new Highlight[0];

    /**
     * Returns the array of currently highlighted values. This might be null or
     * empty if nothing is highlighted.
     * 
     * @return
     */
    public Highlight[] getHighlighted() {
        return mIndicesToHightlight;
    }

    /**
     * Returns true if there are values to highlight, false if there are no
     * values to highlight. Checks if the highlight array is null, has a length
     * of zero or if the first object is null.
     *
     * @return
     */
    public boolean valuesToHighlight() {
        return mIndicesToHightlight == null || mIndicesToHightlight.length <= 0
                || mIndicesToHightlight[0] == null ? false
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
        mIndicesToHightlight = highs;

        // redraw the chart
        invalidate();
    }

    /**
     * Highlights the value at the given x-index in the given DataSet. Provide
     * -1 as the x-index to undo all highlighting.
     *
     * @param xIndex
     * @param dataSetIndex
     */
    public void highlightValue(int xIndex, int dataSetIndex) {

        if (xIndex < 0 || dataSetIndex < 0 || xIndex >= mData.getXValCount()
                || dataSetIndex >= mData.getDataSetCount()) {

            highlightValues(null);
        } else {
            highlightValues(new Highlight[] {
                    new Highlight(xIndex, dataSetIndex)
            });
        }
    }

    /**
     * Highlights the value selected by touch gesture. Unlike
     * highlightValues(...), this generates a callback to the
     * OnChartValueSelectedListener.
     *
     * @param highs
     */
    public void highlightTouch(Highlight high) {

        if (high == null)
            mIndicesToHightlight = null;
        else {

            if (mLogEnabled)
                Log.i(LOG_TAG, "Highlighted: " + high.toString());

            // set the indices to highlight
            mIndicesToHightlight = new Highlight[] {
                    high
            };
        }

        // redraw the chart
        invalidate();

        if (mSelectionListener != null) {

            if (!valuesToHighlight())
                mSelectionListener.onNothingSelected();
            else {

                Entry e = mData.getEntryForHighlight(high);

                // notify the listener
                mSelectionListener.onValueSelected(e, high.getDataSetIndex(), high);
            }
        }
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW CODE IS FOR THE MARKER VIEW */

    /** if set to true, the marker view is drawn when a value is clicked */
    protected boolean mDrawMarkerViews = true;

    /** the view that represents the marker */
    protected MarkerView mMarkerView;

    /**
     * draws all MarkerViews on the highlighted positions
     */
    protected void drawMarkers(Canvas canvas) {

        // if there is no marker view or drawing marker is disabled
        if (mMarkerView == null || !mDrawMarkerViews || !valuesToHighlight())
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

                // callbacks to update the content
                mMarkerView.refreshContent(e, dataSetIndex);

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
    }

    /**
     * Returns the actual position in pixels of the MarkerView for the given
     * Entry in the given DataSet.
     *
     * @param xIndex
     * @param dataSetIndex
     * @return
     */
    protected abstract float[] getMarkerPosition(Entry e, int dataSetIndex);

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
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart. ANIMATIONS
     * ONLY WORK FOR API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillisX
     * @param durationMillisY
     * @param easing an easing function to be used on the animation phase
     */
    public void animateXY(int durationMillisX, int durationMillisY, AnimationEasing.EasingFunction easing) {
        mAnimator.animateXY(durationMillisX, durationMillisY, easing);
    }

    /**
     * Animates the drawing / rendering of the chart on both x- and y-axis with
     * the specified animation time. If animate(...) is called, no further
     * calling of invalidate() is necessary to refresh the chart. ANIMATIONS
     * ONLY WORK FOR API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillisX
     * @param durationMillisY
     * @param easing an easing function option to be used on the animation phase
     */
    public void animateXY(int durationMillisX, int durationMillisY, AnimationEasing.EasingOption easing) {
        mAnimator.animateXY(durationMillisX, durationMillisY, easing);
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
    public void animateXY(int durationMillisX, int durationMillisY) {
        mAnimator.animateXY(durationMillisX, durationMillisY);
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillis
     * @param easing an easing function to be used on the animation phase
     */
    public void animateX(int durationMillis, AnimationEasing.EasingFunction easing) {
        mAnimator.animateX(durationMillis, easing);
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillis
     * @param easing an easing function option to be used on the animation phase
     */
    public void animateX(int durationMillis, AnimationEasing.EasingOption easing) {
        mAnimator.animateX(durationMillis, easing);
    }

    /**
     * Animates the rendering of the chart on the x-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillis
     */
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
     * @param easing an easing function to be used on the animation phase
     */
    public void animateY(int durationMillis, AnimationEasing.EasingFunction easing) {
        mAnimator.animateY(durationMillis, easing);
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillis
     * @param easing an easing function option to be used on the animation phase
     */
    public void animateY(int durationMillis, AnimationEasing.EasingOption easing) {
        mAnimator.animateY(durationMillis, easing);
    }

    /**
     * Animates the rendering of the chart on the y-axis with the specified
     * animation time. If animate(...) is called, no further calling of
     * invalidate() is necessary to refresh the chart. ANIMATIONS ONLY WORK FOR
     * API LEVEL 11 (Android 3.0.x) AND HIGHER.
     *
     * @param durationMillis
     */
    public void animateY(int durationMillis) {
        mAnimator.animateY(durationMillis);
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS FOR DYNAMICALLY ADDING ENTRIES AND DATASETS */

    // public void addEntry(Entry e, int dataSetIndex) {
    // mOriginalData.getDataSetByIndex(dataSetIndex).addEntry(e);
    //
    // prepare();
    // calcMinMax(false);
    // prepareMatrix();
    // calculateOffsets();
    // }
    //
    // public void addEntry(Entry e, String label) {
    // mOriginalData.getDataSetByLabel(label, false).addEntry(e);
    //
    // prepare();
    // calcMinMax(false);
    // prepareMatrix();
    // calculateOffsets();
    // }
    //
    // public void addDataSet(DataSet d) {
    // mOriginalData.addDataSet(d);
    //
    // prepare();
    // calcMinMax(false);
    // prepareMatrix();
    // calculateOffsets();
    // }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW THIS ONLY GETTERS AND SETTERS */

    // /**
    // * Returns the canvas object the chart uses for drawing.
    // *
    // * @return
    // */
    // public Canvas getCanvas() {
    // return mDrawCanvas;
    // }

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
     * If set to true, value highlighting is enabled which means that values can
     * be highlighted programmatically or by touch gesture.
     *
     * @param enabled
     */
    public void setHighlightEnabled(boolean enabled) {
        mHighlightEnabled = enabled;
    }

    /**
     * returns true if highlighting of values is enabled, false if not
     *
     * @return
     */
    public boolean isHighlightEnabled() {
        return mHighlightEnabled;
    }

    /**
     * returns the total value (sum) of all y-values across all DataSets
     *
     * @return
     */
    public float getYValueSum() {
        return mData.getYValueSum();
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

    // /**
    // * Get the total number of X-values.
    // *
    // * @return
    // */
    // @Override
    // public float getDeltaX() {
    // return mDeltaX;
    // }

    @Override
    public float getXChartMax() {
        return mXChartMax;
    }

    @Override
    public float getXChartMin() {
        return mXChartMin;
    }

    /**
     * returns the average value of all values the chart holds
     *
     * @return
     */
    public float getAverage() {
        return getYValueSum() / mData.getYValCount();
    }

    /**
     * returns the average value for a specific DataSet (with a specific label)
     * in the chart
     *
     * @param dataSetLabel
     * @return
     */
    public float getAverage(String dataSetLabel) {

        DataSet<? extends Entry> ds = mData.getDataSetByLabel(dataSetLabel, true);

        return ds.getYValueSum()
                / ds.getEntryCount();
    }

    /**
     * returns the total number of values the chart holds (across all DataSets)
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
     * Set this to true to enable logcat outputs for the chart. Default:
     * disabled
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

    // /**
    // * Sets the offsets from the border of the view to the actual chart in
    // every
    // * direction manually. This method needs to be recalled everytime a new
    // data
    // * object is set for the chart. Provide density pixels -> they are then
    // * rendered to pixels inside the chart.
    // *
    // * @param left
    // * @param right
    // * @param top
    // * @param bottom
    // */
    // public void setOffsets(float left, float top, float right, float bottom)
    // {
    //
    // mOffsetBottom = Utils.convertDpToPixel(bottom);
    // mOffsetLeft = Utils.convertDpToPixel(left);
    // mOffsetRight = Utils.convertDpToPixel(right);
    // mOffsetTop = Utils.convertDpToPixel(top);
    //
    // mTrans.prepareMatrixValuePx(this);
    // mTrans.prepareMatrixOffset(this);
    // prepareContentRect();
    // }

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
     * sets the view that is displayed when a value is clicked on the chart
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

    /** paint for the grid background (only line and barchart) */
    public static final int PAINT_GRID_BACKGROUND = 4;

    /**
     * paint for the info text that is displayed when there are no values in the
     * chart
     */
    public static final int PAINT_INFO = 7;

    /** paint for the description text in the bottom right corner */
    public static final int PAINT_DESCRIPTION = 11;

    /** paint for the hole in the middle of the pie chart */
    public static final int PAINT_HOLE = 13;

    /** paint for the text in the middle of the pie chart */
    public static final int PAINT_CENTER_TEXT = 14;

    /** paint used for the legend */
    public static final int PAINT_LEGEND_LABEL = 18;

    /**
     * set a new paint object for the specified parameter in the chart e.g.
     * Chart.PAINT_VALUES
     *
     * @param p the new paint object
     * @param which Chart.PAINT_VALUES, Chart.PAINT_GRID, Chart.PAINT_VALUES,
     *            ...
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
     * returns the x-value at the given index
     *
     * @param index
     * @return
     */
    public String getXValue(int index) {
        if (mData == null || mData.getXValCount() <= index)
            return null;
        else
            return mData.getXVals().get(index);
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

            DataSet<? extends Entry> set = mData.getDataSetByIndex(i);

            Entry e = set.getEntryForXIndex(xIndex);

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
     * returns the percentage the given value has of the total y-value sum
     *
     * @param val
     * @return
     */
    public float getPercentOfTotal(float val) {
        return val / mData.getYValueSum() * 100f;
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
     * Saves the current state of the chart to the gallery as a JPEG image. The
     * filename and compression can be set. 0 == maximum compression, 100 = low
     * compression (high quality). NOTE: Needs permission WRITE_EXTERNAL_STORAGE
     *
     * @param fileName e.g. "my_image"
     * @param quality e.g. 50, min = 0, max = 100
     * @return returns true if saving was successfull, false if not
     */
    public boolean saveToGallery(String fileName, int quality) {

        // restrain quality
        if (quality < 0 || quality > 100)
            quality = 50;

        long currentTime = System.currentTimeMillis();

        File extBaseDir = Environment.getExternalStorageDirectory();
        File file = new File(extBaseDir.getAbsolutePath() + "/DCIM");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
        }

        String filePath = file.getAbsolutePath() + "/" + fileName;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);

            Bitmap b = getChartBitmap();

            b.compress(Bitmap.CompressFormat.JPEG, quality, out); // control
            // the jpeg
            // quality

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
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.DESCRIPTION, "MPAndroidChart-Library Save");
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, filePath);
        values.put(Images.Media.SIZE, size);

        return getContext().getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values) == null
                ? false : true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        // prepareContentRect();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(left, top, right, bottom);
        }
    }

    /** tasks to be done after the view is setup */
    protected ArrayList<Runnable> mJobs = new ArrayList<Runnable>();

    /**
     * Adds a job to be executed after the chart-view is setup (after
     * onSizeChanged(...) is called).
     * 
     * @param job
     */
    public void addJob(Runnable job) {
        mJobs.add(job);
    }

    public void removeJob(Runnable job) {
        mJobs.remove(job);
    }

    public void clearAllJobs() {
        mJobs.clear();
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mLogEnabled)
            Log.i(LOG_TAG, "OnSizeChanged()");

        if (w > 0 && h > 0 && w < 10000 && h < 10000) {
            // create a new bitmap with the new dimensions

            if (mDrawBitmap != null)
                mDrawBitmap.recycle();

            mDrawBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
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
}
