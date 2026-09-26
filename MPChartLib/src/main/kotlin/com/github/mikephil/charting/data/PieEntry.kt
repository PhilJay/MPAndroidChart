package com.github.mikephil.charting.data

import android.graphics.drawable.Drawable

/**
 * Entry for a pie chart: one slice with a value and an optional label.
 * A pie entry has no meaningful x; [x] is always 0 and the slice position is the entry's index in the data set.
 * Use the [PieEntry] factory function when there is no payload.
 *
 * @param D the type of the payload stored in [data].
 * @property label Text drawn for the slice, or null for none. Also used for the legend.
 */
public open class PieEntry<out D>(
    value: Float,
    public var label: String? = null,
    icon: Drawable? = null,
    data: D?
) : Entry<D>(0f, value, icon, data) {

    /** Size of the slice; the same as [y]. Assign [y] to change it. */
    public val value: Float
        get() = y

    override fun copy(): PieEntry<D> = PieEntry(y, label, icon, data)
}

/**
 * Creates a pie entry without a payload.
 *
 * @param value size of the slice.
 * @param label text drawn for the slice, or null.
 * @param icon drawable drawn at the slice when icons are enabled, or null.
 */
public fun PieEntry(value: Float, label: String? = null, icon: Drawable? = null): PieEntry<Nothing> = PieEntry(value, label, icon, null)
