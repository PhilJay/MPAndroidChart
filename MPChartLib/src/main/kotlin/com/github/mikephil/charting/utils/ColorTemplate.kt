package com.github.mikephil.charting.utils

import android.content.res.Resources
import android.graphics.Color

/**
 * Ready-made color lists for data sets and small color helpers.
 *
 * The templates are plain lists of ARGB color ints, so you can pass them to a data set's `colors`
 * directly or build your own list with as many colors as you like.
 */
object ColorTemplate {

    /**
     * Sentinel meaning "no color set". Renderers that see it fall back to the data set color or
     * skip drawing the element, for example candle colors, the line circle hole or the radar highlight stroke.
     */
    const val COLOR_NONE = 0x00112233

    /** Sentinel used in legend entries to skip drawing the form for that entry. */
    const val COLOR_SKIP = 0x00112234

    /** Five light blue and teal tones. */
    val LIBERTY_COLORS = listOf(
        Color.rgb(207, 248, 246), Color.rgb(148, 212, 212), Color.rgb(136, 180, 187),
        Color.rgb(118, 174, 175), Color.rgb(42, 109, 130)
    )

    /** Five strong pink, orange, yellow, green and cyan tones. */
    val JOYFUL_COLORS = listOf(
        Color.rgb(217, 80, 138), Color.rgb(254, 149, 7), Color.rgb(254, 247, 120),
        Color.rgb(106, 167, 134), Color.rgb(53, 194, 209)
    )

    /** Five muted blue, green, beige, rose and red tones. */
    val PASTEL_COLORS = listOf(
        Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
        Color.rgb(191, 134, 134), Color.rgb(179, 48, 80)
    )

    /** Five saturated red, orange, yellow, green and brown tones. */
    val COLORFUL_COLORS = listOf(
        Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
        Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)
    )

    /** Five light green, yellow, orange, blue and pink tones. */
    val VORDIPLOM_COLORS = listOf(
        Color.rgb(192, 255, 140), Color.rgb(255, 247, 140), Color.rgb(255, 208, 140),
        Color.rgb(140, 234, 255), Color.rgb(255, 140, 157)
    )

    /** Four material design tones: green, yellow, red and blue. */
    val MATERIAL_COLORS = listOf(
        rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db")
    )

    /**
     * Parses a hex color string such as "#2ecc71" or "2ecc71" into an opaque color int.
     *
     * @throws NumberFormatException if [hex] is not a hexadecimal number.
     */
    fun rgb(hex: String): Int {
        val color = hex.replace("#", "").toLong(16).toInt()
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        return Color.rgb(r, g, b)
    }

    /** The light holo blue from Android 4, rgb(51, 181, 229). */
    val holoBlue: Int
        get() = Color.rgb(51, 181, 229)

    /**
     * Returns [color] with its alpha channel replaced by [alpha].
     *
     * @param alpha 0 for fully transparent to 255 for fully opaque.
     */
    fun colorWithAlpha(color: Int, alpha: Int): Int {
        return (color and 0xffffff) or ((alpha and 0xff) shl 24)
    }

    /**
     * Resolves a list of color resource ids to color ints using [r].
     *
     * @throws Resources.NotFoundException if one of the ids does not exist.
     */
    @Suppress("DEPRECATION")
    fun createColors(r: Resources, colorResIds: List<Int>): List<Int> {
        return colorResIds.map { r.getColor(it) }
    }

}
