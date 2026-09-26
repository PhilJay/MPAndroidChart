package com.github.mikephil.charting.data

import android.graphics.drawable.Drawable
import kotlin.math.abs

/**
 * Entry for a candle stick chart with the high, low, open and close values of one candle.
 * The inherited [y] is set once at construction to the middle between [high] and [low] and does not follow
 * later changes to those properties. Use the [CandleEntry] factory function when there is no payload.
 *
 * @param D the type of the payload stored in [data].
 * @property high Highest value, the top of the upper shadow.
 * @property low Lowest value, the bottom of the lower shadow.
 * @property open Value at the start of the period, one end of the body.
 * @property close Value at the end of the period, the other end of the body.
 */
public open class CandleEntry<out D>(
    x: Float,
    public var high: Float,
    public var low: Float,
    public var open: Float,
    public var close: Float,
    icon: Drawable? = null,
    data: D?
) : Entry<D>(x, (high + low) / 2f, icon, data) {

    /** Distance between [high] and [low], the full height of the candle including shadows. */
    public val shadowRange: Float
        get() = abs(high - low)

    /** Distance between [open] and [close], the height of the candle body. */
    public val bodyRange: Float
        get() = abs(open - close)

    override fun copy(): CandleEntry<D> = CandleEntry(x, high, low, open, close, icon, data)
}

/**
 * Creates a candle entry without a payload.
 *
 * @param x position on the x axis in value space.
 * @param high highest value of the candle.
 * @param low lowest value of the candle.
 * @param open value at the start of the period.
 * @param close value at the end of the period.
 * @param icon drawable drawn at the entry when icons are enabled, or null.
 */
public fun CandleEntry(x: Float, high: Float, low: Float, open: Float, close: Float, icon: Drawable? = null): CandleEntry<Nothing> =
    CandleEntry(x, high, low, open, close, icon, null)
