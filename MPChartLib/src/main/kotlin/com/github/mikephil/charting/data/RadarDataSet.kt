package com.github.mikephil.charting.data

import android.graphics.Color
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * One polygon in a radar chart. Each entry is the value on one axis of the web, in axis order.
 *
 * @param D the payload type of the entries.
 * @param entries one value per web axis; an `ArrayList` is kept by reference, other lists are copied.
 * @param label name of the set, shown in the legend.
 */
open class RadarDataSet<D>(entries: List<RadarEntry<D>>, label: String) : LineRadarDataSet<RadarEntry<D>>(entries, label), IRadarDataSet<D> {

    /**
     * Radius in dp the corners of the polygon are rounded with. 0, the default, draws sharp corners, and the
     * renderer clamps the radius to half the shortest edge so a small polygon keeps its shape.
     */
    override var cornerRadius = 0f
        set(value) {
            field = value.coerceAtLeast(0f)
        }

    override var isDrawHighlightCircleEnabled = false

    override var highlightCircleFillColor = Color.WHITE

    /** Stroke color of the highlight circle. [ColorTemplate.COLOR_NONE] (the default) uses the set's first color. */
    override var highlightCircleStrokeColor = ColorTemplate.COLOR_NONE

    override var highlightCircleStrokeAlpha = (0.3 * 255).toInt()

    override var highlightCircleInnerRadius = 3.0f

    override var highlightCircleOuterRadius = 4.0f

    override var highlightCircleStrokeWidth = 2.0f

    override fun copy(): DataSet<RadarEntry<D>> {
        val copiedEntries = entries.mapTo(mutableListOf()) { it.copy() }
        val copied = RadarDataSet(copiedEntries, label)
        copy(copied)
        return copied
    }

    /** Copies the styling into [radarDataSet]; the entries are not copied. */
    protected fun copy(radarDataSet: RadarDataSet<D>) {
        super.copy(radarDataSet)
        radarDataSet.cornerRadius = cornerRadius
        radarDataSet.isDrawHighlightCircleEnabled = isDrawHighlightCircleEnabled
        radarDataSet.highlightCircleFillColor = highlightCircleFillColor
        radarDataSet.highlightCircleInnerRadius = highlightCircleInnerRadius
        radarDataSet.highlightCircleStrokeAlpha = highlightCircleStrokeAlpha
        radarDataSet.highlightCircleStrokeColor = highlightCircleStrokeColor
        radarDataSet.highlightCircleStrokeWidth = highlightCircleStrokeWidth
        radarDataSet.highlightCircleOuterRadius = highlightCircleOuterRadius
    }
}
