package com.github.mikephil.charting.data

import android.graphics.drawable.Drawable

/**
 * Entry for a bubble chart: a point plus the bubble's size.
 * Use the [BubbleEntry] factory function when there is no payload.
 *
 * @param D the type of the payload stored in [data].
 * @property size Size of the bubble in value space. Bubbles are scaled relative to the data set's largest size
 * when [BubbleDataSet.isNormalizeSizeEnabled] is on.
 */
open class BubbleEntry<out D>(
    x: Float,
    y: Float,
    var size: Float,
    icon: Drawable? = null,
    data: D?
) : Entry<D>(x, y, icon, data) {

    override fun copy(): BubbleEntry<D> = BubbleEntry(x, y, size, icon, data)
}

/**
 * Creates a bubble entry without a payload.
 *
 * @param x position on the x axis in value space.
 * @param y position on the y axis in value space.
 * @param size size of the bubble in value space.
 * @param icon drawable drawn at the entry when icons are enabled, or null.
 */
fun BubbleEntry(x: Float, y: Float, size: Float, icon: Drawable? = null): BubbleEntry<Nothing> = BubbleEntry(x, y, size, icon, null)
