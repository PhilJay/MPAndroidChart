
package com.github.mikephil.charting.utils;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.List;

/**
 * Transformer class that contains all matrices and is responsible for
 * transforming values into pixels on the screen and backwards.
 *
 * @author Philipp Jahoda
 */
public class Transformer {

    /**
     * matrix to map the values to the screen pixels
     */
    protected Matrix mMatrixValueToPx = new Matrix();

    /**
     * matrix for handling the different offsets of the chart
     */
    protected Matrix mMatrixOffset = new Matrix();

    protected ViewPortHandler mViewPortHandler;

    public Transformer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }

    /**
     * Prepares the matrix that transforms values to pixels. Calculates the
     * scale factors from the charts size and offsets.
     *
     * @param xChartMin
     * @param deltaX
     * @param deltaY
     * @param yChartMin
     */
    public void prepareMatrixValuePx(float xChartMin, float deltaX, float deltaY, float yChartMin) {

        float scaleX = (float) ((mViewPortHandler.contentWidth()) / deltaX);
        float scaleY = (float) ((mViewPortHandler.contentHeight()) / deltaY);

        if (Float.isInfinite(scaleX)) {
            scaleX = 0;
        }
        if (Float.isInfinite(scaleY)) {
            scaleY = 0;
        }

        // setup all matrices
        mMatrixValueToPx.reset();
        mMatrixValueToPx.postTranslate(-xChartMin, -yChartMin);
        mMatrixValueToPx.postScale(scaleX, -scaleY);
    }

    /**
     * Prepares the matrix that contains all offsets.
     *
     * @param inverted
     */
    public void prepareMatrixOffset(boolean inverted) {

        mMatrixOffset.reset();

        // offset.postTranslate(mOffsetLeft, getHeight() - mOffsetBottom);

        if (!inverted)
            mMatrixOffset.postTranslate(mViewPortHandler.offsetLeft(),
                    mViewPortHandler.getChartHeight() - mViewPortHandler.offsetBottom());
        else {
            mMatrixOffset
                    .setTranslate(mViewPortHandler.offsetLeft(), -mViewPortHandler.offsetTop());
            mMatrixOffset.postScale(1.0f, -1.0f);
        }

        // mMatrixOffset.set(offset);

        // mMatrixOffset.reset();
        //
        // mMatrixOffset.postTranslate(mOffsetLeft, getHeight() -
        // mOffsetBottom);
    }

    protected float[] valuePointsForGenerateTransformedValuesScatter = new float[1];
    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the SCATTERCHART.
     *
     * @param data
     * @return
     */
    public float[] generateTransformedValuesScatter(IScatterDataSet data, float phaseX,
                                                    float phaseY, int from, int to) {

        final int count = (int) ((to - from) * phaseX + 1) * 2;

        if(valuePointsForGenerateTransformedValuesScatter.length != count){
            valuePointsForGenerateTransformedValuesScatter = new float[count];
        }
        float[] valuePoints = valuePointsForGenerateTransformedValuesScatter;

        for (int j = 0; j < count; j += 2) {

            Entry e = data.getEntryForIndex(j / 2 + from);

            if (e != null) {
                valuePoints[j] = e.getX();
                valuePoints[j + 1] = e.getY() * phaseY;
            }else{
                valuePoints[j] = 0;
                valuePoints[j + 1] = 0;
            }
        }

        getValueToPixelMatrix().mapPoints(valuePoints);

        return valuePoints;
    }

    protected float[] valuePointsForGenerateTransformedValuesBubble = new float[1];
    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the BUBBLECHART.
     *
     * @param data
     * @return
     */
    public float[] generateTransformedValuesBubble(IBubbleDataSet data, float phaseY, int from, int to) {

        final int count = (to - from + 1) * 2; // (int) Math.ceil((to - from) * phaseX) * 2;

        if(valuePointsForGenerateTransformedValuesBubble.length != count){
            valuePointsForGenerateTransformedValuesBubble = new float[count];
        }
        float[] valuePoints = valuePointsForGenerateTransformedValuesBubble;

        for (int j = 0; j < count; j += 2) {

            Entry e = data.getEntryForIndex(j / 2 + from);

            if (e != null) {
                valuePoints[j] = e.getX();
                valuePoints[j + 1] = e.getY() * phaseY;
            }else{
                valuePoints[j] = 0;
                valuePoints[j + 1] = 0;
            }
        }

        getValueToPixelMatrix().mapPoints(valuePoints);

        return valuePoints;
    }

    protected float[] valuePointsForGenerateTransformedValuesLine = new float[1];
    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the LINECHART.
     *
     * @param data
     * @return
     */
    public float[] generateTransformedValuesLine(ILineDataSet data,
                                                 float phaseX, float phaseY, int from, int to) {

        final int count = (int) ((to - from) * phaseX + 1) * 2;

        if(valuePointsForGenerateTransformedValuesLine.length != count){
            valuePointsForGenerateTransformedValuesLine = new float[count];
        }
        float[] valuePoints = valuePointsForGenerateTransformedValuesLine;

        for (int j = 0; j < count; j += 2) {

            Entry e = data.getEntryForIndex(j / 2 + from);

            if (e != null) {
                valuePoints[j] = e.getX();
                valuePoints[j + 1] = e.getY() * phaseY;
            }else{
                valuePoints[j] = 0;
                valuePoints[j + 1] = 0;
            }
        }

        getValueToPixelMatrix().mapPoints(valuePoints);

        return valuePoints;
    }

    protected float[] valuePointsForGenerateTransformedValuesCandle = new float[1];
    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the CANDLESTICKCHART.
     *
     * @param data
     * @return
     */
    public float[] generateTransformedValuesCandle(ICandleDataSet data,
                                                   float phaseX, float phaseY, int from, int to) {

        final int count = (int) ((to - from) * phaseX + 1) * 2;

        if(valuePointsForGenerateTransformedValuesCandle.length != count){
            valuePointsForGenerateTransformedValuesCandle = new float[count];
        }
        float[] valuePoints = valuePointsForGenerateTransformedValuesCandle;

        for (int j = 0; j < count; j += 2) {

            CandleEntry e = data.getEntryForIndex(j / 2 + from);

            if (e != null) {
                valuePoints[j] = e.getX();
                valuePoints[j + 1] = e.getHigh() * phaseY;
            }else{
                valuePoints[j] = 0;
                valuePoints[j + 1] = 0;
            }
        }

        getValueToPixelMatrix().mapPoints(valuePoints);

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
        path.transform(mViewPortHandler.getMatrixTouch());
        path.transform(mMatrixOffset);
    }

    /**
     * Transforms multiple paths will all matrices.
     *
     * @param paths
     */
    public void pathValuesToPixel(List<Path> paths) {

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
        mViewPortHandler.getMatrixTouch().mapPoints(pts);
        mMatrixOffset.mapPoints(pts);
    }

    /**
     * Transform a rectangle with all matrices.
     *
     * @param r
     */
    public void rectValueToPixel(RectF r) {

        mMatrixValueToPx.mapRect(r);
        mViewPortHandler.getMatrixTouch().mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * Transform a rectangle with all matrices with potential animation phases.
     *
     * @param r
     * @param phaseY
     */
    public void rectToPixelPhase(RectF r, float phaseY) {

        // multiply the height of the rect with the phase
        r.top *= phaseY;
        r.bottom *= phaseY;

        mMatrixValueToPx.mapRect(r);
        mViewPortHandler.getMatrixTouch().mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    public void rectToPixelPhaseHorizontal(RectF r, float phaseY) {

        // multiply the height of the rect with the phase
        r.left *= phaseY;
        r.right *= phaseY;

        mMatrixValueToPx.mapRect(r);
        mViewPortHandler.getMatrixTouch().mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * Transform a rectangle with all matrices with potential animation phases.
     *
     * @param r
     */
    public void rectValueToPixelHorizontal(RectF r) {

        mMatrixValueToPx.mapRect(r);
        mViewPortHandler.getMatrixTouch().mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * Transform a rectangle with all matrices with potential animation phases.
     *
     * @param r
     * @param phaseY
     */
    public void rectValueToPixelHorizontal(RectF r, float phaseY) {

        // multiply the height of the rect with the phase
        r.left *= phaseY;
        r.right *= phaseY;

        mMatrixValueToPx.mapRect(r);
        mViewPortHandler.getMatrixTouch().mapRect(r);
        mMatrixOffset.mapRect(r);
    }

    /**
     * transforms multiple rects with all matrices
     *
     * @param rects
     */
    public void rectValuesToPixel(List<RectF> rects) {

        Matrix m = getValueToPixelMatrix();

        for (int i = 0; i < rects.size(); i++)
            m.mapRect(rects.get(i));
    }

    protected Matrix mPixelsToValueMatrixBuffer = new Matrix();
    /**
     * Transforms the given array of touch positions (pixels) (x, y, x, y, ...)
     * into values on the chart.
     *
     * @param pixels
     */
    public void pixelsToValue(float[] pixels) {

        Matrix tmp = mPixelsToValueMatrixBuffer;
        tmp.reset();

        // invert all matrixes to convert back to the original value
        mMatrixOffset.invert(tmp);
        tmp.mapPoints(pixels);

        mViewPortHandler.getMatrixTouch().invert(tmp);
        tmp.mapPoints(pixels);

        mMatrixValueToPx.invert(tmp);
        tmp.mapPoints(pixels);
    }

    /**
     * buffer for performance
     */
    float[] ptsBuffer = new float[2];

    /**
     * Returns a recyclable PointD instance.
     * returns the x and y values in the chart at the given touch point
     * (encapsulated in a PointD). This method transforms pixel coordinates to
     * coordinates / values in the chart. This is the opposite method to
     * getPixelsForValues(...).
     *
     * @param x
     * @param y
     * @return
     */
    public PointD getValuesByTouchPoint(float x, float y) {

        PointD result = PointD.getInstance(0,0);
        getValuesByTouchPoint(x,y,result);
        return result;
    }

    public void getValuesByTouchPoint(float x, float y, PointD outputPoint){

        ptsBuffer[0] = x;
        ptsBuffer[1] = y;

        pixelsToValue(ptsBuffer);

        outputPoint.x = ptsBuffer[0];
        outputPoint.y = ptsBuffer[1];
    }

    /**
     * Returns a recyclable PointD instance.
     * Returns the x and y coordinates (pixels) for a given x and y value in the chart.
     *
     * @param x
     * @param y
     * @return
     */
    public PointD getPixelsForValues(float x, float y) {

        ptsBuffer[0] = x;
        ptsBuffer[1] = y;

        pointValuesToPixel(ptsBuffer);

        double xPx = ptsBuffer[0];
        double yPx = ptsBuffer[1];

        return PointD.getInstance(xPx, yPx);
    }

    public Matrix getValueMatrix() {
        return mMatrixValueToPx;
    }

    public Matrix getOffsetMatrix() {
        return mMatrixOffset;
    }

    private Matrix mMBuffer1 = new Matrix();

    public Matrix getValueToPixelMatrix() {
        mMBuffer1.set(mMatrixValueToPx);
        mMBuffer1.postConcat(mViewPortHandler.mMatrixTouch);
        mMBuffer1.postConcat(mMatrixOffset);
        return mMBuffer1;
    }

    private Matrix mMBuffer2 = new Matrix();

    public Matrix getPixelToValueMatrix() {
        getValueToPixelMatrix().invert(mMBuffer2);
        return mMBuffer2;
    }
}
