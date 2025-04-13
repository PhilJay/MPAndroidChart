package com.github.mikephil.charting.buffer

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlin.math.abs

open class BarBuffer(size: Int, dataSetCount: Int, containsStacks: Boolean) : AbstractBuffer<IBarDataSet?>(size) {
    protected var dataSetIndex: Int = 0
    protected var dataSetCount: Int = 1

    @JvmField
    protected var containsStacks: Boolean = false

    @JvmField
    protected var inverted: Boolean = false

    /** width of the bar on the x-axis, in values (not pixels)  */
    @JvmField
    protected var barWidth: Float = 1f

    init {
        this.dataSetCount = dataSetCount
        this.containsStacks = containsStacks
    }

    fun setBarWidth(barWidthGiven: Float) {
        this.barWidth = barWidthGiven
    }

    fun setDataSet(index: Int) {
        this.dataSetIndex = index
    }

    fun setInverted(invertedGiven: Boolean) {
        this.inverted = invertedGiven
    }

    protected fun addBar(left: Float, top: Float, right: Float, bottom: Float) {
        buffer[index++] = left
        buffer[index++] = top
        buffer[index++] = right
        buffer[index++] = bottom
    }

    override fun toString(): String {
        return "BarBuffer{" +
                "dataSetIndex=" + dataSetIndex +
                ", dataSetCount=" + dataSetCount +
                ", containsStacks=" + containsStacks +
                ", inverted=" + inverted +
                ", barWidth=" + barWidth +
                ", buffer=" + buffer.contentToString() +
                ", index=" + index +
                '}'
    }

    override fun feed(data: IBarDataSet?) {
        val size = (data?.entryCount ?: 0) * phaseX
        val barWidthHalf = barWidth / 2f

        var i = 0
        while (i < size) {
            val e = data?.getEntryForIndex(i)

            if (e == null) {
                i++
                continue
            }

            val x = e.x
            var y = e.y
            val vals = e.yVals

            if (!containsStacks || vals == null) {
                val left = x - barWidthHalf
                val right = x + barWidthHalf
                var bottom: Float
                var top: Float

                if (inverted) {
                    bottom = if (y >= 0) y else 0f
                    top = if (y <= 0) y else 0f
                } else {
                    top = if (y >= 0) y else 0f
                    bottom = if (y <= 0) y else 0f
                }

                // multiply the height of the rect with the phase
                if (top > 0) top *= phaseY
                else bottom *= phaseY

                addBar(left, top, right, bottom)
            } else {
                var posY = 0f
                var negY = -e.negativeSum
                var yStart: Float

                // fill the stack
                for (k in vals.indices) {
                    val value = vals[k]

                    if (value == 0.0f && (posY == 0.0f || negY == 0.0f)) {
                        // Take care of the situation of a 0.0 value, which overlaps a non-zero bar
                        y = value
                        yStart = y
                    } else if (value >= 0.0f) {
                        y = posY
                        yStart = posY + value
                        posY = yStart
                    } else {
                        y = negY
                        yStart = (negY + abs(value.toDouble())).toFloat()
                        negY += abs(value.toDouble()).toFloat()
                    }

                    val left = x - barWidthHalf
                    val right = x + barWidthHalf
                    var bottom: Float
                    var top: Float

                    if (inverted) {
                        bottom = if (y >= yStart) y else yStart
                        top = if (y <= yStart) y else yStart
                    } else {
                        top = if (y >= yStart) y else yStart
                        bottom = if (y <= yStart) y else yStart
                    }

                    // multiply the height of the rect with the phase
                    top *= phaseY
                    bottom *= phaseY

                    addBar(left, top, right, bottom)
                }
            }
            i++
        }

        reset()
    }
}
