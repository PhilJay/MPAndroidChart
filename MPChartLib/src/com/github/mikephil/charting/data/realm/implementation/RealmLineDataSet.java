package com.github.mikephil.charting.data.realm.implementation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;

import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.realm.base.RealmLineRadarDataSet;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public class RealmLineDataSet<T extends RealmObject> extends RealmLineRadarDataSet<T> implements ILineDataSet {

    /** Drawing mode for this line dataset **/
    private LineDataSet.Mode mMode = LineDataSet.Mode.LINEAR;

    /**
     * List representing all colors that are used for the circles
     */
    private List<Integer> mCircleColors = null;

    /**
     * the color of the inner circles
     */
    private int mCircleColorHole = Color.WHITE;

    /**
     * the radius of the circle-shaped value indicators
     */
    private float mCircleRadius = 8f;

    /** the hole radius of the circle-shaped value indicators */
    private float mCircleHoleRadius = 4f;

    /**
     * sets the intensity of the cubic lines
     */
    private float mCubicIntensity = 0.2f;

    /**
     * the path effect of this DataSet that makes dashed lines possible
     */
    private DashPathEffect mDashPathEffect = null;

    /**
     * formatter for customizing the position of the fill-line
     */
    private FillFormatter mFillFormatter = new DefaultFillFormatter();

    /**
     * if true, drawing circles is enabled
     */
    private boolean mDrawCircles = true;

    private boolean mDrawCircleHole = true;

    /**
     * Constructor for creating a LineDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     */
    public RealmLineDataSet(RealmResults<T> result, String yValuesField) {
        super(result, yValuesField);
        mCircleColors = new ArrayList<Integer>();

        // default color
        mCircleColors.add(Color.rgb(140, 234, 255));

        build(this.results);
        calcMinMax();
    }

    /**
     * Constructor for creating a LineDataSet with realm data.
     *
     * @param result       the queried results from the realm database
     * @param yValuesField the name of the field in your data object that represents the y-value
     * @param xIndexField  the name of the field in your data object that represents the x-index
     */
    public RealmLineDataSet(RealmResults<T> result, String yValuesField, String xIndexField) {
        super(result, yValuesField, xIndexField);
        mCircleColors = new ArrayList<Integer>();

        // default color
        mCircleColors.add(Color.rgb(140, 234, 255));

        build(this.results);
        calcMinMax();
    }

    /**
     * Returns the drawing mode for this line dataset
     *
     * @return
     */
    @Override
    public LineDataSet.Mode getMode() {
        return mMode;
    }

    /**
     * Returns the drawing mode for this line dataset
     *
     * @return
     */
    public void setMode(LineDataSet.Mode mode) {
        mMode = mode;
    }

    /**
     * Sets the intensity for cubic lines (if enabled). Max = 1f = very cubic,
     * Min = 0.05f = low cubic effect, Default: 0.2f
     *
     * @param intensity
     */
    public void setCubicIntensity(float intensity) {

        if (intensity > 1f)
            intensity = 1f;
        if (intensity < 0.05f)
            intensity = 0.05f;

        mCubicIntensity = intensity;
    }

    @Override
    public float getCubicIntensity() {
        return mCubicIntensity;
    }

    /**
     * sets the size (radius) of the circle shpaed value indicators, default
     * size = 4f
     *
     * @param size
     */
    public void setCircleSize(float size) {
        mCircleRadius = Utils.convertDpToPixel(size);
    }

    @Override
    public float getCircleRadius() {
        return mCircleRadius;
    }

    /**
     * sets the hole radius of the drawn circles.
     * Default radius = 2f
     *
     * @param holeRadius
     */
    public void setCircleHoleRadius(float holeRadius) {
        mCircleHoleRadius = Utils.convertDpToPixel(holeRadius);
    }

    @Override
    public float getCircleHoleRadius() {
        return mCircleHoleRadius;
    }

    /**
     * Enables the line to be drawn in dashed mode, e.g. like this
     * "- - - - - -". THIS ONLY WORKS IF HARDWARE-ACCELERATION IS TURNED OFF.
     * Keep in mind that hardware acceleration boosts performance.
     *
     * @param lineLength  the length of the line pieces
     * @param spaceLength the length of space in between the pieces
     * @param phase       offset, in degrees (normally, use 0)
     */
    public void enableDashedLine(float lineLength, float spaceLength, float phase) {
        mDashPathEffect = new DashPathEffect(new float[]{
                lineLength, spaceLength
        }, phase);
    }

    /**
     * Disables the line to be drawn in dashed mode.
     */
    public void disableDashedLine() {
        mDashPathEffect = null;
    }

    @Override
    public boolean isDashedLineEnabled() {
        return mDashPathEffect == null ? false : true;
    }

    @Override
    public DashPathEffect getDashPathEffect() {
        return mDashPathEffect;
    }

    /**
     * set this to true to enable the drawing of circle indicators for this
     * DataSet, default true
     *
     * @param enabled
     */
    public void setDrawCircles(boolean enabled) {
        this.mDrawCircles = enabled;
    }

    @Override
    public boolean isDrawCirclesEnabled() {
        return mDrawCircles;
    }

    @Deprecated
    public void setDrawCubic(boolean enabled) {
        mMode = enabled ? LineDataSet.Mode.CUBIC_BEZIER : LineDataSet.Mode.LINEAR;
    }

    @Deprecated
    @Override
    public boolean isDrawCubicEnabled() {
        return mMode == LineDataSet.Mode.CUBIC_BEZIER;
    }

    @Deprecated
    public void setDrawStepped(boolean enabled) {
        mMode = enabled ? LineDataSet.Mode.STEPPED : LineDataSet.Mode.LINEAR;
    }

    @Deprecated
    @Override
    public boolean isDrawSteppedEnabled() {
        return mMode == LineDataSet.Mode.STEPPED;
    }

    /** ALL CODE BELOW RELATED TO CIRCLE-COLORS */

    /**
     * returns all colors specified for the circles
     *
     * @return
     */
    public List<Integer> getCircleColors() {
        return mCircleColors;
    }

    @Override
    public int getCircleColor(int index) {
        return mCircleColors.get(index % mCircleColors.size());
    }

    /**
     * Sets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. Make sure that the colors
     * are already prepared (by calling getResources().getColor(...)) before
     * adding them to the DataSet.
     *
     * @param colors
     */
    public void setCircleColors(List<Integer> colors) {
        mCircleColors = colors;
    }

    /**
     * Sets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. Make sure that the colors
     * are already prepared (by calling getResources().getColor(...)) before
     * adding them to the DataSet.
     *
     * @param colors
     */
    public void setCircleColors(int[] colors) {
        this.mCircleColors = ColorTemplate.createColors(colors);
    }

    /**
     * ets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. You can use
     * "new String[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     *
     * @param colors
     */
    public void setCircleColors(int[] colors, Context c) {

        List<Integer> clrs = new ArrayList<Integer>();

        for (int color : colors) {
            clrs.add(c.getResources().getColor(color));
        }

        mCircleColors = clrs;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     *
     * @param color
     */
    public void setCircleColor(int color) {
        resetCircleColors();
        mCircleColors.add(color);
    }

    /**
     * resets the circle-colors array and creates a new one
     */
    public void resetCircleColors() {
        mCircleColors = new ArrayList<Integer>();
    }

    /**
     * Sets the color of the inner circle of the line-circles.
     *
     * @param color
     */
    public void setCircleColorHole(int color) {
        mCircleColorHole = color;
    }

    @Override
    public int getCircleHoleColor() {
        return mCircleColorHole;
    }

    /**
     * Set this to true to allow drawing a hole in each data circle.
     *
     * @param enabled
     */
    public void setDrawCircleHole(boolean enabled) {
        mDrawCircleHole = enabled;
    }

    @Override
    public boolean isDrawCircleHoleEnabled() {
        return mDrawCircleHole;
    }

    /**
     * Sets a custom FillFormatter to the chart that handles the position of the
     * filled-line for each DataSet. Set this to null to use the default logic.
     *
     * @param formatter
     */
    public void setFillFormatter(FillFormatter formatter) {

        if (formatter == null)
            mFillFormatter = new DefaultFillFormatter();
        else
            mFillFormatter = formatter;
    }

    @Override
    public FillFormatter getFillFormatter() {
        return mFillFormatter;
    }
}
