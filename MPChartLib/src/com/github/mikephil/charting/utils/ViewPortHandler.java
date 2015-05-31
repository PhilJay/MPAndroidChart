
package com.github.mikephil.charting.utils;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

public class ViewPortHandler {

    /** matrix used for touch events */
    protected final Matrix mMatrixTouch = new Matrix();

    /** this rectangle defines the area in which graph values can be drawn */
    protected RectF mContentRect = new RectF();

    protected float mChartWidth = 0f;
    protected float mChartHeight = 0f;

    /** minimum scale value on the y-axis */
    private float mMinScaleY = 1f;

    /** minimum scale value on the x-axis */
    private float mMinScaleX = 1f;

    /** contains the current scale factor of the x-axis */
    private float mScaleX = 1f;

    /** contains the current scale factor of the y-axis */
    private float mScaleY = 1f;

    /** offset that allows the chart to be dragged over its bounds on the x-axis */
    private float mTransOffsetX = 0f;

    /** offset that allows the chart to be dragged over its bounds on the x-axis */
    private float mTransOffsetY = 0f;

    public ViewPortHandler() {

    }

    /**
     * Sets the width and height of the chart.
     * 
     * @param width
     * @param height
     */

    public void setChartDimens(float width, float height) {

        float offsetLeft = this.offsetLeft();
        float offsetTop = this.offsetTop();
        float offsetRight = this.offsetRight();
        float offsetBottom = this.offsetBottom();

        mChartHeight = height;
        mChartWidth = width;

        restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom);

    }

    public boolean hasChartDimens() {
        if (mChartHeight > 0 && mChartWidth > 0)
            return true;
        else
            return false;
    }

    public void restrainViewPort(float offsetLeft, float offsetTop, float offsetRight,
            float offsetBottom) {
        mContentRect.set(offsetLeft, offsetTop, mChartWidth - offsetRight, mChartHeight
                - offsetBottom);
    }

    public float offsetLeft() {
        return mContentRect.left;
    }

    public float offsetRight() {
        return mChartWidth - mContentRect.right;
    }

    public float offsetTop() {
        return mContentRect.top;
    }

    public float offsetBottom() {
        return mChartHeight - mContentRect.bottom;
    }

    public float contentTop() {
        return mContentRect.top;
    }

    public float contentLeft() {
        return mContentRect.left;
    }

    public float contentRight() {
        return mContentRect.right;
    }

    public float contentBottom() {
        return mContentRect.bottom;
    }

    public float contentWidth() {
        return mContentRect.width();
    }

    public float contentHeight() {
        return mContentRect.height();
    }

    public RectF getContentRect() {
        return mContentRect;
    }

    public PointF getContentCenter() {
        return new PointF(mContentRect.centerX(), mContentRect.centerY());
    }

    public float getChartHeight() {
        return mChartHeight;
    }

    public float getChartWidth() {
        return mChartWidth;
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
    public Matrix zoomIn(float x, float y) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        save.postScale(1.4f, 1.4f, x, y);

        return save;
    }

    /**
     * Zooms out by 0.7f, x and y are the coordinates (in pixels) of the zoom
     * center.
     */
    public Matrix zoomOut(float x, float y) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        save.postScale(0.7f, 0.7f, x, y);

        return save;
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
    public Matrix zoom(float scaleX, float scaleY, float x, float y) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        // Log.i(LOG_TAG, "Zooming, x: " + x + ", y: " + y);

        save.postScale(scaleX, scaleY, x, y);

        return save;
    }

    /**
     * Resets all zooming and dragging and makes the chart fit exactly it's
     * bounds.
     */
    public Matrix fitScreen() {
        
        mMinScaleX = 1f;
        mMinScaleY = 1f;

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

        return save;
    }

    /**
     * Centers the viewport around the specified position (x-index and y-value)
     * in the chart. Centering the viewport outside the bounds of the chart is
     * not possible. Makes most sense in combination with the
     * setScaleMinima(...) method.
     * 
     * @param pts the position to center view viewport to
     * @param chart
     * @return save
     */
    public synchronized void centerViewPort(final float[] transformedPts, final View view) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        final float x = transformedPts[0] - offsetLeft();
        final float y = transformedPts[1] - offsetTop();

        //Log.i("", "Moving view to x: " + x + ", y: " + y);

        save.postTranslate(-x, -y);

        refresh(save, view, false);

        // final View v = chart.getChartView();
        //
        // v.post(new Runnable() {
        //
        // @Override
        // public void run() {
        // Matrix save = new Matrix();
        // save.set(mMatrixTouch);
        //
        // final float x = transformedPts[0] - offsetLeft();
        // final float y = transformedPts[1] - offsetTop();
        //
        // save.postTranslate(-x, -y);
        //
        // refresh(save, chart, false);
        // }
        // });
    }

    /**
     * call this method to refresh the graph with a given matrix
     * 
     * @param newMatrix
     * @return
     */
    public Matrix refresh(Matrix newMatrix, View chart, boolean invalidate) {

        mMatrixTouch.set(newMatrix);

        // make sure scale and translation are within their bounds
        limitTransAndScale(mMatrixTouch, mContentRect);

        if (invalidate)
            chart.invalidate();

        newMatrix.set(mMatrixTouch);
        return newMatrix;
    }

    /**
     * limits the maximum scale and X translation of the given matrix
     * 
     * @param matrix
     */
    public void limitTransAndScale(Matrix matrix, RectF content) {

        float[] vals = new float[9];
        matrix.getValues(vals);

        float curTransX = vals[Matrix.MTRANS_X];
        float curScaleX = vals[Matrix.MSCALE_X];

        float curTransY = vals[Matrix.MTRANS_Y];
        float curScaleY = vals[Matrix.MSCALE_Y];

        // min scale-x is 1f
        mScaleX = Math.max(mMinScaleX, curScaleX);

        // min scale-y is 1f
        mScaleY = Math.max(mMinScaleY, curScaleY);

        float width = 0f;
        float height = 0f;

        if (content != null) {
            width = content.width();
            height = content.height();
        }

        float maxTransX = -width * (mScaleX - 1f);
        float newTransX = Math.min(Math.max(curTransX, maxTransX - mTransOffsetX), mTransOffsetX);

        // if(curScaleX < mMinScaleX) {
        // newTransX = (-width * (mScaleX - 1f)) / 2f;
        // }

        float maxTransY = height * (mScaleY - 1f);
        float newTransY = Math.max(Math.min(curTransY, maxTransY + mTransOffsetY), -mTransOffsetY);

        // if(curScaleY < mMinScaleY) {
        // newTransY = (height * (mScaleY - 1f)) / 2f;
        // }

        vals[Matrix.MTRANS_X] = newTransX;
        vals[Matrix.MSCALE_X] = mScaleX;

        vals[Matrix.MTRANS_Y] = newTransY;
        vals[Matrix.MSCALE_Y] = mScaleY;

        matrix.setValues(vals);
    }

    public void setMinimumScaleX(float xScale) {

        if (xScale < 1f)
            xScale = 1f;

        mMinScaleX = xScale;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    public void setMinimumScaleY(float yScale) {

        if (yScale < 1f)
            yScale = 1f;

        mMinScaleY = yScale;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Returns the charts-touch matrix used for translation and scale on touch.
     * 
     * @return
     */
    public Matrix getMatrixTouch() {
        return mMatrixTouch;
    }

    /**
     * ################ ################ ################ ################
     */
    /** BELOW METHODS FOR BOUNDS CHECK */

    public boolean isInBoundsX(float x) {
        if (isInBoundsLeft(x) && isInBoundsRight(x))
            return true;
        else
            return false;
    }

    public boolean isInBoundsY(float y) {
        if (isInBoundsTop(y) && isInBoundsBottom(y))
            return true;
        else
            return false;
    }

    public boolean isInBounds(float x, float y) {
        if (isInBoundsX(x) && isInBoundsY(y))
            return true;
        else
            return false;
    }

    public boolean isInBoundsLeft(float x) {
        return mContentRect.left <= x ? true : false;
    }

    public boolean isInBoundsRight(float x) {
        x = (float)((int)(x * 100.f)) / 100.f;
        return mContentRect.right >= x ? true : false;
    }

    public boolean isInBoundsTop(float y) {
        return mContentRect.top <= y ? true : false;
    }

    public boolean isInBoundsBottom(float y) {
        y = (float)((int)(y * 100.f)) / 100.f;
        return mContentRect.bottom >= y ? true : false;
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
        if (mScaleY > mMinScaleY || mMinScaleY > 1f)
            return false;
        else
            return true;
    }

    /**
     * Returns true if the chart is fully zoomed out on it's x-axis
     * (horizontal).
     * 
     * @return
     */
    public boolean isFullyZoomedOutX() {
        if (mScaleX > mMinScaleX || mMinScaleX > 1f)
            return false;
        else
            return true;
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
}
