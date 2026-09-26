package com.github.mikephil.charting.data

import android.graphics.Paint
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * One series of candles in a candle stick chart. The y range covers the candles' low to high values.
 *
 * @param D the payload type of the entries.
 * @param entries the candles, sorted by x; an `ArrayList` is kept by reference, other lists are copied.
 * @param label name of the set, shown in the legend.
 */
public open class CandleDataSet<D>(entries: List<CandleEntry<D>>, label: String) : LineScatterCandleRadarDataSet<CandleEntry<D>>(entries, label), ICandleDataSet<D> {

    override var shadowWidth: Float = 1.5f

    override var showCandleBar: Boolean = true

    /** Space left free on each side of a candle as a fraction of the slot width, clamped to 0 to 0.45. Default 0.1. */
    override var barSpace: Float = 0.1f
        set(value) {
            field = value.coerceIn(0f, 0.45f)
        }

    override var shadowColorSameAsCandle: Boolean = false

    override var increasingPaintStyle: Paint.Style = Paint.Style.STROKE

    override var decreasingPaintStyle: Paint.Style = Paint.Style.FILL

    /** Color for candles with open equal to close. [ColorTemplate.COLOR_NONE], the default, uses the entry color. */
    override var neutralColor: Int = ColorTemplate.COLOR_NONE

    /** Color for candles with close above open. [ColorTemplate.COLOR_NONE], the default, uses the entry color from [colors]. */
    override var increasingColor: Int = ColorTemplate.COLOR_NONE

    /** Color for candles with close below open. [ColorTemplate.COLOR_NONE], the default, uses the entry color from [colors]. */
    override var decreasingColor: Int = ColorTemplate.COLOR_NONE

    /**
     * Color of the shadow lines when [shadowColorSameAsCandle] is off. Set [ColorTemplate.COLOR_NONE] to use
     * the entry color from [colors].
     */
    override var shadowColor: Int = ColorTemplate.COLOR_NONE

    override fun copy(): DataSet<CandleEntry<D>> {
        val copiedEntries = entries.mapTo(mutableListOf()) { it.copy() }
        val copied = CandleDataSet(copiedEntries, label)
        copy(copied)
        return copied
    }

    /** Copies the styling from this set into [candleDataSet]; entries are not copied here. */
    protected fun copy(candleDataSet: CandleDataSet<D>) {
        super.copy(candleDataSet)
        candleDataSet.shadowWidth = shadowWidth
        candleDataSet.showCandleBar = showCandleBar
        candleDataSet.barSpace = barSpace
        candleDataSet.shadowColorSameAsCandle = shadowColorSameAsCandle
        candleDataSet.highlightColor = highlightColor
        candleDataSet.increasingPaintStyle = increasingPaintStyle
        candleDataSet.decreasingPaintStyle = decreasingPaintStyle
        candleDataSet.neutralColor = neutralColor
        candleDataSet.increasingColor = increasingColor
        candleDataSet.decreasingColor = decreasingColor
        candleDataSet.shadowColor = shadowColor
    }

    /** Widens the ranges with the candle's [CandleEntry.low] and [CandleEntry.high] instead of its y. */
    override fun calcMinMax(e: CandleEntry<D>) {
        calcMinMaxY(e)
        calcMinMaxX(e)
    }

    /** Widens the y range with the candle's [CandleEntry.low] and [CandleEntry.high] instead of its y; a candle with a NaN or infinite low or high is left out. */
    override fun calcMinMaxY(e: CandleEntry<D>) {
        if (!e.high.isFinite() || !e.low.isFinite()) return
        if (e.high < yMin) yMin = e.high
        if (e.high > yMax) yMax = e.high
        if (e.low < yMin) yMin = e.low
        if (e.low > yMax) yMax = e.low
    }
}
