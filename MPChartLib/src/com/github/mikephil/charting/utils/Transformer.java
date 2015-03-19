
package com.github.mikephil.charting.utils;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.List;

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

    private ViewPortHandler mViewPortHandler;

    public Transformer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }

    /**
     * Prepares the matrix that transforms values to pixels. Calculates the
     * scale factors from the charts size and offsets.
     * 
     * @param chart
     */
    public void prepareMatrixValuePx(float xChartMin, float deltaX, float deltaY, float yChartMin) {

        float scaleX = (float) ((mViewPortHandler.getChartWidth() - mViewPortHandler.offsetRight() - mViewPortHandler
                .offsetLeft()) / deltaX);
        float scaleY = (float) ((mViewPortHandler.getChartHeight() - mViewPortHandler.offsetTop() - mViewPortHandler
                .offsetBottom()) / deltaY);

        // setup all matrices
        mMatrixValueToPx.reset();
        mMatrixValueToPx.postTranslate(-xChartMin, -yChartMin);
        mMatrixValueToPx.postScale(scaleX, -scaleY);
    }

    // /**
    // * Prepares the transformation matrix with the specified scales.
    // *
    // * @param chart
    // * @param scaleX
    // * @param scaleY
    // */
    // public void prepareMatrixValuePx(ChartInterface chart, float scaleX,
    // float scaleY) {
    //
    // mMatrixValueToPx.reset();
    // mMatrixValueToPx.postTranslate(0, -chart.getYChartMin());
    // mMatrixValueToPx.postScale(scaleX, -scaleY);
    // }

    /**
     * Prepares the matrix that contains all offsets.
     * 
     * @param chart
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

    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the SCATTERCHART.
     * 
     * @param entries
     * @return
     */
    public float[] generateTransformedValuesScatter(List<? extends Entry> entries,
            float phaseY) {

        float[] valuePoints = new float[entries.size() * 2];

        for (int j = 0; j < valuePoints.length; j += 2) {

            Entry e = entries.get(j / 2);

            if (e != null) {
                valuePoints[j] = e.getXIndex();
                valuePoints[j + 1] = e.getVal() * phaseY;
            }
        }

        pointValuesToPixel(valuePoints);

        return valuePoints;
    }
    
    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the LINECHART.
     * 
     * @param entries
     * @return
     */
    public float[] generateTransformedValuesLine(List<? extends Entry> entries,
            float phaseY) {

        float[] valuePoints = new float[entries.size() * 2];

        for (int j = 0; j < valuePoints.length; j += 2) {

            Entry e = entries.get(j / 2);

            if (e != null) {
                valuePoints[j] = e.getXIndex();
                valuePoints[j + 1] = e.getVal() * phaseY;
            }
        }

        pointValuesToPixel(valuePoints);

        return valuePoints;
    }
    
    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the CANDLESTICKCHART.
     * 
     * @param entries
     * @return
     */
    public float[] generateTransformedValuesCandle(List<CandleEntry> entries,
            float phaseY) {

        float[] valuePoints = new float[entries.size() * 2];

        for (int j = 0; j < valuePoints.length; j += 2) {

            CandleEntry e = entries.get(j / 2);

            if (e != null) {
                valuePoints[j] = e.getXIndex();
                valuePoints[j + 1] = e.getHigh() * phaseY;
            }
        }

        pointValuesToPixel(valuePoints);

        return valuePoints;
    }

    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the BARCHART.
     * 
     * @param entries
     * @param dataSet the dataset index
     * @return
     */
    public float[] generateTransformedValuesBarChart(List<? extends Entry> entries,
            int dataSet, BarData bd, float phaseY) {

        float[] valuePoints = new float[entries.size() * 2];

        int setCount = bd.getDataSetCount();
        float space = bd.getGroupSpace();

        for (int j = 0; j < valuePoints.length; j += 2) {

            Entry e = entries.get(j / 2);

            // calculate the x-position, depending on datasetcount
            float x = e.getXIndex() + (j / 2 * (setCount - 1)) + dataSet + space * (j / 2)
                    + space / 2f;
            float y = e.getVal();

            valuePoints[j] = x;
            valuePoints[j + 1] = y * phaseY;
        }

        pointValuesToPixel(valuePoints);

        return valuePoints;
    }
    
    /**
     * Transforms an List of Entry into a float array containing the x and
     * y values transformed with all matrices for the BARCHART.
     * 
     * @param entries
     * @param dataSet the dataset index
     * @return
     */
    public float[] generateTransformedValuesHorizontalBarChart(List<? extends Entry> entries,
            int dataSet, BarData bd, float phaseY) {

        float[] valuePoints = new float[entries.size() * 2];

        int setCount = bd.getDataSetCount();
        float space = bd.getGroupSpace();

        for (int j = 0; j < valuePoints.length; j += 2) {

            Entry e = entries.get(j / 2);

            // calculate the x-position, depending on datasetcount
            float x = e.getXIndex() + (j / 2 * (setCount - 1)) + dataSet + space * (j / 2)
                    + space / 2f ;
            float y = e.getVal();

            valuePoints[j] = y * phaseY;
            valuePoints[j + 1] = x;
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
    public void rectValueToPixel(RectF r, float phaseY) {

        // multiply the height of the rect with the phase
        if (r.top > 0)
            r.top *= phaseY;
        else
            r.bottom *= phaseY;

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
        if (r.left > 0)
            r.left *= phaseY;
        else
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

        mViewPortHandler.getMatrixTouch().invert(tmp);
        tmp.mapPoints(pixels);

        mMatrixValueToPx.invert(tmp);
        tmp.mapPoints(pixels);
    }

    /**
     * Returns the x and y values in the chart at the given touch point
     * (encapsulated in a PointD). This method transforms pixel coordinates to
     * coordinates / values in the chart. This is the opposite method to
     * getPixelsForValues(...).
     * 
     * @param x
     * @param y
     * @return
     */
    public PointD getValuesByTouchPoint(float x, float y) {

        // create an array of the touch-point
        float[] pts = new float[2];
        pts[0] = x;
        pts[1] = y;

        pixelsToValue(pts);

        double xTouchVal = pts[0];
        double yTouchVal = pts[1];

        return new PointD(xTouchVal, yTouchVal);
    }

    // /**
    // * transforms the given rect objects with the touch matrix only
    // *
    // * @param paths
    // */
    // public void transformRectsTouch(List<RectF> rects) {
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
    // public void transformPathsTouch(List<Path> paths) {
    // for (int i = 0; i < paths.size(); i++) {
    // paths.get(i).transform(mMatrixTouch);
    // }
    // }

    public Matrix getValueMatrix() {
        return mMatrixValueToPx;
    }

    public Matrix getOffsetMatrix() {
        return mMatrixOffset;
    }
}
