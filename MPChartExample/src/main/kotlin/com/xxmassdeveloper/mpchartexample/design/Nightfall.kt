package com.xxmassdeveloper.mpchartexample.design

import android.graphics.Color

/** Colors of the Nightfall design in its dark and light variant. */
class Nightfall private constructor(
    val isDark: Boolean,
    val stage: Int,
    val card: Int,
    val text: Int,
    val muted: Int,
    val grid: Int,
    val track: Int,
    val pillBackground: Int,
    val pillText: Int,
) {
    companion object {
        val palette = listOf(0xFF2FB4B6, 0xFFF97066, 0xFFF5B041, 0xFF8B7CF6, 0xFF5DD39E).map { it.toInt() }
        val accent = palette[0]
        val coral = palette[1]
        val amber = palette[2]
        val violet = palette[3]
        val green = palette[4]

        val dark = Nightfall(
            isDark = true,
            stage = 0xFF0F172A.toInt(),
            card = 0xFF111C33.toInt(),
            text = 0xFFE2E8F0.toInt(),
            muted = 0xFF94A3B8.toInt(),
            grid = Color.argb(36, 148, 163, 184),
            track = Color.argb(26, 148, 163, 184),
            pillBackground = 0xFFE2E8F0.toInt(),
            pillText = 0xFF0F172A.toInt(),
        )

        val light = Nightfall(
            isDark = false,
            stage = 0xFFF8FAFC.toInt(),
            card = 0xFFFFFFFF.toInt(),
            text = 0xFF0F172A.toInt(),
            muted = 0xFF64748B.toInt(),
            grid = 0xFFE2E8F0.toInt(),
            track = 0xFFEEF2F7.toInt(),
            pillBackground = 0xFF0F172A.toInt(),
            pillText = 0xFFFFFFFF.toInt(),
        )

        fun withAlpha(color: Int, alpha: Int): Int = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }
}
