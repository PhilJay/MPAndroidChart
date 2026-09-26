package com.github.mikephil.charting.interfaces.datasets

import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

/**
 * What the pie chart renderer reads from a data set: slice spacing, selection shift and the placement of
 * value labels with their connector lines. Implemented by [PieDataSet].
 *
 * @param D the type of the payload attached to each entry.
 */
public interface IPieDataSet<D> : IDataSet<PieEntry<D>> {

    /** Gap between neighbouring slices in dp, from 0 to 20. Default 0. */
    public val sliceSpace: Float

    /** True to drop the slice space when the smallest slice would be narrower than the space itself. Default false. */
    public val isAutomaticallyDisableSliceSpacingEnabled: Boolean

    /** Distance in dp a highlighted slice is pushed outwards from the center. Default 9 dp. */
    public val selectionShift: Float

    /** Where the entry labels are drawn, inside or outside the slice. Default [PieDataSet.ValuePosition.INSIDE_SLICE]. */
    public val xValuePosition: PieDataSet.ValuePosition

    /** Where the values are drawn, inside or outside the slice. Default [PieDataSet.ValuePosition.INSIDE_SLICE]. */
    public val yValuePosition: PieDataSet.ValuePosition

    /** Color of the connector line for values drawn outside the slice. Default black. */
    public val valueLineColor: Int

    /** True to draw the connector line in the slice color instead of [valueLineColor]. Default false. */
    public val isUseValueColorForLineEnabled: Boolean

    /** Stroke width of the connector line in dp. Default 1 dp. */
    public val valueLineWidth: Float

    /** Where the connector line starts, as a percentage of the radius measured from the slice edge. Default 75. */
    public val valueLinePart1OffsetPercentage: Float

    /** Length of the first, outward pointing part of the connector line as a fraction of the radius. Default 0.3. */
    public val valueLinePart1Length: Float

    /** Length of the second, horizontal part of the connector line as a fraction of the radius. Default 0.4. */
    public val valueLinePart2Length: Float

    /** True to shorten the connector line for slices near the horizontal center. Default true. */
    public val isValueLineVariableLength: Boolean

    /** Color of a highlighted slice, null to keep the slice color. */
    public val highlightColor: Int?
}
