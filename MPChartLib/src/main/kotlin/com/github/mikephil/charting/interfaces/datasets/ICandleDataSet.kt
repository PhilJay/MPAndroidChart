package com.github.mikephil.charting.interfaces.datasets

import android.graphics.Paint
import com.github.mikephil.charting.data.CandleEntry

/**
 * What the candle stick renderer reads from a data set: candle body width, colors per direction and the
 * shadow line. Implemented by [com.github.mikephil.charting.data.CandleDataSet].
 *
 * Every color may be [com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE] to fall back to the
 * data set color of the entry. That is the default for all of them.
 *
 * @param D the type of the payload attached to each entry.
 */
interface ICandleDataSet<D> : ILineScatterCandleRadarDataSet<CandleEntry<D>> {

    /** Space left free on each side of a candle body, as a fraction of the x step from 0 to 0.45. Default 0.1. */
    val barSpace: Float

    /** True to draw candle bodies; false draws only the open and close ticks on the shadow line. Default true. */
    val showCandleBar: Boolean

    /** Stroke width of the shadow line between high and low in dp. Default 1.5 dp. */
    val shadowWidth: Float

    /** Color of the shadow line, unless [shadowColorSameAsCandle] is true. */
    val shadowColor: Int

    /** Body color when open equals close. */
    val neutralColor: Int

    /** Body color when close is above open. */
    val increasingColor: Int

    /** Body color when close is below open. */
    val decreasingColor: Int

    /** Whether increasing bodies are filled or outlined. Default [Paint.Style.STROKE]. */
    val increasingPaintStyle: Paint.Style

    /** Whether decreasing bodies are filled or outlined. Default [Paint.Style.FILL]. */
    val decreasingPaintStyle: Paint.Style

    /** True to draw the shadow line in the body color of its candle instead of [shadowColor]. Default false. */
    val shadowColorSameAsCandle: Boolean
}
