package com.github.mikephil.charting.data

import android.graphics.drawable.Drawable

/**
 * Entry for a radar chart: one value on one axis of the web.
 * [x] is always 0; the axis the value belongs to is the entry's index in the data set.
 * Use the [RadarEntry] factory function when there is no payload.
 *
 * @param D the type of the payload stored in [data].
 */
public open class RadarEntry<out D>(value: Float, icon: Drawable? = null, data: D?) : Entry<D>(0f, value, icon, data) {

    /** Distance from the center of the web; the same as [y]. Assign [y] to change it. */
    public val value: Float
        get() = y

    override fun copy(): RadarEntry<D> = RadarEntry(y, icon, data)
}

/**
 * Creates a radar entry without a payload.
 *
 * @param value distance from the center of the web.
 * @param icon drawable drawn at the entry when icons are enabled, or null.
 */
public fun RadarEntry(value: Float, icon: Drawable? = null): RadarEntry<Nothing> = RadarEntry(value, icon, null)
