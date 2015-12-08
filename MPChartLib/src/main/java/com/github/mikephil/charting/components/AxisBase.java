
package com.github.mikephil.charting.components;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.util.Log;

import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Baseclass of all labels.
 * 
 * @author Philipp Jahoda
 */
public abstract class AxisBase extends ComponentBase {

	private int mGridColor = Color.GRAY;

    private float mGridLineWidth = 1f;

    private int mAxisLineColor = Color.GRAY;

    private float mAxisLineWidth = 1f;

    /** flag indicating if the grid lines for this axis should be drawn */
    protected boolean mDrawGridLines = true;

    /** flag that indicates if the line alongside the axis is drawn or not */
    protected boolean mDrawAxisLine = true;

    /** flag that indicates of the labels of this axis should be drawn or not */
    protected boolean mDrawLabels = true;

    /** the path effect of the grid lines that makes dashed lines possible */
    private DashPathEffect mGridDashPathEffect = null;

	/** array of limit lines that can be set for the axis */
	protected List<LimitLine> mLimitLines;

	/** flag indicating the limit lines layer depth */
	protected boolean mDrawLimitLineBehindData = false;

    /** default constructor */
    public AxisBase() {
        this.mTextSize = Utils.convertDpToPixel(10f);
        this.mXOffset = Utils.convertDpToPixel(5f);
        this.mYOffset = Utils.convertDpToPixel(5f);
		this.mLimitLines = new ArrayList<LimitLine>();
	}

    /**
     * Set this to true to enable drawing the grid lines for this axis.
     * 
     * @param enabled
     */
    public void setDrawGridLines(boolean enabled) {
        mDrawGridLines = enabled;
    }

    /**
     * Returns true if drawing grid lines is enabled for this axis.
     * 
     * @return
     */
    public boolean isDrawGridLinesEnabled() {
        return mDrawGridLines;
    }

    /**
     * Set this to true if the line alongside the axis should be drawn or not.
     * 
     * @param enabled
     */
    public void setDrawAxisLine(boolean enabled) {
        mDrawAxisLine = enabled;
    }

    /**
     * Returns true if the line alongside the axis should be drawn.
     * 
     * @return
     */
    public boolean isDrawAxisLineEnabled() {
        return mDrawAxisLine;
    }

    /**
     * Sets the color of the grid lines for this axis (the horizontal lines
     * coming from each label).
     * 
     * @param color
     */
    public void setGridColor(int color) {
        mGridColor = color;
    }

    /**
     * Returns the color of the grid lines for this axis (the horizontal lines
     * coming from each label).
     * 
     * @return
     */
    public int getGridColor() {
        return mGridColor;
    }

    /**
     * Sets the width of the border surrounding the chart in dp.
     * 
     * @param width
     */
    public void setAxisLineWidth(float width) {
        mAxisLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * Returns the width of the axis line (line alongside the axis).
     * 
     * @return
     */
    public float getAxisLineWidth() {
        return mAxisLineWidth;
    }

    /**
     * Sets the width of the grid lines that are drawn away from each axis
     * label.
     * 
     * @param width
     */
    public void setGridLineWidth(float width) {
        mGridLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * Returns the width of the grid lines that are drawn away from each axis
     * label.
     * 
     * @return
     */
    public float getGridLineWidth() {
        return mGridLineWidth;
    }

    /**
     * Sets the color of the border surrounding the chart.
     * 
     * @param color
     */
    public void setAxisLineColor(int color) {
        mAxisLineColor = color;
    }

    /**
     * Returns the color of the axis line (line alongside the axis).
     * 
     * @return
     */
    public int getAxisLineColor() {
        return mAxisLineColor;
    }

    /**
     * Set this to true to enable drawing the labels of this axis (this will not
     * affect drawing the grid lines or axis lines).
     * 
     * @param enabled
     */
    public void setDrawLabels(boolean enabled) {
        mDrawLabels = enabled;
    }

    /**
     * Returns true if drawing the labels is enabled for this axis.
     * 
     * @return
     */
    public boolean isDrawLabelsEnabled() {
        return mDrawLabels;
    }

	/**
	 * Adds a new LimitLine to this axis.
	 *
	 * @param l
	 */
	public void addLimitLine(LimitLine l) {
		mLimitLines.add(l);

		if (mLimitLines.size() > 6) {
			Log.e("MPAndroiChart",
					"Warning! You have more than 6 LimitLines on your axis, do you really want that?");
		}
	}

	/**
	 * Removes the specified LimitLine from the axis.
	 *
	 * @param l
	 */
	public void removeLimitLine(LimitLine l) {
		mLimitLines.remove(l);
	}

	/**
	 * Removes all LimitLines from the axis.
	 */
	public void removeAllLimitLines() {
		mLimitLines.clear();
	}

	/**
	 * Returns the LimitLines of this axis.
	 *
	 * @return
	 */
	public List<LimitLine> getLimitLines() {
		return mLimitLines;
	}

	/**
	 * If this is set to true, the LimitLines are drawn behind the actual data,
	 * otherwise on top. Default: false
	 *
	 * @param enabled
	 */
	public void setDrawLimitLinesBehindData(boolean enabled) {
		mDrawLimitLineBehindData = enabled;
	}

	public boolean isDrawLimitLinesBehindDataEnabled() {
		return mDrawLimitLineBehindData;
	}

	/**
     * Returns the longest formatted label (in terms of characters), this axis
     * contains.
     * 
     * @return
     */
    public abstract String getLongestLabel();

    /**
     * Enables the grid line to be drawn in dashed mode, e.g. like this
     * "- - - - - -". THIS ONLY WORKS IF HARDWARE-ACCELERATION IS TURNED OFF.
     * Keep in mind that hardware acceleration boosts performance.
     *
     * @param lineLength the length of the line pieces
     * @param spaceLength the length of space in between the pieces
     * @param phase offset, in degrees (normally, use 0)
     */
    public void enableGridDashedLine(float lineLength, float spaceLength, float phase) {
        mGridDashPathEffect = new DashPathEffect(new float[] {
                lineLength, spaceLength
        }, phase);
    }

    /**
     * Disables the grid line to be drawn in dashed mode.
     */
    public void disableGridDashedLine() {
        mGridDashPathEffect = null;
    }

    /**
     * Returns true if the grid dashed-line effect is enabled, false if not.
     *
     * @return
     */
    public boolean isGridDashedLineEnabled() {
        return mGridDashPathEffect == null ? false : true;
    }

    /**
     * returns the DashPathEffect that is set for grid line
     *
     * @return
     */
    public DashPathEffect getGridDashPathEffect() {
        return mGridDashPathEffect;
    }
}
