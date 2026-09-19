package com.github.mikephil.charting.data

import android.graphics.drawable.Drawable

/**
 * Common part of every chart entry: a y value, an optional icon and a read-only payload.
 *
 * @param D the type of the payload; use [Nothing] when there is none.
 * @property y The y value in value space; for pie and radar entries this is the entry's value.
 * @property icon Drawable drawn at the entry's position when the data set has icons enabled, or null for none.
 * @property data Optional payload carried with the entry, for example the object the entry was made from.
 */
abstract class BaseEntry<out D>(
    var y: Float = 0f,
    var icon: Drawable? = null,
    val data: D?
)
