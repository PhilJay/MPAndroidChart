package com.github.mikephil.charting.data

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.interfaces.datasets.ILineRadarDataSet

/**
 * Base for line and radar data sets: a stroked line with an optional filled area under it.
 *
 * @param T the entry type this set holds.
 */
public abstract class LineRadarDataSet<T : Entry<*>>(entries: List<T>, label: String) :
    LineScatterCandleRadarDataSet<T>(entries, label), ILineRadarDataSet<T> {

    /** Color of the filled area. Assigning it clears [fillDrawable], which otherwise takes precedence. */
    override var fillColor: Int = Color.rgb(140, 234, 255)
        set(value) {
            field = value
            fillDrawable = null
        }

    override var fillDrawable: Drawable? = null

    override var fillAlpha: Int = 85

    /** Width of the line in dp, clamped to 0 to 10. Default 1. Thinner lines draw faster. */
    override var lineWidth: Float = 1f
        set(value) {
            field = value.coerceIn(0f, 10f)
        }

    override var isDrawFilledEnabled: Boolean = false

    /** Copies the styling from this set into [dataSet]; entries are not copied here. */
    protected open fun copy(dataSet: LineRadarDataSet<*>) {
        super.copy(dataSet)
        dataSet.isDrawFilledEnabled = isDrawFilledEnabled
        dataSet.fillAlpha = fillAlpha
        dataSet.fillColor = fillColor
        dataSet.fillDrawable = fillDrawable
        dataSet.lineWidth = lineWidth
    }
}
