package com.github.mikephil.charting.buffer

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Buffer of bar rectangles for one bar data set, in value space. Every bar takes four floats: left, top,
 * right, bottom. Left and right are x values (`x - barWidth / 2` and `x + barWidth / 2`), top and bottom are y
 * values between the bar value and 0. A stacked entry produces one rectangle per stack value. The renderer
 * converts the part of the buffer that was filled to pixels with a `Transformer` afterwards.
 *
 * @param size number of floats, `entryCount * 4` (times the stack size for stacked sets)
 * @property dataSetCount Number of data sets in the bar data.
 * @property containsStacks True when the data set is stacked, so an entry may produce several rectangles.
 */
open class BarBuffer(size: Int, protected val dataSetCount: Int, val containsStacks: Boolean) : AbstractBuffer<IBarDataSet<*>>(size) {

    /**
     * Index of the data set this buffer holds.
     */
    var dataSetIndex = 0

    /**
     * True when the y axis of the data set is inverted; swaps top and bottom so the bar still grows from 0.
     */
    var isInverted = false

    /**
     * Width of a bar in x value units. Default 1.
     */
    var barWidth = 1f

    /**
     * Width of one pixel column in x value units, or 0 while every fed entry stands only for itself. A bar that
     * stands for the other bars of its column is widened to the room they took together, at most this much, so it
     * covers the ground they covered. Rectangles that pad a short stack keep no width at all.
     */
    var columnWidth = 0f

    /**
     * Index of the entry the first rectangle of the last [feed] belongs to.
     */
    var firstEntry = 0
        protected set

    /**
     * How many rectangles one entry takes in the buffer: the stack size of the data set for a stacked set, 1
     * otherwise. An entry with fewer stack values than that is padded, so a bar sits at the same place in the
     * buffer whichever entries were fed.
     */
    var barsPerEntry = 1
        protected set

    /**
     * Number of floats the last [feed] wrote, counted from the start of [buffer]. Whatever follows is left over
     * from an earlier feed and must not be drawn.
     */
    var filledSize = 0
        protected set

    private var fedEntries = IntArray(0)

    private var fedCount = 0

    /**
     * Index of the entry the rectangle at [bar] belongs to, where [bar] counts rectangles from the start of
     * [buffer]. The entries of a feed need not follow one another, so this is the only way back from a rectangle
     * to the entry it was built from.
     */
    fun entryOfBar(bar: Int): Int = fedEntries[bar / barsPerEntry]

    /**
     * Place of the rectangle at [bar] among the bars of the whole data set, which is the index the per-bar
     * settings of a data set are keyed by: the index of its entry times [barsPerEntry], plus its place inside
     * that entry.
     */
    fun positionOfBar(bar: Int): Int = entryOfBar(bar) * barsPerEntry + bar % barsPerEntry

    /**
     * Width in x value units of a bar that stands for [columnEntries] entries: the room their bars took together,
     * at most [columnWidth], and never less than [barWidth]. The rule lives here so that everything drawing a
     * widened bar, the bar shadows included, reads the same number.
     */
    fun widthOfBar(columnEntries: Int): Float = max(barWidth, min(columnWidth, columnEntries * barWidth))

    /**
     * Appends one rectangle (value space) at the write position.
     */
    protected fun addBar(left: Float, top: Float, right: Float, bottom: Float) {
        buffer[index++] = left
        buffer[index++] = top
        buffer[index++] = right
        buffer[index++] = bottom
    }

    /**
     * Fills the buffer with the rectangles of every entry of [data].
     */
    override fun feed(data: IBarDataSet<*>) = feed(data, 0, data.entryCount - 1)

    /**
     * Fills the buffer with one rectangle per bar (or per stack value) of the entries of [data] from [from] to
     * [to], writing from the start of the buffer. The y values are multiplied by the y phase; which entries are
     * fed is up to the caller. Resets the write position afterwards.
     *
     * The bar renderers of this library feed the entries a
     * [com.github.mikephil.charting.renderer.PointReducer] kept and so take the other [feed]. A subclass that
     * overrides this one for geometry of its own is therefore no longer reached; override [addEntry] instead.
     *
     * @param from index of the first entry to feed.
     * @param to index of the last entry to feed, inclusive.
     */
    open fun feed(data: IBarDataSet<*>, from: Int, to: Int) {
        val first = from.coerceAtLeast(0)
        val last = min(to, data.entryCount - 1)

        beginFeed(data, last - first + 1)
        firstEntry = first

        for (i in first..last) appendEntry(data, i, 1)

        endFeed()
    }

    /**
     * Fills the buffer with the bars of the entries the first [count] values of [indices] name, which is how a
     * renderer feeds only the entries a
     * [com.github.mikephil.charting.renderer.PointReducer] kept. The indices have to ascend.
     *
     * @param columnEntries how many entries each of [indices] stands for, which is how wide its bar is drawn.
     */
    open fun feed(data: IBarDataSet<*>, indices: IntArray, columnEntries: IntArray, count: Int) {
        beginFeed(data, count)
        firstEntry = if (count > 0) indices[0] else 0

        for (k in 0 until count) appendEntry(data, indices[k], columnEntries[k])

        endFeed()
    }

    /**
     * Appends the rectangles of the entry at [index] of [data]: one for a plain bar, one per stack value for a
     * stacked entry, padded with rectangles without width up to [barsPerEntry].
     *
     * @param barWidthHalf half the width the bar takes in x value units, which is wider than [barWidth] for a bar
     *   that stands for a whole pixel column.
     */
    protected open fun addEntry(data: IBarDataSet<*>, index: Int, barWidthHalf: Float) {
        val e = data.getEntryForIndex(index)

        val x = e.x
        var y = e.y
        val vals = e.stackValues
        var bars = 0

        if (!containsStacks || vals == null) {
            val left = x - barWidthHalf
            val right = x + barWidthHalf
            var bottom: Float
            var top: Float

            if (isInverted) {
                bottom = if (y >= 0) y else 0f
                top = if (y <= 0) y else 0f
            } else {
                top = if (y >= 0) y else 0f
                bottom = if (y <= 0) y else 0f
            }

            if (top > 0) top *= phaseY else bottom *= phaseY

            addBar(left, top, right, bottom)
            bars++
        } else {
            var posY = 0f
            var negY = -e.negativeSum
            var yStart: Float

            for (value in vals) {
                if (value == 0.0f && (posY == 0.0f || negY == 0.0f)) {
                    y = value
                    yStart = y
                } else if (value >= 0.0f) {
                    y = posY
                    yStart = posY + value
                    posY = yStart
                } else {
                    y = negY
                    yStart = negY + abs(value)
                    negY += abs(value)
                }

                val left = x - barWidthHalf
                val right = x + barWidthHalf
                var bottom: Float
                var top: Float

                if (isInverted) {
                    bottom = if (y >= yStart) y else yStart
                    top = if (y <= yStart) y else yStart
                } else {
                    top = if (y >= yStart) y else yStart
                    bottom = if (y <= yStart) y else yStart
                }

                top *= phaseY
                bottom *= phaseY

                addBar(left, top, right, bottom)
                bars++
            }
        }

        while (bars < barsPerEntry) {
            addBar(x, 0f, x, 0f)
            bars++
        }
    }

    private fun beginFeed(data: IBarDataSet<*>, entries: Int) {
        barsPerEntry = if (containsStacks) data.stackSize else 1

        val needed = entries.coerceAtLeast(0)
        if (fedEntries.size < needed) fedEntries = IntArray(max(needed, fedEntries.size * 2))

        reset()
        filledSize = 0
        fedCount = 0
    }

    private fun appendEntry(data: IBarDataSet<*>, entry: Int, columnEntries: Int) {
        fedEntries[fedCount++] = entry
        addEntry(data, entry, widthOfBar(columnEntries) / 2f)
    }

    private fun endFeed() {
        filledSize = index
        reset()
    }
}
