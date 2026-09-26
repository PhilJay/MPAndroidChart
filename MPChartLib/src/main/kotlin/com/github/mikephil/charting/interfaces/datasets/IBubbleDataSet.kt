package com.github.mikephil.charting.interfaces.datasets

import com.github.mikephil.charting.data.BubbleEntry

/**
 * What the bubble chart renderer reads from a data set: bubble sizing and the highlight ring.
 * Implemented by [com.github.mikephil.charting.data.BubbleDataSet].
 *
 * @param D the type of the payload attached to each entry.
 */
public interface IBubbleDataSet<D> : IBarLineScatterCandleBubbleDataSet<BubbleEntry<D>> {

    /** Stroke width in dp of the ring drawn around a highlighted bubble. Default 1 dp. */
    public var highlightCircleWidth: Float

    /** Largest entry size in this data set, the reference for normalised bubble sizes. */
    public val maxSize: Float

    /**
     * True to scale bubbles relative to [maxSize] so the largest fills the reference size; false uses the
     * entry size as a direct factor. Default true.
     */
    public val isNormalizeSizeEnabled: Boolean
}
