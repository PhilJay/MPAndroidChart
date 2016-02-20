
package com.github.mikephil.charting.utils;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

/**
 * Class that contains information about the charts current viewport settings, including offsets, scale & translation levels, ...
 *
 * @author Philipp Jahoda
 */
public class ViewPortHandler {

    /**
     * matrix used for touch events
     */
    protected final Matrix mMatrixTouch = new Matrix();

    /**
     * this rectangle defines the area in which graph values can be drawn
     */
    protected RectF mContentRect = new RectF();

    protected float mChartWidth = 0f;
    protected float mChartHeight = 0f;

    /**
     * minimum scale value on the y-axis
     */
    private float mMinScaleY = 1f;

    /**
     * maximum scale value on the y-axis
     */
    private float mMaxScaleY = Float.MAX_VALUE;

    /**
     * minimum scale value on the x-axis
     */
    private float mMinScaleX = 1f;

    /**
     * maximum scale value on the x-axis
     */
    private float mMaxScaleX = Float.MAX_VALUE;

    /**
     * contains the current scale factor of the x-axis
     */
    private float mScaleX = 1f;

    /**
     * contains the current scale factor of the y-axis
     */
    private float mScaleY = 1f;

    /**
     * current translation (drag distance) on the x-axis
     */
    private float mTransX = 0f;

    /**
     * current translation (drag distance) on the y-axis
     */
    private float mTransY = 0f;

    /**
     * offset that allows the chart to be dragged over its bounds on the x-axis
     */
    private float mTransOffsetX = 0f;

    /**
     * offset that allows the chart to be dragged over its bounds on the x-axis
     */
    private float mTransOffsetY = 0f;

    /**
     * Constructor - don't forget calling setChartDimens(...)
     */
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

        save.postScale(scaleX, scaleY, x, y);

        return save;
    }

    public Matrix zoom(float scaleX, float scaleY) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        save.setScale(scaleX, scaleY);

        return save;
    }

    /**
     * Zooms by the specified scale factors and moves the viewport to the specified values.
     *
     * @param scaleX
     * @param scaleY
     * @param pts
     * @param view
     * @return
     */
    public synchronized Matrix zoomAndCenter(float scaleX, float scaleY, float[] pts, final View view) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        final float x = pts[0] - offsetLeft();
        final float y = pts[1] - offsetTop();

        PointF center = getContentCenter();

        save.setScale(scaleX, scaleY, center.x, center.y);
        save.postTranslate(-x, -y);

        return refresh(save, view, true);
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
     * @param transformedPts the position to center view viewport to
     * @param view
     * @return save
     */
    public synchronized void centerViewPort(final float[] transformedPts, final View view) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        final float x = transformedPts[0] - offsetLeft();
        final float y = transformedPts[1] - offsetTop();

        //Log.i("", "Moving view to x: " + x + ", y: " + y);

        save.postTranslate(-x, -y);

        refresh(save, view, true);
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

        // min scale-x is 1f, max is the max float
        mScaleX = Math.min(Math.max(mMinScaleX, curScaleX), mMaxScaleX);

        // min scale-y is 1f
        mScaleY = Math.min(Math.max(mMinScaleY, curScaleY), mMaxScaleY);

        float width = 0f;
        float height = 0f;

        if (content != null) {
            width = content.width();
            height = content.height();
        }

        float maxTransX = -width * (mScaleX - 1f);
        float newTransX = Math.min(Math.max(curTransX, maxTransX - mTransOffsetX), mTransOffsetX);
        mTransX = newTransX;

        float maxTransY = height * (mScaleY - 1f);
        float newTransY = Math.max(Math.min(curTransY, maxTransY + mTransOffsetY), -mTransOffsetY);
        mTransY = newTransY;

        vals[Matrix.MTRANS_X] = mTransX;
        vals[Matrix.MSCALE_X] = mScaleX;

        vals[Matrix.MTRANS_Y] = mTransY;
        vals[Matrix.MSCALE_Y] = mScaleY;

        matrix.setValues(vals);
    }

    /**
     * Sets the minimum scale factor for the x-axis
     *
     * @param xScale
     */
    public void setMinimumScaleX(float xScale) {

        if (xScale < 1f)
            xScale = 1f;

        mMinScaleX = xScale;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Sets the maximum scale factor for the x-axis
     *
     * @param xScale
     */
    public void setMaximumScaleX(float xScale) {

        mMaxScaleX = xScale;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Sets the minimum and maximum scale factors for the x-axis
     *
     * @param minScaleX
     * @param maxScaleX
     */
    public void setMinMaxScaleX(float minScaleX, float maxScaleX) {

        if (minScaleX < 1f)
            minScaleX = 1f;

        mMinScaleX = minScaleX;
        mMaxScaleX = maxScaleX;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Sets the minimum scale factor for the y-axis
     *
     * @param yScale
     */
    public void setMinimumScaleY(float yScale) {

        if (yScale < 1f)
            yScale = 1f;

        mMinScaleY = yScale;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Sets the maximum scale factor for the y-axis
     *
     * @param yScale
     */
    public void setMaximumScaleY(float yScale) {

        mMaxScaleY = yScale;

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
    /**
     * BELOW METHODS FOR BOUNDS CHECK
     */

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
        x = (float) ((int) (x * 100.f)) / 100.f;
        return mContentRect.right >= x ? true : false;
    }

    public boolean isInBoundsTop(float y) {
        return mContentRect.top <= y ? true : false;
    }

    public boolean isInBoundsBottom(float y) {
        y = (float) ((int) (y * 100.f)) / 100.f;
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
     * Returns the translation (drag / pan) distance on the x-axis
     *
     * @return
     */
    public float getTransX() {
        return mTransX;
    }

    /**
     * Returns the translation (drag / pan) distance on the y-axis
     *
     * @return
     */
    public float getTransY() {
        return mTransY;
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

    /**
     * Returns true if the chart is not yet fully zoomed out on the x-axis
     *
     * @return
     */
    public boolean canZoomOutMoreX() {
        return (mScaleX > mMinScaleX);
    }

    /**
     * Returns true if the chart is not yet fully zoomed in on the x-axis
     *
     * @return
     */
    public boolean canZoomInMoreX() {
        return (mScaleX < mMaxScaleX);
    }

}
