
package com.github.mikephil.charting.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * RelativeLayout that is displayed when selecting values. Use the
 * setCustomViewResource(int res) method to set a custom layout for the marker
 * view.
 * 
 * @author Philipp Jahoda
 */
public class MarkerView extends RelativeLayout {

    /** draw offset on the x-axis */
    private float mXOffset = 0f;

    /** draw offset on the y-axis */
    private float mYOffset = 0f;

    /**
     * Constructor.
     * 
     * @param context
     */
    public MarkerView(Context context) {
        super(context);
    }

    /**
     * Sets the layout resource for a custom MarkerView.
     * 
     * @param layoutResource
     */
    public void setCustomViewResource(int layoutResource) {

        View.inflate(getContext(), layoutResource, this);

        measure(getWidth(), getHeight());
        layout(0, 0, getWidth(), getHeight());
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
        posx += mXOffset;
        posy += mYOffset;

        canvas.translate(posx, posy);
        draw(canvas);
        canvas.translate(-posx, -posy);
    }

    /**
     * Set the position offset of the MarkerView. By default, the top left edge
     * of the MarkerView is drawn directly where the selected value is at. In
     * order to change that, offsets in pixels can be defined.
     * 
     * @param x
     * @param y
     */
    public void setOffsets(float x, float y) {
        this.mXOffset = x;
        this.mYOffset = y;
    }

    public float getXOffset() {
        return mXOffset;
    }

    public float getYOffset() {
        return mYOffset;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //
        // int width = getMeasuredWidth();
        // int height = getMeasuredHeight();
        // int widthWithoutPadding = width - getPaddingLeft() -
        // getPaddingRight();
        // int heigthWithoutPadding = height - getPaddingTop() -
        // getPaddingBottom();
        //
        // setMeasuredDimension(widthWithoutPadding + getPaddingLeft() +
        // getPaddingRight(),
        // heigthWithoutPadding + getPaddingTop() + getPaddingBottom());
        //
        // setMeasuredDimension(100, 100);
    }
}
