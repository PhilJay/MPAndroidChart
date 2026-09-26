package com.github.mikephil.charting.buffer

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlin.math.abs

/**
 * [BarBuffer] for horizontal bar charts. The four floats per bar are still left, top, right, bottom, but here
 * left and right are y values (the bar value and 0) and top and bottom are x values (`x - barWidth / 2` and
 * `x + barWidth / 2`), matching the swapped axes of the chart.
 */
public class HorizontalBarBuffer(size: Int, dataSetCount: Int, containsStacks: Boolean) : BarBuffer(size, dataSetCount, containsStacks) {

    override fun addEntry(data: IBarDataSet<*>, index: Int, barWidthHalf: Float) {
        val e = data.getEntryForIndex(index)

        val x = e.x
        var y = e.y
        val vals = e.stackValues
        var bars = 0

        if (!containsStacks || vals == null) {
            val bottom = x - barWidthHalf
            val top = x + barWidthHalf
            var left: Float
            var right: Float

            if (isInverted) {
                left = if (y >= 0) y else 0f
                right = if (y <= 0) y else 0f
            } else {
                right = if (y >= 0) y else 0f
                left = if (y <= 0) y else 0f
            }

            if (right > 0) right *= phaseY else left *= phaseY

            addBar(left, top, right, bottom)
            bars++
        } else {
            var posY = 0f
            var negY = -e.negativeSum
            var yStart: Float

            for (value in vals) {
                if (bars >= barsPerEntry) break
                if (value == 0f && (posY == 0f || negY == 0f)) {
                    y = value
                    yStart = y
                } else if (value >= 0f) {
                    y = posY
                    yStart = posY + value
                    posY = yStart
                } else {
                    y = negY
                    yStart = negY + abs(value)
                    negY += abs(value)
                }

                val bottom = x - barWidthHalf
                val top = x + barWidthHalf
                var left: Float
                var right: Float

                if (isInverted) {
                    left = if (y >= yStart) y else yStart
                    right = if (y <= yStart) y else yStart
                } else {
                    right = if (y >= yStart) y else yStart
                    left = if (y <= yStart) y else yStart
                }

                right *= phaseY
                left *= phaseY

                addBar(left, top, right, bottom)
                bars++
            }
        }

        while (bars < barsPerEntry) {
            addBar(0f, x, 0f, x)
            bars++
        }
    }
}
