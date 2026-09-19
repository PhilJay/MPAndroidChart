package com.github.mikephil.charting.data

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.ParcelFormatException
import android.os.Parcelable
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs

/**
 * One point in a chart: an x value, a y value, an optional icon and a typed payload.
 * Used directly by line and scatter charts; the other chart types use subclasses.
 * Use the [Entry] factory function when there is no payload; it returns an `Entry<Nothing>`.
 *
 * @param D the type of the payload stored in [data].
 * @property x Position on the x axis in value space (not an index).
 */
open class Entry<out D>(
    var x: Float,
    y: Float,
    icon: Drawable? = null,
    data: D?
) : BaseEntry<D>(y, icon, data), Parcelable {

    /** Returns a new entry with the same x, y, icon and payload. The icon and payload are shared, not copied. */
    open fun copy(): Entry<D> = Entry(x, y, icon, data)

    /**
     * Compares x and y within [Utils.FLOAT_EPSILON] and requires the same payload instance.
     * Unlike `equals`, two separately created entries with the same values are considered equal.
     *
     * @return true if [e] is not null and matches in x, y and payload.
     */
    fun equalTo(e: Entry<*>?): Boolean {
        if (e == null) return false
        if (e.data !== data) return false
        if (abs(e.x - x) > Utils.FLOAT_EPSILON) return false
        if (abs(e.y - y) > Utils.FLOAT_EPSILON) return false
        return true
    }

    override fun toString(): String = "Entry, x: $x y: $y"

    override fun describeContents(): Int = 0

    /**
     * Writes x, y and the payload. The icon is not written.
     *
     * @throws ParcelFormatException when [data] is set but does not implement [Parcelable].
     */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeFloat(x)
        dest.writeFloat(y)
        val data = data
        if (data != null) {
            if (data is Parcelable) {
                dest.writeInt(1)
                dest.writeParcelable(data, flags)
            } else {
                throw ParcelFormatException("Cannot parcel an Entry with non-parcelable data")
            }
        } else {
            dest.writeInt(0)
        }
    }

    companion object {
        /** Recreates entries written by [writeToParcel]. The icon is not restored and the payload is untyped. */
        @JvmField
        val CREATOR: Parcelable.Creator<Entry<Any?>> = object : Parcelable.Creator<Entry<Any?>> {
            override fun createFromParcel(source: Parcel): Entry<Any?> {
                val x = source.readFloat()
                val y = source.readFloat()
                @Suppress("DEPRECATION")
                val data: Any? = if (source.readInt() == 1) source.readParcelable<Parcelable>(Any::class.java.classLoader) else null
                return Entry(x, y, null, data)
            }

            override fun newArray(size: Int): Array<Entry<Any?>?> = arrayOfNulls(size)
        }
    }
}

/**
 * Creates an entry without a payload.
 *
 * @param x position on the x axis in value space.
 * @param y the y value.
 * @param icon drawable drawn at the entry when icons are enabled, or null.
 */
fun Entry(x: Float = 0f, y: Float = 0f, icon: Drawable? = null): Entry<Nothing> = Entry(x, y, icon, null)
