package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.IPieDataSet

/**
 * The slices of a pie chart. A [PieData] holds exactly one of these. Only the y range is tracked, the
 * x range stays at its empty values because pie entries have no x.
 *
 * @param D the payload type of the entries.
 * @param entries the slices in drawing order; an `ArrayList` is kept by reference, other lists are copied.
 * @param label name of the set.
 */
open class PieDataSet<D>(entries: List<PieEntry<D>>, label: String) : DataSet<PieEntry<D>>(entries, label), IPieDataSet<D> {

    /** Gap between slices in dp, clamped to 0 to 20. Default 0. */
    override var sliceSpace = 0f
        set(value) {
            field = value.coerceIn(0f, 20f)
        }

    override var isAutomaticallyDisableSliceSpacingEnabled = false

    override var selectionShift = 9f

    override var xValuePosition = ValuePosition.INSIDE_SLICE

    override var yValuePosition = ValuePosition.INSIDE_SLICE

    override var valueLineColor = 0xff000000.toInt()

    override var isUseValueColorForLineEnabled = false

    override var valueLineWidth = 1.0f

    override var valueLinePart1OffsetPercentage = 75f

    override var valueLinePart1Length = 0.3f

    override var valueLinePart2Length = 0.4f

    override var isValueLineVariableLength = true

    /** Color of a highlighted slice, or null (the default) to keep the slice's own color. */
    override var highlightColor: Int? = null

    override fun copy(): DataSet<PieEntry<D>> {
        val copiedEntries = entries.mapTo(mutableListOf()) { it.copy() }
        val copied = PieDataSet(copiedEntries, label)
        copy(copied)
        return copied
    }

    /** Copies the inherited styling into [pieDataSet]; the pie specific settings are not copied. */
    protected fun copy(pieDataSet: PieDataSet<D>) {
        super.copy(pieDataSet)
    }

    /** Widens only the y range; pie entries have no x. */
    override fun calcMinMax(e: PieEntry<D>) {
        calcMinMaxY(e)
    }

    /** Where a slice's label or value is drawn. */
    enum class ValuePosition {
        /** Inside the slice. */
        INSIDE_SLICE,
        /** Outside the pie, connected to the slice with a line. */
        OUTSIDE_SLICE
    }
}
