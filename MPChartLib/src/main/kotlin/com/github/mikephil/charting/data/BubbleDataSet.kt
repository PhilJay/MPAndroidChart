package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet

/**
 * One series of bubbles in a bubble chart.
 *
 * @param D the payload type of the entries.
 * @param entries the bubbles, sorted by x; an `ArrayList` is kept by reference, other lists are copied.
 * @param label name of the set, shown in the legend.
 */
open class BubbleDataSet<D>(entries: List<BubbleEntry<D>>, label: String) : BarLineScatterCandleBubbleDataSet<BubbleEntry<D>>(entries, label), IBubbleDataSet<D> {

    /**
     * Largest [BubbleEntry.size] in the set, the reference for scaling bubbles when [isNormalizeSizeEnabled]
     * is on. Adding an entry can only widen it; [calcMinMax] recomputes it from scratch.
     */
    override var maxSize = 0f
        protected set

    override var isNormalizeSizeEnabled = true

    override var highlightCircleWidth = 1f

    /** Recomputes [maxSize] from scratch along with the ranges. */
    override fun calcMinMax() {
        maxSize = 0f
        super.calcMinMax()
    }

    override fun calcMinMax(e: BubbleEntry<D>) {
        super.calcMinMax(e)
        if (e.size > maxSize) maxSize = e.size
    }

    override fun copy(): DataSet<BubbleEntry<D>> {
        val copiedEntries = entries.mapTo(mutableListOf()) { it.copy() }
        val copied = BubbleDataSet(copiedEntries, label)
        copy(copied)
        return copied
    }

    /** Copies the styling into [bubbleDataSet]; the entries are not copied. */
    protected fun copy(bubbleDataSet: BubbleDataSet<D>) {
        super.copy(bubbleDataSet)
        bubbleDataSet.highlightCircleWidth = highlightCircleWidth
        bubbleDataSet.isNormalizeSizeEnabled = isNormalizeSizeEnabled
    }
}
