package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.BarHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.BarDataProvider;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererBarChart;

/**
 * Chart that draws bars.
 * 
 * @author Philipp Jahoda
 */
public class BarChart extends BarLineChartBase<BarData> implements BarDataProvider {

	/** flag that enables or disables the highlighting arrow */
	private boolean mDrawHighlightArrow = false;

	/**
	 * if set to true, all values are drawn above their bars, instead of below their top
	 */
	private boolean mDrawValueAboveBar = true;

	/**
	 * if set to true, all values of a stack are drawn individually, and not just their sum
	 */
	// private boolean mDrawValuesForWholeStack = true;

	/**
	 * if set to true, a grey area is drawn behind each bar that indicates the maximum value
	 */
	private boolean mDrawBarShadow = false;

	public BarChart(Context context) {
		super(context);
	}

	public BarChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BarChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void init() {
		super.init();

		mRenderer = new BarChartRenderer(this, mAnimator, mViewPortHandler);
		mXAxisRenderer = new XAxisRendererBarChart(mViewPortHandler, mXAxis, mLeftAxisTransformer, this);

		mHighlighter = new BarHighlighter(this);

		mXChartMin = -0.5f;
	}

	@Override
	protected void calcMinMax() {
		super.calcMinMax();

		// increase deltax by 1 because the bars have a width of 1
		mDeltaX += 0.5f;

		// extend xDelta to make space for multiple datasets (if ther are one)
		mDeltaX *= mData.getDataSetCount();

		float groupSpace = mData.getGroupSpace();
		mDeltaX += mData.getXValCount() * groupSpace;
		mXChartMax = mDeltaX - mXChartMin;
	}

	/**
	 * Returns the Highlight object (contains x-index and DataSet index) of the selected value at the given touch point
	 * inside the BarChart.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	@Override
	public Highlight getHighlightByTouchPoint(float x, float y) {

		if (mData == null) {
			Log.e(LOG_TAG, "Can't select by touch. No data set.");
			return null;
		} else
			return mHighlighter.getHighlight(x, y);
	}

	/**
	 * Returns the bounding box of the specified Entry in the specified DataSet. Returns null if the Entry could not be
	 * found in the charts data.
	 * 
	 * @param e
	 * @return
	 */
	public RectF getBarBounds(BarEntry e) {

		BarDataSet set = mData.getDataSetForEntry(e);

		if (set == null)
			return null;

		float barspace = set.getBarSpace();
		float y = e.getVal();
		float x = e.getXIndex();

		float barWidth = 0.5f;

		float spaceHalf = barspace / 2f;
		float left = x - barWidth + spaceHalf;
		float right = x + barWidth - spaceHalf;
		float top = y >= 0 ? y : 0;
		float bottom = y <= 0 ? y : 0;

		RectF bounds = new RectF(left, top, right, bottom);

		getTransformer(set.getAxisDependency()).rectValueToPixel(bounds);

		return bounds;
	}

	/**
	 * set this to true to draw the highlightning arrow
	 * 
	 * @param enabled
	 */
	public void setDrawHighlightArrow(boolean enabled) {
		mDrawHighlightArrow = enabled;
	}

	/**
	 * returns true if drawing the highlighting arrow is enabled, false if not
	 * 
	 * @return
	 */
	public boolean isDrawHighlightArrowEnabled() {
		return mDrawHighlightArrow;
	}

	/**
	 * If set to true, all values are drawn above their bars, instead of below their top.
	 * 
	 * @param enabled
	 */
	public void setDrawValueAboveBar(boolean enabled) {
		mDrawValueAboveBar = enabled;
	}

	/**
	 * returns true if drawing values above bars is enabled, false if not
	 * 
	 * @return
	 */
	public boolean isDrawValueAboveBarEnabled() {
		return mDrawValueAboveBar;
	}

	// /**
	// * if set to true, all values of a stack are drawn individually, and not
	// * just their sum
	// *
	// * @param enabled
	// */
	// public void setDrawValuesForWholeStack(boolean enabled) {
	// mDrawValuesForWholeStack = enabled;
	// }
	//
	// /**
	// * returns true if all values of a stack are drawn, and not just their sum
	// *
	// * @return
	// */
	// public boolean isDrawValuesForWholeStackEnabled() {
	// return mDrawValuesForWholeStack;
	// }

	/**
	 * If set to true, a grey area is drawn behind each bar that indicates the maximum value. Enabling his will reduce
	 * performance by about 50%.
	 * 
	 * @param enabled
	 */
	public void setDrawBarShadow(boolean enabled) {
		mDrawBarShadow = enabled;
	}

	/**
	 * returns true if drawing shadows (maxvalue) for each bar is enabled, false if not
	 * 
	 * @return
	 */
	public boolean isDrawBarShadowEnabled() {
		return mDrawBarShadow;
	}

	@Override
	public BarData getBarData() {
		return mData;
	}

	/**
	 * Returns the lowest x-index (value on the x-axis) that is still visible on the chart.
	 * 
	 * @return
	 */
	@Override
	public int getLowestVisibleXIndex() {

		float step = mData.getDataSetCount();
		float div = (step <= 1) ? 1 : step + mData.getGroupSpace();

		float[] pts = new float[] { mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom() };

		getTransformer(AxisDependency.LEFT).pixelsToValue(pts);
		return (int) ((pts[0] <= getXChartMin()) ? 0 : (pts[0] / div) + 1);
	}

	/**
	 * Returns the highest x-index (value on the x-axis) that is still visible on the chart.
	 * 
	 * @return
	 */
	@Override
	public int getHighestVisibleXIndex() {

		float step = mData.getDataSetCount();
		float div = (step <= 1) ? 1 : step + mData.getGroupSpace();

		float[] pts = new float[] { mViewPortHandler.contentRight(), mViewPortHandler.contentBottom() };

		getTransformer(AxisDependency.LEFT).pixelsToValue(pts);
		return (int) ((pts[0] >= getXChartMax()) ? getXChartMax() / div : (pts[0] / div));
	}
}
