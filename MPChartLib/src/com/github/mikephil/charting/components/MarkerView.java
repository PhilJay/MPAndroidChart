
package com.github.mikephil.charting.components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.data.Entry;

/**
 * View that can be displayed when selecting values in the chart. Extend this
 * class to provide custom layouts for your markers.
 * 
 * @author Philipp Jahoda
 */
public abstract class MarkerView extends RelativeLayout {
    /** On which axis will draw the text **/
    protected YAxis.AxisDependency mAxisDependency = YAxis.AxisDependency.LEFT;

    /** The text that will draw on the axis **/
    protected String mAxisText;

    /** Axis text size in dp **/
    protected float mAxisTextSize;

    /** Axis text color **/
    protected int mAxisTextColor;

    /** Flag indicates draw axis text or not **/
    protected boolean mDrawAxisText;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     * 
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MarkerView(Context context, int layoutResource) {
        super(context);
        setupLayoutResource(layoutResource);
    }

    /**
     * Sets the layout resource for a custom MarkerView.
     * 
     * @param layoutResource
     */
    private void setupLayoutResource(int layoutResource) {

        View inflated = LayoutInflater.from(getContext()).inflate(layoutResource, this);

        inflated.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        // measure(getWidth(), getHeight());
        inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());
    }

    /**
     * Draws the MarkerView on the given position on the screen with the given
     * Canvas object.
     * 
     * @param canvas
     * @param posx
     * @param posy
     */
    public void draw(Canvas canvas, float posx, float posy) {

        // take offsets into consideration
        posx += getXOffset();
        posy += getYOffset();

        // translate to the correct position and draw
        canvas.translate(posx, posy);
        draw(canvas);
        canvas.translate(-posx, -posy);
    }

    /**
     * This method enables a specified custom MarkerView to update it's content
     * everytime the MarkerView is redrawn.
     * 
     * @param e The Entry the MarkerView belongs to. This can also be any
     *            subclass of Entry, like BarEntry or CandleEntry, simply cast
     *            it at runtime.
     * @param dataSetIndex the index of the DataSet the selected value is in
     */
    public abstract void refreshContent(Entry e, int dataSetIndex);

    /**
     * Use this to return the desired offset you wish the MarkerView to have on
     * the x-axis. By returning -(getWidth() / 2) you will center the MarkerView
     * horizontally.
     * 
     * @return
     */
    public abstract int getXOffset();

    /**
     * Use this to return the desired position offset you wish the MarkerView to
     * have on the y-axis. By returning -getHeight() you will cause the
     * MarkerView to be above the selected value.
     * 
     * @return
     */
    public abstract int getYOffset();

    /**
     * Returns YAxis.AxisDependency object, that indicates on which axis will draw the text
     * @return
     */
    public YAxis.AxisDependency getAxisDependency() {
        return mAxisDependency;
    }

    /**
     * Sets YAxis.AxisDependency object, that indicates on which axis will draw the text
     * @param axisDependency
     */
    public void setAxisDependency(YAxis.AxisDependency axisDependency) {
        this.mAxisDependency = axisDependency;
    }

    /**
     * Returns the text which draw on the YAxis
     * @return
     */
    public String getAxisText() {
        return mAxisText;
    }

    /**
     * Sets the text, which will draw on the YAxis
     * @param axisText
     */
    public void setAxisText(String axisText) {
        this.mAxisText = axisText;
    }

    /**
     * Returns axis text size in dp
     * @return
     */
    public float getAxisTextSize() {
        return mAxisTextSize;
    }

    /**
     * Sets axis text size in dp
     * @param axisTextSize
     */
    public void setAxisTextSize(float axisTextSize) {
        this.mAxisTextSize = axisTextSize;
    }

    /**
     * Returns axis text color
     * @return
     */
    public int getAxisTextColor() {
        return mAxisTextColor;
    }

    /**
     * Sets axis text color
     * @param axisTextColor
     */
    public void setAxisTextColor(int axisTextColor) {
        this.mAxisTextColor = axisTextColor;
    }

    /**
     * Returns true if drawing a text on YAxis is enabled
     * @return
     */
    public boolean isDrawAxisText() {
        return mDrawAxisText;
    }

    /**
     * Set true for drawing text on the YAxis
     * @param mDrawAxisText
     */
    public void setDrawAxisText(boolean mDrawAxisText) {
        this.mDrawAxisText = mDrawAxisText;
    }
}
