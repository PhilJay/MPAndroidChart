package com.github.mikephil.charting.renderer

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * The x position and the y range of the entries a [PointReducer] works on. Charts with one y value per entry
 * return that value from both [lowAt] and [highAt]; candle charts return the low and the high.
 *
 * Implement it to reduce something the built-in renderers do not read this way. A custom renderer that draws an
 * ordinary data set needs no implementation of its own:
 * [BarLineScatterCandleBubbleRenderer.reduceVisible] already supplies one.
 */
interface ColumnValues {

    /** X value of the entry at [index], in value space. */
    fun xAt(index: Int): Float

    /** Bottom of the y range of the entry at [index], in value space. */
    fun lowAt(index: Int): Float

    /** Top of the y range of the entry at [index], in value space. */
    fun highAt(index: Int): Float

    /**
     * True while the entries are drawn in sizes of their own, so that the reduction has to ask [sizeAt] and keep
     * the largest of every column. False, the default, for charts whose entries all take the same room.
     */
    val hasSizes: Boolean get() = false

    /**
     * How much of the picture the entry at [index] takes, compared only with the other entries of its column.
     * A bubble chart answers with the size of the bubble; every other chart leaves this at 0 and then nothing is
     * kept for being large. Never read while [hasSizes] is false.
     */
    fun sizeAt(index: Int): Float = 0f
}

/**
 * Picks the entries worth drawing when several of them fall into the same pixel column.
 *
 * Per column it keeps the first entry, the lowest, the highest and the last, so the vertical extent survives
 * and the line enters and leaves the column where it really does. Above [COARSE_POINTS_PER_PIXEL] the column is
 * a solid smear and only the lowest and highest are kept. Below [ACTIVATION_POINTS_PER_PIXEL] nothing is
 * dropped. Where the entries carry sizes of their own, the largest of the column is kept as well, so a bubble
 * big enough to see is never dropped for sitting between two that reach further. An entry without a finite value
 * is always kept, and never counts as an extreme, so the gap it stands for survives. Every index returned is an
 * index of a real entry, so anything that looks entries up still works.
 *
 * A custom renderer reaches the reduction of the entries on screen through
 * [BarLineScatterCandleBubbleRenderer.reducer], and reduces something else by building its own reducer over its
 * own [ColumnValues].
 */
class PointReducer {

    /** Entry indices to draw, ascending. Only the first [count] are meaningful; the array is reused. */
    var indices = IntArray(0)
        private set

    /**
     * How many entries shared the pixel column of the entry at the same place in [indices], which is how many
     * entries that one stands for. Only the first [count] are meaningful; the array is reused.
     */
    var columnCounts = IntArray(0)
        private set

    /** How many entries of [indices] the last [reduce] filled. */
    var count = 0
        private set

    /**
     * Fills [indices] with the entries between [from] and [to] that are worth drawing.
     *
     * @param values the entries to reduce, indexed as the data set indexes them.
     * @param from index of the first entry to consider.
     * @param to index of the last entry to consider, inclusive.
     * @param firstX x value at the left edge of the content rectangle, where column 0 begins; entries left of it
     *   fall into the negative columns, one per pixel, and are reduced like any other.
     * @param valuesPerPixel width of one pixel in value space.
     * @param pixelWidth width of the content rectangle in pixels.
     */
    fun reduce(values: ColumnValues, from: Int, to: Int, firstX: Float, valuesPerPixel: Float, pixelWidth: Float) {
        count = 0
        if (to < from) return

        val entryCount = to - from + 1
        val pointsPerPixel = entryCount / max(pixelWidth, 1f)

        if (pointsPerPixel <= ACTIVATION_POINTS_PER_PIXEL || valuesPerPixel <= 0f) {
            ensureCapacity(entryCount)
            for (i in from..to) {
                columnCounts[count] = 1
                indices[count++] = i
            }
            return
        }

        val keepEntryAndExit = pointsPerPixel <= COARSE_POINTS_PER_PIXEL
        val hasSizes = values.hasSizes
        var index = from
        var indexColumn = columnOf(values, index, firstX, valuesPerPixel)

        while (index <= to) {
            val columnStart = index
            val column = indexColumn
            var lowest = -1
            var highest = -1
            var biggest = -1
            var lowestValue = 0f
            var highestValue = 0f
            var biggestSize = 0f
            var hasGap = false

            // low and high are read once per index here, so the entries themselves need fetching only once.
            while (index <= to && indexColumn == column) {
                val low = values.lowAt(index)
                val high = values.highAt(index)

                if (low.isFinite() && high.isFinite()) {
                    if (lowest < 0 || low < lowestValue) {
                        lowest = index
                        lowestValue = low
                    }
                    if (highest < 0 || high > highestValue) {
                        highest = index
                        highestValue = high
                    }
                    if (hasSizes) {
                        val size = values.sizeAt(index)
                        if (size > biggestSize) {
                            biggest = index
                            biggestSize = size
                        }
                    }
                } else {
                    hasGap = true
                }

                index++
                if (index <= to) indexColumn = columnOf(values, index, firstX, valuesPerPixel)
            }

            val columnEnd = index - 1
            val columnEntries = columnEnd - columnStart + 1

            if (hasGap) {
                appendWithGaps(values, columnStart, columnEnd, lowest, highest, biggest, keepEntryAndExit)
            } else {
                if (keepEntryAndExit) append(columnStart, columnEntries)
                appendExtremes(lowest, highest, biggest, columnEntries)
                if (keepEntryAndExit) append(columnEnd, columnEntries)
            }
        }
    }

    /** Appends the extremes of one column lowest index first, skipping the -1 of an extreme that has none. */
    private fun appendExtremes(lowest: Int, highest: Int, biggest: Int, columnEntries: Int) {
        var first = lowest
        var second = highest
        var third = biggest

        if (first > second) { val swap = first; first = second; second = swap }
        if (second > third) { val swap = second; second = third; third = swap }
        if (first > second) { val swap = first; first = second; second = swap }

        if (first >= 0) append(first, columnEntries)
        if (second >= 0) append(second, columnEntries)
        if (third >= 0) append(third, columnEntries)
    }

    /** True while both ends of the y range of the entry at [index] are real numbers. */
    private fun isFinite(values: ColumnValues, index: Int) =
        values.lowAt(index).isFinite() && values.highAt(index).isFinite()

    /**
     * Appends a column that holds an entry without a value. Every such entry is kept, so the gap it stands for
     * still reaches the renderer, and the extremes are those of the entries around it.
     */
    private fun appendWithGaps(
        values: ColumnValues,
        columnStart: Int,
        columnEnd: Int,
        lowest: Int,
        highest: Int,
        biggest: Int,
        keepEntryAndExit: Boolean
    ) {
        val columnEntries = columnEnd - columnStart + 1

        for (index in columnStart..columnEnd) {
            val isEdge = keepEntryAndExit && (index == columnStart || index == columnEnd)
            if (isEdge || index == lowest || index == highest || index == biggest || !isFinite(values, index)) {
                append(index, columnEntries)
            }
        }
    }

    private fun columnOf(values: ColumnValues, index: Int, firstX: Float, valuesPerPixel: Float): Int =
        floor((values.xAt(index) - firstX) / valuesPerPixel).toInt()

    /** Appends [index] unless it would repeat or precede the last one, which keeps the result ascending. */
    private fun append(index: Int, columnEntries: Int) {
        if (count > 0 && indices[count - 1] >= index) return
        ensureCapacity(count + 1)
        columnCounts[count] = columnEntries
        indices[count++] = index
    }

    /** Fills [indices] with every index from [from] to [to], as if no column held more than one entry. */
    fun reduceNothing(from: Int, to: Int) {
        count = 0
        if (to < from) return
        ensureCapacity(to - from + 1)
        for (i in from..to) {
            columnCounts[count] = 1
            indices[count++] = i
        }
    }

    private fun ensureCapacity(needed: Int) {
        if (indices.size >= needed) return
        val size = max(needed, indices.size * 2)
        indices = indices.copyOf(size)
        columnCounts = columnCounts.copyOf(size)
    }

    companion object {

        /** Points per pixel below which every entry is kept. */
        const val ACTIVATION_POINTS_PER_PIXEL = 1f

        /** Points per pixel above which only the lowest and highest of a column are kept. */
        const val COARSE_POINTS_PER_PIXEL = 8f
    }
}
