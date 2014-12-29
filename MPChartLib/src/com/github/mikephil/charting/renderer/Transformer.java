
package com.github.mikephil.charting.renderer;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.ChartInterface;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * Transformer class that contains all matrices and is responsible for
 * transforming values into pixels on the screen and backwards.
 * 
 * @author Philipp Jahoda
 */
public class Transformer {

    /** matrix to map the values to the screen pixels */
    protected Matrix mMatrixValueToPx = new Matrix();

    /** matrix for handling the different offsets of the chart */
    protected Matrix mMatrixOffset = new Matrix();

    /** matrix used for touch events */
    protected final Matrix mMatrixTouch = new Matrix();

    /** if set to true, the y-axis is inverted and low values start at the top */
    private boolean mInvertYAxis = false;

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

    public Transformer() {

    }

    /**
     * Prepares the matrix that transforms values to pixels.
     * 
     * @param chart
     */
    public void prepareMatrixValuePx(ChartInterface chart) {

        float scaleX = (float) ((chart.getWidth() - chart.getOffsetRight() - chart
                .getOffsetLeft()) / chart.getDeltaX());
        float scaleY = (float) ((chart.getHeight() - chart.getOffsetTop() - chart
                .getOffsetBottom()) / chart.getDeltaY());

        // setup all matrices
        mMatrixValueToPx.reset();
        mMatrixValueToPx.postTranslate(0, -chart.getYChartMin());
        mMatrixValueToPx.postScale(scaleX, -scaleY);
    }

    /**
     * Prepares the matrix that contains all offsets.
     * 
     * @param chart
     */
    public void prepareMatrixOffset(ChartInterface chart) {

        mMatrixOffset.reset();

        // offset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);

        if (!mInvertYAxis)
            mMatrixOffset.postTranslate(chart.getOffsetLeft(),
                    chart.getHeight() - chart.getOffsetBottom());
        else {
            mMatrixOffset.setTranslate(chart.getOffsetLeft(), -chart.getOffsetTop());
            mMatrixOffset.postScale(1.0f, -1.0f);
        }

        // mMatrixOffset.set(offset);

        // mMatrixOffset.reset();
        //
        // mMatrixOffset.postTranslate(mOffsetLeft, getHeight() -
        // mOffsetBottom);
    }

    /**
     * Transforms an arraylist of Entry into a float array containing the x and
     * y values transformed with all matrices for the LINECHART or SCATTERCHART.
     * 
     * @param entries
     * @return
     */
    public float[] generateTransformedValuesLineScatter(ArrayList<? extends Entry> entries,
            float phaseY) {

        float[] valuePoints = new float[entries.size() * 2];

        for (int j = 0; j < valuePoints.length; j += 2) {

            Entry e = entries.get(j / 2);

            valuePoints[j] = e.getXIndex();
            valuePoints[j + 1] = e.getVal() * phaseY;
        }

        pointValuesToPixel(valuePoints);

        return valuePoints;
    }

    /**
     * Transforms an arraylist of Entry into a float array containing the x and
     * y values transformed with all matrices for the BARCHART.
     * 
     * @param entries
     * @param dataSet the dataset index
     * @return
     */
    public float[] generateTransformedValuesBarChart(ArrayList<? extends Entry> entries,
            int dataSet, BarData bd, float phaseY) {

        float[] valuePoints = new float[entries.size() * 2];

        int setCount = bd.getDataSetCount();
        float space = bd.getGroupSpace();

        for (int j = 0; j < valuePoints.length; j += 2) {

            Entry e = entries.get(j / 2);

            // calculate the x-position, depending on datasetcount
            float x = e.getXIndex() + (j / 2 * (setCount - 1)) + dataSet + 0.5f + space * (j / 2)
                    + space / 2f;
            float y = e.getVal();

            valuePoints[j] = x;
            valuePoints[j + 1] = y * phaseY;
        }

        pointValuesToPixel(valuePoints);

        return valuePoints;
    }

    /**
     * transform a path with all the given matrices VERY IMPORTANT: keep order
     * to value-touch-offset
     * 
     * @param path
     */
    public void pathValueToPixel(Path path) {

        path.transform(mMatrixValueToPx);
        path.transform(mMatrixTouch);
        path.transform(mMatrixOffset);
    }

    /**
     * Transforms multiple paths will all matrices.
     * 
     * @param paths
     */
    public void pathValuesToPixel(ArrayList<Path> paths) {

        for (int i = 0; i < paths.size(); i++) {
            pathValueToPixel(paths.get(i));
        }
    }

    /**
     * Transform an array of points with all matrices. VERY IMPORTANT: Keep
     * matrix order "value-touch-offset" when transforming.
     * 
     * @param pts
     */
    public void pointValuesToPixel(float[] pts) {

        mMatrixValueToPx.mapPoints(pts);
        mMatrixTouch.mapPoints(pts);
        mMatrixOffset.mapPoints(pts);
    }

    /**
     * Transform a rectangle with all matrices.
     * 
     * @param r
     */
    public void rectValueToPixel(RectF r) {

        mMatrixValueToPx.mapRect(r);
        mMatrixTouch.mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * Transform a rectangle with all matrices with potential animation phases.
     * 
     * @param r
     * @param phaseY
     */
    public void rectValueToPixel(RectF r, float phaseY) {

        // multiply the height of the rect with the phase
        if (r.top > 0)
            r.top *= phaseY;
        else
            r.bottom *= phaseY;

        mMatrixValueToPx.mapRect(r);
        mMatrixTouch.mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * transforms multiple rects with all matrices
     * 
     * @param rects
     */
    public void rectValuesToPixel(ArrayList<RectF> rects) {

        for (int i = 0; i < rects.size(); i++)
            rectValueToPixel(rects.get(i));
    }

    /**
     * Transforms the given array of touch positions (pixels) (x, y, x, y, ...)
     * into values on the chart.
     * 
     * @param pixels
     */
    public void pixelsToValue(float[] pixels) {

        Matrix tmp = new Matrix();

        // invert all matrixes to convert back to the original value
        mMatrixOffset.invert(tmp);
        tmp.mapPoints(pixels);

        mMatrixTouch.invert(tmp);
        tmp.mapPoints(pixels);

        mMatrixValueToPx.invert(tmp);
        tmp.mapPoints(pixels);
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
     * If this is set to true, the y-axis is inverted which means that low
     * values are on top of the chart, high values on bottom.
     * 
     * @param enabled
     */
    public void setInvertYAxisEnabled(boolean enabled) {
        mInvertYAxis = enabled;
    }

    /**
     * If this returns true, the y-axis is inverted.
     * 
     * @return
     */
    public boolean isInvertYAxisEnabled() {
        return mInvertYAxis;
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
    public synchronized void centerViewPort(final float[] pts, final ChartInterface chart) {

        final View v = chart.getChartView();

        v.post(new Runnable() {

            @Override
            public void run() {
                Matrix save = new Matrix();
                save.set(mMatrixTouch);

                pointValuesToPixel(pts);

                final float x = pts[0] - chart.getOffsetLeft();
                final float y = pts[1] - chart.getOffsetTop();

                save.postTranslate(-x, -y);

                refresh(save, chart);
            }
        });
    }

    /**
     * call this method to refresh the graph with a given matrix
     * 
     * @param newMatrix
     * @return
     */
    public Matrix refresh(Matrix newMatrix, ChartInterface chart) {

        mMatrixTouch.set(newMatrix);

        // make sure scale and translation are within their bounds
        limitTransAndScale(mMatrixTouch, chart.getContentRect());

        chart.getChartView().invalidate();

        newMatrix.set(mMatrixTouch);
        return newMatrix;
    }

    /**
     * limits the maximum scale and X translation of the given matrix
     * 
     * @param matrix
     */
    private void limitTransAndScale(Matrix matrix, RectF content) {

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

    /**
     * Sets the minimum scale values for both axes. This limits the extent to
     * which the user can zoom-out.
     * 
     * @param scaleXmin
     * @param scaleYmin
     */
    public void setScaleMinima(float scaleXmin, float scaleYmin, ChartInterface chart) {

        if (scaleXmin < 1f)
            scaleXmin = 1f;
        if (scaleYmin < 1f)
            scaleYmin = 1f;

        mMinScaleX = scaleXmin;
        mMinScaleY = scaleYmin;

        Matrix save = zoom(mMinScaleX, mMinScaleY, 0f, 0f);
        refresh(save, chart);
    }

    // /**
    // * transforms the given rect objects with the touch matrix only
    // *
    // * @param paths
    // */
    // public void transformRectsTouch(ArrayList<RectF> rects) {
    // for (int i = 0; i < rects.size(); i++) {
    // mMatrixTouch.mapRect(rects.get(i));
    // }
    // }
    //
    // /**
    // * transforms the given path objects with the touch matrix only
    // *
    // * @param paths
    // */
    // public void transformPathsTouch(ArrayList<Path> paths) {
    // for (int i = 0; i < paths.size(); i++) {
    // paths.get(i).transform(mMatrixTouch);
    // }
    // }

    public Matrix getTouchMatrix() {
        return mMatrixTouch;
    }

    public Matrix getValueMatrix() {
        return mMatrixValueToPx;
    }

    public Matrix getOffsetMatrix() {
        return mMatrixOffset;
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
