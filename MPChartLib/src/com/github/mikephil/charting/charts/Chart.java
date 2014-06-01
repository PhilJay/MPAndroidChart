package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.SelInfo;
import com.github.mikephil.charting.utils.Utils;

/**
 * Baseclass of all Chart-Views.
 * 
 * @author Philipp Jahoda
 */
public abstract class Chart extends View {

	public static final String LOG_TAG = "MPChart";

	protected int mColorDarkBlue = Color.rgb(41, 128, 186);
	protected int mColorDarkRed = Color.rgb(232, 76, 59);

	/**
	 * defines the number of digits to use for all printed values, -1 means automatically determine
	 */
	protected int mValueDigitsToUse = -1;

	/**
	 * defines the number of digits all printed values
	 */
	protected int mValueFormatDigits = -1;

	/** chart offset to the left */
	protected int mOffsetLeft = 35;

	/** chart toffset to the top */
	protected int mOffsetTop = 25;

	/** chart offset to the right */
	protected int mOffsetRight = 20;

	/** chart offset to the bottom */
	protected int mOffsetBottom = 15;

	/** object that holds all data relevant for the chart (x-vals, y-vals, ...) */
	protected ChartData mData = null;

	/** final bitmap that contains all information and is drawn to the screen */
	protected Bitmap mDrawBitmap;

	/** the canvas that is used for drawing on the bitmap */
	protected Canvas mDrawCanvas;

	/** the lowest value the chart can display */
	protected float mYChartMin = 0.0f;

	/** the highest value the chart can display */
	protected float mYChartMax = 0.0f;

	/**
	 * paint object used for darwing the bitmap to the screen
	 */
	protected Paint mDrawPaint;

	/**
	 * paint object used for drawing the description text in the bottom right corner of the chart
	 */
	protected Paint mDescPaint;

	/**
	 * paint object for drawing the information text when there are no values in the chart
	 */
	protected Paint mInfoPaint;

	/**
	 * paint object for drawing values (text representing values of chart entries)
	 */
	protected Paint mValuePaint;

	/** this is the paint object used for drawing the data onto the chart */
	protected Paint mRenderPaint;

	/** the colortemplate the chart uses */
	protected ColorTemplate mCt;

	/** description text that appears in the bottom right corner of the chart */
	protected String mDescription = "Description.";

	/** flag that indicates if the chart has been fed with data yet */
	protected boolean mDataNotSet = true;

	/** the range of y-values the chart displays */
	protected float mDeltaY = 1f;

	/** the number of x-values the chart displays */
	protected float mDeltaX = 1f;

	/** contains the current scale factor of the x-axis */
	protected float mScaleX = 1f;
	
	/** contains the current scale factor of the y-axis */
	protected float mScaleY = 1f;

	/** matrix to map the values to the screen pixels */
	protected Matrix mMatrixValueToPx;

	/** matrix for handling the different offsets of the chart */
	protected Matrix mMatrixOffset;

	/** matrix used for touch events */
	protected Matrix mMatrixTouch = new Matrix();

	/** the default draw color (some kind of light blue) */
	protected int mDrawColor = Color.rgb(56, 199, 240);

	/** if true, touch gestures are enabled on the chart */
	protected boolean mTouchEnabled = true;

	/** if true, y-values are drawn on the chart */
	protected boolean mDrawYValues = true;

	/** if true, value highlightning is enabled */
	protected boolean mHighlightEnabled = true;

	/** this rectangle defines the area in which graph values can be drawn */
	protected Rect mContentRect;

	/** listener that is called when a value on the chart is selected */
	protected OnChartValueSelectedListener mSelectionListener;

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

		// initialize the utils
		Utils.init(getContext().getResources());

		setBackgroundColor(Color.WHITE);

		// do screen density conversions
		mOffsetBottom = (int) Utils.convertDpToPixel(mOffsetBottom);
		mOffsetLeft = (int) Utils.convertDpToPixel(mOffsetLeft);
		mOffsetRight = (int) Utils.convertDpToPixel(mOffsetRight);
		mOffsetTop = (int) Utils.convertDpToPixel(mOffsetTop);

		mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mRenderPaint.setStyle(Style.FILL);

		mDrawPaint = new Paint();

		mDescPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mDescPaint.setColor(Color.BLACK);
		mDescPaint.setTextAlign(Align.RIGHT);
		mDescPaint.setTextSize(Utils.convertDpToPixel(9f));

		mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInfoPaint.setColor(Color.rgb(247, 189, 51)); // orange
		mInfoPaint.setTextAlign(Align.CENTER);
		mInfoPaint.setTextSize(Utils.convertDpToPixel(12f));

		mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mValuePaint.setColor(Color.rgb(63, 63, 63));
		mValuePaint.setTextAlign(Align.CENTER);
		mValuePaint.setTextSize(Utils.convertDpToPixel(9f));

		mCt = new ColorTemplate();
		mCt.addDataSetColors(ColorTemplate.LIBERTY_COLORS, getContext());
	}

	/**
	 * Sets a new ChartData object for the chart.
	 * 
	 * @param data
	 */
	public void setData(ChartData data) {

		if (data == null || !data.isValid()) {
			Log.e(LOG_TAG, "Cannot set data for chart. Provided chart values are null or contain less than 2 entries.");
			mDataNotSet = true;
			return;
		}

		// LET THE CHART KNOW THERE IS DATA
		mDataNotSet = false;
		mData = data;

		prepare();
	}

	/**
	 * Sets primitive data for the chart. Internally, this is converted into a ChartData object with one DataSet (type
	 * 0). If you have more specific requirements for your data, use the setData(ChartData data) method and create your
	 * own ChartData object with as many DataSets as you like.
	 * 
	 * @param xVals
	 * @param yVals
	 */
	public void setData(ArrayList<String> xVals, ArrayList<Float> yVals) {

		ArrayList<Entry> series = new ArrayList<Entry>();

		for (int i = 0; i < yVals.size(); i++) {
			series.add(new Entry(yVals.get(i), i));
		}

		DataSet set = new DataSet(series, 0);
		ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
		dataSets.add(set);

		ChartData data = new ChartData(xVals, dataSets);

		setData(data);
	}

	/**
	 * does needed preparations for drawing
	 */
	public abstract void prepare();

	/**
	 * calcualtes the y-min and y-max value and the y-delta and x-delta value
	 */
	protected void calcMinMax(boolean fixedValues) {
		// only calculate values if not fixed values
		if (fixedValues) {
			mYChartMin = mData.getYMin();
			mYChartMax = mYChartMin + mData.getYMax();
		}

		// calc delta
		mDeltaY = mData.getYMax() - mYChartMin;
		mDeltaX = mData.getXVals().size() - 1;
	}

	private boolean mFirstDraw = true;
	private boolean mContentRectSetup = false;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (!mContentRectSetup) {
			mContentRectSetup = true;
			prepareContentRect();
		}

		if (mDataNotSet) { // check if there is data

			// if no data, inform the user
			canvas.drawText("No chart data available.", getWidth() / 2, getHeight() / 2, mInfoPaint);
			return;
		}

		if (mFirstDraw) {
			mFirstDraw = false;
			prepareMatrix();
		}

		if (mDrawBitmap == null || mDrawCanvas == null) {

			// use RGB_565 for best performance
			mDrawBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
			mDrawCanvas = new Canvas(mDrawBitmap);
		}

		mDrawCanvas.drawColor(Color.WHITE); // clear all
	}

	/**
	 * setup all the matrices that will be used for scaling the coordinates to the display
	 */
	protected void prepareMatrix() {

		float scaleX = (float) ((getWidth() - mOffsetLeft - mOffsetRight) / mDeltaX);
		float scaleY = (float) ((getHeight() - mOffsetBottom - mOffsetTop) / mDeltaY);

		// setup all matrices
		mMatrixValueToPx = new Matrix();
		mMatrixValueToPx.reset();
		mMatrixValueToPx.postTranslate(0, -mYChartMin);
		mMatrixValueToPx.postScale(scaleX, -scaleY);
		mMatrixOffset = new Matrix();
		mMatrixOffset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);
		// mXLegendRect = new Rect(mOffsetLeft-20, 0, getWidth() - mOffsetRight 
		// + 20, mOffsetTop);

		// calcModulus();
	}

	/**
	 * sets up the content rect that restricts the chart surface
	 */
	protected void prepareContentRect() {
		// create the content rect
		mContentRect = new Rect(mOffsetLeft, mOffsetTop, getWidth() - mOffsetRight, getHeight() - mOffsetBottom);
	}

	/**
	 * transforms an arraylist of Entry into a float array containing the x and y values transformed with all matrices
	 * 
	 * @param entries
	 * @param xoffset
	 *            offset the chart values should have on the x-axis (0.5f) to center for barchart
	 * @return
	 */
	protected float[] generateTransformedValues(ArrayList<Entry> entries, float xOffset) {

		float[] valuePoints = new float[entries.size() * 2];

		for (int j = 0; j < valuePoints.length; j += 2) {
			valuePoints[j] = entries.get(j / 2).getXIndex() + xOffset;
			valuePoints[j + 1] = entries.get(j / 2).getVal();
		}

		transformPointArray(valuePoints);

		return valuePoints;
	}

	/**
	 * transform a path with all the given matrices VERY IMPORTANT: keep order to value-touch-offset
	 * 
	 * @param path
	 */
	protected void transformPath(Path path) {

		path.transform(mMatrixValueToPx);
		path.transform(mMatrixTouch);
		path.transform(mMatrixOffset);
	}

	/**
	 * transforms multiple paths will all matrices
	 * 
	 * @param paths
	 */
	protected void transformPaths(ArrayList<Path> paths) {

		for (int i = 0; i < paths.size(); i++) {
			transformPath(paths.get(i));
		}
	}

	/**
	 * transform an array of points VERY IMPORTANT: keep order to value-touch-offset
	 * 
	 * @param pts
	 */
	protected void transformPointArray(float[] pts) {

		mMatrixValueToPx.mapPoints(pts);
		mMatrixTouch.mapPoints(pts);
		mMatrixOffset.mapPoints(pts);
	}

	/**
	 * transform a rectangle with all matrices
	 * 
	 * @param r
	 */
	protected void transformRect(RectF r) {

		mMatrixValueToPx.mapRect(r);
		mMatrixTouch.mapRect(r);
		mMatrixOffset.mapRect(r);
	}

	/**
	 * transforms multiple rects with all matrices
	 * 
	 * @param rects
	 */
	protected void transformRects(ArrayList<RectF> rects) {

		for (int i = 0; i < rects.size(); i++)
			transformRect(rects.get(i));
	}

	/**
	 * transforms the given rect objects with the touch matrix only
	 * 
	 * @param paths
	 */
	protected void transformRectsTouch(ArrayList<RectF> rects) {
		for (int i = 0; i < rects.size(); i++) {
			mMatrixTouch.mapRect(rects.get(i));
		}
	}

	/**
	 * transforms the given path objects with the touch matrix only
	 * 
	 * @param paths
	 */
	protected void transformPathsTouch(ArrayList<Path> paths) {
		for (int i = 0; i < paths.size(); i++) {
			paths.get(i).transform(mMatrixTouch);
		}
	}

	/**
	 * transform an array of points with all matrixes except the touch matrix --> use this if the transformed values are
	 * not effected by touch gestures
	 * 
	 * @param pts
	 */
	protected void transformPointArrayNoTouch(float[] pts) {

		mMatrixValueToPx.mapPoints(pts);
		// mMatrixTouch.mapPoints(pts);
		mMatrixOffset.mapPoints(pts);
	}

	/**
	 * draws the description text in the bottom right corner of the chart
	 */
	protected void drawDescription() {

		mDrawCanvas
				.drawText(mDescription, getWidth() - mOffsetRight - 10, getHeight() - mOffsetBottom - 10, mDescPaint);
	}

	/**
	 * calculates the approximate width of a text, depending on a demo text avoid repeated calls (e.g. inside drawing
	 * methods)
	 * 
	 * @param paint
	 * @param demoText
	 * @return
	 */
	protected int calcTextWidth(Paint paint, String demoText) {

		Rect r = new Rect();
		paint.getTextBounds(demoText, 0, demoText.length(), r);
		return r.width();
	}

	/**
	 * draws all the text-values to the chart
	 */
	protected abstract void drawValues();

	/**
	 * draws the actual data
	 */
	protected abstract void drawData();

	/**
	 * draws additional stuff, whatever that might be
	 */
	protected abstract void drawAdditional();

	/**
	 * draws the values of the chart that need highlightning
	 */
	protected abstract void drawHighlights();

	/**
	 * ################ ################ ################ ################
	 */
	/** CODE BELOW THIS RELATED TO SCALING AND GESTURES */

	/** touchlistener that handles touches and gestures on the chart */
	protected OnTouchListener mListener;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mListener == null)
			return false;

		// check if touch gestures are enabled
		if (!mTouchEnabled)
			return false;
		else
			return mListener.onTouch(this, event);
	}

	/**
	 * disables intercept touchevents
	 */
	public void disableScroll() {
		ViewParent parent = getParent();
		parent.requestDisallowInterceptTouchEvent(true);
	}

	/**
	 * enables intercept touchevents
	 */
	public void enableScroll() {
		ViewParent parent = getParent();
		parent.requestDisallowInterceptTouchEvent(false);
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
		
		Log.i(LOG_TAG, "curTransX: " + curTransX + ", curScaleX: " + curScaleX);
		Log.i(LOG_TAG, "curTransY: " + curTransY + ", curScaleY: " + curScaleY);

		// min scale-x is 1f
		mScaleX = Math.max(1f, Math.min(getMaxScaleX(), curScaleX));
		
		// min scale-y is 1f
		mScaleY = Math.max(1f, Math.min(getMaxScaleY(), curScaleY));

		float maxTransX = -(float) mContentRect.width() * (mScaleX - 1f);
		float newTransX = Math.min(Math.max(curTransX, maxTransX), 0);
		
		float maxTransY = -(float) mContentRect.height() * (mScaleY - 1f);
		float newTransY = Math.min(Math.max(curTransY, maxTransY), 0);
        
        Log.i(LOG_TAG, "scale-X: " + mScaleX + ", maxTransX: " + maxTransX + ", newTransX: " + newTransX);
        Log.i(LOG_TAG, "scale-Y: " + mScaleY + ", maxTransY: " + maxTransY + ", newTransY: " + newTransY);

		vals[Matrix.MTRANS_X] = newTransX;
		vals[Matrix.MSCALE_X] = mScaleX;
		vals[Matrix.MTRANS_Y] = -newTransY;
        vals[Matrix.MSCALE_Y] = mScaleY;

		matrix.setValues(vals);
	}

	/**
	 * ################ ################ ################ ################
	 */
	/** BELOW THIS CODE FOR HIGHLIGHTING */

	/**
	 * array of Highlight objects that reference the highlighted slices in the pie chart
	 */
	protected Highlight[] mIndicesToHightlight = new Highlight[0];

	/**
	 * checks if the given index is set for highlighting or not
	 * 
	 * @param xIndex
	 * @param dataSetIndex
	 * @return
	 */
	public boolean needsHighlight(int xIndex, int dataSetIndex) {

		// no highlight
		if (!valuesToHighlight())
			return false;

		for (int i = 0; i < mIndicesToHightlight.length; i++)

			// check if the xvalue for the given dataset needs highlight
			if (mIndicesToHightlight[i].getXIndex() == xIndex
					&& mIndicesToHightlight[i].getDataSetIndex() == dataSetIndex && xIndex <= mDeltaX)
				return true;

		return false;
	}

	/**
	 * returns true if there are values to highlight, false if there are not checks if the highlight array is null, has
	 * a length of zero or contains -1
	 * 
	 * @return
	 */
	public boolean valuesToHighlight() {
		return mIndicesToHightlight == null || mIndicesToHightlight.length <= 0 || mIndicesToHightlight[0] == null ? false
				: true;
	}

	/**
	 * highlights the value at the given index of the values list
	 * 
	 * @param highs
	 */
	public void highlightValues(Highlight[] highs) {

		// set the indices to highlight
		mIndicesToHightlight = highs;

		// redraw the chart
		invalidate();

		if (mSelectionListener != null) {

			if (!valuesToHighlight())
				mSelectionListener.onNothingSelected();
			else {

				Entry[] values = new Entry[highs.length];

				for (int i = 0; i < values.length; i++)
					values[i] = getEntryByDataSetIndex(highs[i].getXIndex(), highs[i].getDataSetIndex());

				// notify the listener
				mSelectionListener.onValuesSelected(values, highs);
			}
		}
	}

	/**
	 * ################ ################ ################ ################
	 */
	/** BELOW CODE IS FOR THE MARKER VIEW */

	/** the x-position the marker appears on */
	protected float mMarkerPosX = 0f;

	/** the y-postion the marker appears on */
	protected float mMarkerPosY = 0f;

	/** if set to true, the marker view is drawn when a value is clicked */
	protected boolean mDrawMarkerView = true;

	/** the view that represents the marker */
	protected RelativeLayout mMarkerView;

	/**
	 * draws the view that is displayed when the chart is clicked
	 */
	protected void drawMarkerView() {

		// if there is no marker view or no values are to highlight, return
		if (mMarkerView == null || !mDrawMarkerView || !valuesToHighlight())
			return;

		// int index = mIndicesToHightlight[0];
		// float value = getYValue(index);
		//
		// // position of the marker depends on selected value index and value
		// float[] pts = new float[] {
		// index, value
		// };
		// transformPointArray(pts);
		//
		// mMarkerPosX = pts[0] - mMarkerView.getWidth() / 2f;
		// mMarkerPosY = pts[1] - mMarkerView.getHeight();
		//
		// Log.i("", "h: " + mMarkerView.getHeight() + ", w: " +
		// mMarkerView.getWidth());
		//
		// // translate to marker position
		// mDrawCanvas.translate(mMarkerPosX, mMarkerPosY);
		// mMarkerView.draw(mDrawCanvas);
		//
		// // translate back
		// mDrawCanvas.translate(-mMarkerPosX, -mMarkerPosY);
	}

	/**
	 * ################ ################ ################ ################
	 */
	/** BELOW THIS ONLY GETTERS AND SETTERS */

	/**
	 * set a new (e.g. custom) charttouchlistener NOTE: make sure to setTouchEnabled(true); if you need touch gestures
	 * on the chart
	 * 
	 * @param l
	 */
	public void setOnTouchListener(OnTouchListener l) {
		this.mListener = l;
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
	 * if set to true, value highlightning is enabled
	 * 
	 * @param enabled
	 */
	public void setHighlightEnabled(boolean enabled) {
		mHighlightEnabled = enabled;
	}

	/**
	 * returns true if highlightning of values is enabled, false if not
	 * 
	 * @return
	 */
	public boolean isHighlightEnabled() {
		return mHighlightEnabled;
	}

	/**
	 * returns the total value (sum) of all y-values
	 * 
	 * @return
	 */
	public float getYValueSum() {
		return mData.getYValueSum();
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
	 * calcualtes the maximum x-scale value depending on the number of x-values, maximum scale is numberOfXvals / 2
	 * 
	 * @return
	 */
	public float getMaxScaleX() {
		return mDeltaX / 2f;
	}
	
	/**
     * calcualtes the maximum y-scale value depending on the y delta value
     * 
     * @return
     */
    public float getMaxScaleY() {
        return mDeltaY;
    }

	/**
	 * returns the current y-max value in the y-values array
	 * 
	 * @return
	 */
	public float getYMax() {
		return mData.getYMax();
	}

	/**
	 * returns the current minimum y-value that is visible on the chart - bottom line
	 * 
	 * @return
	 */
	public float getYChartMin() {
		return mYChartMin;
	}

	/**
	 * returns the current maximum y-value that is visible on the chart - can be displayed by the chart
	 * 
	 * @return
	 */
	public float getYChartMax() {
		return mYChartMax;
	}

	/**
	 * returns the current y-min value in the y-values array
	 * 
	 * @return
	 */
	public float getYMin() {
		return mData.getYMin();
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
	 * returns the average value for a specific DataSet type in the chart
	 * 
	 * @param type
	 * @return
	 */
	public float getAverage(int type) {
		return mData.getDataSetByType(type).getYValueSum() / mData.getDataSetByType(type).getEntryCount();
	}

	/**
	 * returns the number of values the chart holds
	 * 
	 * @return
	 */
	public int getValueCount() {
		return mData.getYValCount();
	}

	/**
	 * returns the center point of the chart
	 * 
	 * @return
	 */
	public PointF getCenter() {
		return new PointF(getWidth() / 2, getHeight() / 2);
	}

	/**
	 * returns the left offset of the chart in pixels
	 * 
	 * @return
	 */
	public float getOffsetLeft() {
		return mOffsetLeft;
	}

	/**
	 * sets the size of the description text in pixels, min 7f, max 14f
	 * 
	 * @param size
	 */
	public void setDescriptionTextSize(float size) {

		if (size > 14f)
			size = 14f;
		if (size < 7f)
			size = 7f;

		mInfoPaint.setTextSize(Utils.convertDpToPixel(size));
	}

	/**
	 * set a new draw color for the chart values (line and filled) default is Color.rgb(56, 199, 240)
	 * 
	 * @param color
	 */
	public void setDrawColor(int color) {
		mDrawColor = color;
	}

	/**
	 * set a description text that appears in the bottom right corner of the chart, size = Y-legend text size
	 * 
	 * @param desc
	 */
	public void setDescription(String desc) {
		this.mDescription = desc;
	}

	/**
	 * sets the offsets of the graph in every direction provide density pixels -> they are then rendered to pixels
	 * inside the chart
	 * 
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 */
	public void setGraphOffsets(int left, int right, int top, int bottom) {

		mOffsetBottom = (int) Utils.convertDpToPixel(bottom);
		mOffsetLeft = (int) Utils.convertDpToPixel(left);
		mOffsetRight = (int) Utils.convertDpToPixel(right);
		mOffsetTop = (int) Utils.convertDpToPixel(top);
	}

	/**
	 * set this to false to disable gestures on the chart, default: true
	 * 
	 * @param enabled
	 */
	public void setTouchEnabled(boolean enabled) {
		this.mTouchEnabled = enabled;
	}

	/**
	 * set this to true to draw y-values on the chart NOTE (for bar and linechart): if "maxvisiblecount" is reached, no
	 * values will be drawn even if this is enabled
	 * 
	 * @param enabled
	 */
	public void setDrawYValues(boolean enabled) {
		this.mDrawYValues = enabled;
	}

	/**
	 * sets the y- starting and ending value
	 * 
	 * @param start
	 * @param end
	 */
	public void setYStartEnd(float start, float end) {
		mYChartMin = start;
		mDeltaY = end - start;
	}

	/**
	 * Sets a colortemplate for the chart that defindes the colors used for drawing. If more values need to be drawn
	 * than provided colors available in the colortemplate, colors are repeated.
	 * 
	 * @param ct
	 */
	public void setColorTemplate(ColorTemplate ct) {
		this.mCt = ct;
	}

	/**
	 * returns the colortemplate used by the chart
	 * 
	 * @return
	 */
	public ColorTemplate getColorTemplate() {
		return mCt;
	}

	/**
	 * sets the view that is displayed when a value is clicked on the chart
	 * 
	 * @param v
	 */
	public void setMarkerView(View v) {

		mMarkerView = new RelativeLayout(getContext());
		mMarkerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT));
		mMarkerView.addView(v);

		mMarkerView.measure(getWidth(), getHeight());
		mMarkerView.layout(0, 0, getWidth(), getHeight());
	}

	/**
	 * returns the view that is set as a marker view for the chartv
	 * 
	 * @return
	 */
	public View getMarkerView() {
		return mMarkerView;
	}

	/**
	 * returns the x-position of the marker view
	 * 
	 * @return
	 */
	public float getMarkerPosX() {
		return mMarkerPosX;
	}

	/**
	 * returns the y-position of the marker view
	 * 
	 * @return
	 */
	public float getMarkerPosY() {
		return mMarkerPosY;
	}

	/** paint for the grid lines (only line and barchart) */
	public static final int PAINT_GRID = 3;

	/** paint for the grid background (only line and barchart) */
	public static final int PAINT_GRID_BACKGROUND = 4;

	/** paint for the y-legend values (only line and barchart) */
	public static final int PAINT_YLEGEND = 5;

	/** paint for the x-legend values (only line and barchart) */
	public static final int PAINT_XLEGEND = 6;

	/**
	 * paint for the info text that is displayed when there are no values in the chart
	 */
	public static final int PAINT_INFO = 7;

	/** paint for the value text */
	public static final int PAINT_VALUES = 8;

	/** paint for the inner circle (linechart) */
	public static final int PAINT_CIRCLES_INNER = 10;

	/** paint for the description text in the bottom right corner */
	public static final int PAINT_DESCRIPTION = 11;

	/** paint for the line surrounding the chart (only line and barchart) */
	public static final int PAINT_OUTLINE = 12;

	/** paint for the hole in the middle of the pie chart */
	public static final int PAINT_HOLE = 13;

	/** paint for the text in the middle of the pie chart */
	public static final int PAINT_CENTER_TEXT = 14;

	/** paint for highlightning the values of a linechart */
	public static final int PAINT_HIGHLIGHT_LINE = 15;

	/** paint for highlightning the values of a linechart */
	public static final int PAINT_HIGHLIGHT_BAR = 16;

	/**
	 * set a new paint object for the specified parameter in the chart e.g. Chart.PAINT_VALUES
	 * 
	 * @param p
	 *            the new paint object
	 * @param which
	 *            Chart.PAINT_VALUES, Chart.PAINT_GRID, Chart.PAINT_VALUES, ...
	 */
	public void setPaint(Paint p, int which) {

		switch (which) {
		case PAINT_INFO:
			mInfoPaint = p;
			break;
		case PAINT_DESCRIPTION:
			mDescPaint = p;
			break;
		case PAINT_VALUES:
			mValuePaint = p;
			break;
		}
	}

	/**
	 * returns true if drawing the marker-view is enabled when tapping on values (use the setMarkerView(View v) method
	 * to specify a marker view)
	 * 
	 * @return
	 */
	public boolean isDrawMarkerViewEnabled() {
		return mDrawMarkerView;
	}

	/**
	 * set this to true to draw a user specified marker-view when tapping on chart values (use the setMarkerView(View v)
	 * method to specify a marker view)
	 * 
	 * @param enabled
	 */
	public void setDrawMarkerView(boolean enabled) {
		mDrawMarkerView = enabled;
	}

	/**
	 * sets the draw color for the value paint object
	 * 
	 * @param color
	 */
	public void setValuePaintColor(int color) {
		mValuePaint.setColor(color);
	}

	/**
	 * returns true if y-value drawing is enabled, false if not
	 * 
	 * @return
	 */
	public boolean isDrawYValuesEnabled() {
		return mDrawYValues;
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
	 * Returns the y-value for the given index from the first DataSet. If multiple DataSets are used, please use
	 * getYValue(int index, int type);
	 * 
	 * @param index
	 * @return
	 */
	public float getYValue(int index) {
		return mData.getDataSetByIndex(0).getYVals().get(index).getVal();
	}

	/**
	 * returns the y-value for the given index from the DataSet with the given type.
	 * 
	 * @param index
	 * @param type
	 * @return
	 */
	public float getYValue(int index, int type) {
		DataSet set = mData.getDataSetByType(type);
		return set.getYVals().get(index).getVal();
	}

	/**
	 * returns the y-value for the given x-index and DataSet index
	 * 
	 * @param index
	 * @param dataSet
	 * @return
	 */
	public float getYValueByDataSetIndex(int xIndex, int dataSet) {
		DataSet set = mData.getDataSetByIndex(dataSet);
		return set.getYValForXIndex(xIndex);
	}

	/**
	 * returns the DataSet with the given index in the DataSet array held by the ChartData object.
	 * 
	 * @param index
	 * @return
	 */
	public DataSet getDataSetByIndex(int index) {
		return mData.getDataSetByIndex(index);
	}

	/**
	 * returns the DataSet with the given type that is stored in the ChartData object.
	 * 
	 * @param type
	 * @return
	 */
	public DataSet getDataSetByType(int type) {
		return mData.getDataSetByType(type);
	}

	/**
	 * returns the Entry object from the first DataSet stored in the ChartData object. If multiple DataSets are used,
	 * use getEntry(index, type) or getEntryByDataSetIndex(xIndex, dataSetIndex);
	 * 
	 * @param index
	 * @return
	 */
	public Entry getEntry(int index) {
		return mData.getDataSetByIndex(0).getYVals().get(index);
	}

	/**
	 * returns the Entry object at the given index from the DataSet with the given type.
	 * 
	 * @param index
	 * @param type
	 * @return
	 */
	public Entry getEntry(int index, int type) {
		return mData.getDataSetByType(type).getYVals().get(index);
	}

	/**
	 * Returns the corresponding Entry object at the given xIndex from the given DataSet. INFORMATION: This method does
	 * calculations at runtime. Do not over-use in performance critical situations.
	 * 
	 * @param xIndex
	 * @param dataSetIndex
	 * @return
	 */
	public Entry getEntryByDataSetIndex(int xIndex, int dataSetIndex) {
		return mData.getDataSetByIndex(dataSetIndex).getEntryForXIndex(xIndex);
	}

	/**
	 * Returns an array of SelInfo objects for the given x-index. The SelInfo objects give information about the value
	 * at the selected index and the DataSet it belongs to. INFORMATION: This method does calculations at runtime. Do
	 * not over-use in performance critical situations.
	 * 
	 * @param xIndex
	 * @return
	 */
	protected ArrayList<SelInfo> getYValsAtIndex(int xIndex) {

		ArrayList<SelInfo> vals = new ArrayList<SelInfo>();

		for (int i = 0; i < mData.getDataSetCount(); i++) {

			// extract all y-values from all DataSets at the given x-index
			float yVal = mData.getDataSetByIndex(i).getYValForXIndex(xIndex);

			if (!Float.isNaN(yVal)) {
				vals.add(new SelInfo(yVal, i));
			}
		}

		return vals;
	}

	/**
	 * Get all Entry objects at the given index across all DataSets. INFORMATION: This method does calculations at
	 * runtime. Do not over-use in performance critical situations.
	 * 
	 * @param xIndex
	 * @return
	 */
	public ArrayList<Entry> getEntriesAtIndex(int xIndex) {

		ArrayList<Entry> vals = new ArrayList<Entry>();

		for (int i = 0; i < mData.getDataSetCount(); i++) {

			DataSet set = mData.getDataSetByIndex(i);

			Entry e = set.getEntryForXIndex(xIndex);

			if (e != null) {
				vals.add(e);
			}
		}

		return vals;
	}

	/**
	 * returns the chartdata object the chart represents
	 * 
	 * @return
	 */
	public ChartData getData() {
		return mData;
	}

	/**
	 * returns the percentage the given value has of the total y-values
	 * 
	 * @param val
	 * @return
	 */
	public float getPercentOfTotal(float val) {
		return val / mData.getYValueSum() * 100f;
	}

	/**
	 * sets a typeface for the value-paint
	 * 
	 * @param t
	 */
	public void setValueTypeface(Typeface t) {
		mValuePaint.setTypeface(t);
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
	 * sets the number of digits that should be used for all printed values (if this is set to -1, digits will be
	 * calculated automatically), default -1
	 * 
	 * @param digits
	 */
	public void setValueDigits(int digits) {
		mValueDigitsToUse = digits;
	}

	/**
	 * returns the number of digits used to format the prited values of the chart (-1 means digits are calculated
	 * automatically)
	 * 
	 * @return
	 */
	public int getValueDigits() {
		return mValueDigitsToUse;
	}

	/**
	 * saves the current chart state to a bitmap in the gallery NOTE: Needs permission WRITE_EXTERNAL_STORAGE
	 * 
	 * @param title
	 */
	public void saveToGallery(String title) {
		MediaStore.Images.Media.insertImage(getContext().getContentResolver(), mDrawBitmap, title, "");
	}

	/**
	 * saves the chart with the given name to the given path on the sdcard leaving the path empty "" will put the saved
	 * file directly on the SD card chart is saved as .png example: saveToPath("myfilename", "foldername1/foldername2");
	 * 
	 * @param title
	 * @param pathOnSD
	 *            e.g. "folder1/folder2/folder3"
	 */
	public void saveToPath(String title, String pathOnSD) {

		OutputStream stream = null;
		try {
			stream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + pathOnSD + "/" + title
					+ ".png");

			/*
			 * Write bitmap to file using JPEG or PNG and 40% quality hint for JPEG.
			 */
			mDrawBitmap.compress(CompressFormat.PNG, 40, stream);

			stream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
