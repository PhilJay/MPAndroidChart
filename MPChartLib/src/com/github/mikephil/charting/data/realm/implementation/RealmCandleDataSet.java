package com.github.mikephil.charting.data.realm.implementation;

import android.graphics.Paint;

import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.realm.base.RealmLineScatterCandleRadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import io.realm.DynamicRealmObject;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Philipp Jahoda on 07/11/15.
 */
public class RealmCandleDataSet<T extends RealmObject> extends RealmLineScatterCandleRadarDataSet<T, CandleEntry> implements ICandleDataSet {

    private String mHighField;
    private String mLowField;
    private String mOpenField;
    private String mCloseField;

    /**
     * the width of the shadow of the candle
     */
    private float mShadowWidth = 3f;

    /**
     * should the candle bars show?
     * when false, only "ticks" will show
     *
     * - default: true
     */
    private boolean mShowCandleBar = true;

    /**
     * the space between the candle entries, default 0.1f (10%)
     */
    private float mBarSpace = 0.1f;

    /**
     * use candle color for the shadow
     */
    private boolean mShadowColorSameAsCandle = false;

    /**
     * paint style when open < close
     * increasing candlesticks are traditionally hollow
     */
    protected Paint.Style mIncreasingPaintStyle = Paint.Style.STROKE;

    /**
     * paint style when open > close
     * descreasing candlesticks are traditionally filled
     */
    protected Paint.Style mDecreasingPaintStyle = Paint.Style.FILL;

    /**
     * color for open == close
     */
    protected int mNeutralColor = ColorTemplate.COLOR_NONE;

    /**
     * color for open < close
     */
    protected int mIncreasingColor = ColorTemplate.COLOR_NONE;

    /**
     * color for open > close
     */
    protected int mDecreasingColor = ColorTemplate.COLOR_NONE;

    /**
     * shadow line color, set -1 for backward compatibility and uses default
     * color
     */
    protected int mShadowColor = ColorTemplate.COLOR_NONE;

    /**
     * Constructor for creating a LineDataSet with realm data.
     *
     * @param result     the queried results from the realm database
     * @param highField  the name of the field in your data object that represents the "high" value
     * @param lowField   the name of the field in your data object that represents the "low" value
     * @param openField  the name of the field in your data object that represents the "open" value
     * @param closeField the name of the field in your data object that represents the "close" value
     */
    public RealmCandleDataSet(RealmResults<T> result, String highField, String lowField, String openField, String closeField) {
        super(result, null);
        this.mHighField = highField;
        this.mLowField = lowField;
        this.mOpenField = openField;
        this.mCloseField = closeField;

        build(this.results);
        calcMinMax(0, this.results.size());
    }

    /**
     * Constructor for creating a LineDataSet with realm data.
     *
     * @param result      the queried results from the realm database
     * @param highField   the name of the field in your data object that represents the "high" value
     * @param lowField    the name of the field in your data object that represents the "low" value
     * @param openField   the name of the field in your data object that represents the "open" value
     * @param closeField  the name of the field in your data object that represents the "close" value
     * @param xIndexField the name of the field in your data object that represents the x-index
     */
    public RealmCandleDataSet(RealmResults<T> result, String highField, String lowField, String openField, String closeField, String xIndexField) {
        super(result, null, xIndexField);
        this.mHighField = highField;
        this.mLowField = lowField;
        this.mOpenField = openField;
        this.mCloseField = closeField;

        build(this.results);
        calcMinMax(0, this.results.size());
    }

    @Override
    public void build(RealmResults<T> results) {

        if (mIndexField == null) {

            int xIndex = 0;

            for (T object : results) {

                DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
                mValues.add(new CandleEntry(xIndex, dynamicObject.getFloat(mHighField), dynamicObject.getFloat(mLowField),
                        dynamicObject.getFloat(mOpenField), dynamicObject.getFloat(mCloseField)));
                xIndex++;
            }
        } else {

            for (T object : results) {

                DynamicRealmObject dynamicObject = new DynamicRealmObject(object);
                mValues.add(new CandleEntry(dynamicObject.getInt(mIndexField), dynamicObject.getFloat(mHighField), dynamicObject.getFloat(mLowField),
                        dynamicObject.getFloat(mOpenField), dynamicObject.getFloat(mCloseField)));
            }
        }
    }

    @Override
    public void calcMinMax(int start, int end) {

        if (mValues == null)
            return;

        if (mValues.size() == 0)
            return;

        int endValue;

        if (end == 0 || end >= mValues.size())
            endValue = mValues.size() - 1;
        else
            endValue = end;

        mYMin = Float.MAX_VALUE;
        mYMax = -Float.MAX_VALUE;

        for (int i = start; i <= endValue; i++) {

            CandleEntry e = mValues.get(i);

            if (e.getLow() < mYMin)
                mYMin = e.getLow();

            if (e.getHigh() > mYMax)
                mYMax = e.getHigh();
        }
    }

    /**
     * Sets the space that is left out on the left and right side of each
     * candle, default 0.1f (10%), max 0.45f, min 0f
     *
     * @param space
     */
    public void setBarSpace(float space) {

        if (space < 0f)
            space = 0f;
        if (space > 0.45f)
            space = 0.45f;

        mBarSpace = space;
    }

    @Override
    public float getBarSpace() {
        return mBarSpace;
    }

    /**
     * Sets the width of the candle-shadow-line in pixels. Default 3f.
     *
     * @param width
     */
    public void setShadowWidth(float width) {
        mShadowWidth = Utils.convertDpToPixel(width);
    }

    @Override
    public float getShadowWidth() {
        return mShadowWidth;
    }

    /**
     * Sets whether the candle bars should show?
     *
     * @param showCandleBar
     */
    public void setShowCandleBar(boolean showCandleBar) {
        mShowCandleBar = showCandleBar;
    }

    @Override
    public boolean getShowCandleBar() {
        return mShowCandleBar;
    }


    /** BELOW THIS COLOR HANDLING */

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open == close.
     *
     * @param color
     */
    public void setNeutralColor(int color) {
        mNeutralColor = color;
    }

    @Override
    public int getNeutralColor() {
        return mNeutralColor;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open < close.
     *
     * @param color
     */
    public void setIncreasingColor(int color) {
        mIncreasingColor = color;
    }

    @Override
    public int getIncreasingColor() {
        return mIncreasingColor;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open > close.
     *
     * @param color
     */
    public void setDecreasingColor(int color) {
        mDecreasingColor = color;
    }

    @Override
    public int getDecreasingColor() {
        return mDecreasingColor;
    }

    @Override
    public Paint.Style getIncreasingPaintStyle() {
        return mIncreasingPaintStyle;
    }

    /**
     * Sets paint style when open < close
     *
     * @param paintStyle
     */
    public void setIncreasingPaintStyle(Paint.Style paintStyle) {
        this.mIncreasingPaintStyle = paintStyle;
    }

    @Override
    public Paint.Style getDecreasingPaintStyle() {
        return mDecreasingPaintStyle;
    }

    /**
     * Sets paint style when open > close
     *
     * @param decreasingPaintStyle
     */
    public void setDecreasingPaintStyle(Paint.Style decreasingPaintStyle) {
        this.mDecreasingPaintStyle = decreasingPaintStyle;
    }

    @Override
    public int getShadowColor() {
        return mShadowColor;
    }

    /**
     * Sets shadow color for all entries
     *
     * @param shadowColor
     */
    public void setShadowColor(int shadowColor) {
        this.mShadowColor = shadowColor;
    }

    @Override
    public boolean getShadowColorSameAsCandle() {
        return mShadowColorSameAsCandle;
    }

    /**
     * Sets shadow color to be the same color as the candle color
     *
     * @param shadowColorSameAsCandle
     */
    public void setShadowColorSameAsCandle(boolean shadowColorSameAsCandle) {
        this.mShadowColorSameAsCandle = shadowColorSameAsCandle;
    }
}
