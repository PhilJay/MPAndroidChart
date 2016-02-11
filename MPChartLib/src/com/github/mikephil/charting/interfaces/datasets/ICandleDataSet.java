package com.github.mikephil.charting.interfaces.datasets;

import android.graphics.Paint;

import com.github.mikephil.charting.data.CandleEntry;

/**
 * Created by philipp on 21/10/15.
 */
public interface ICandleDataSet extends ILineScatterCandleRadarDataSet<CandleEntry> {

    /**
     * Returns the space that is left out on the left and right side of each
     * candle.
     *
     * @return
     */
    float getBodySpace();

    /**
     * Returns the width of the candle-shadow-line in pixels.
     *
     * @return
     */
    float getShadowWidth();

    /**
     * Returns shadow color for all entries
     *
     * @return
     */
    int getShadowColor();

    /**
     * Returns the neutral color (for open == close)
     *
     * @return
     */
    int getNeutralColor();

    /**
     * Returns the decreasing color (for open > close).
     *
     * @return
     */
    int getDecreasingColor();

    /**
     * Returns the increasing color (for open < close).
     *
     * @return
     */
    int getIncreasingColor();

    /**
     * Returns paint style when open > close
     *
     * @return
     */
    Paint.Style getDecreasingPaintStyle();

    /**
     * Returns paint style when open <= close
     *
     * @return
     */
    Paint.Style getIncreasingPaintStyle();

    /**
     * Is the shadow color same as the candle color?
     *
     * @return
     */
    boolean getShadowColorSameAsCandle();
}
