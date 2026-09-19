package com.github.mikephil.charting.data

import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.renderer.scatter.ChevronDownShapeRenderer
import com.github.mikephil.charting.renderer.scatter.ChevronUpShapeRenderer
import com.github.mikephil.charting.renderer.scatter.CircleShapeRenderer
import com.github.mikephil.charting.renderer.scatter.CrossShapeRenderer
import com.github.mikephil.charting.renderer.scatter.IShapeRenderer
import com.github.mikephil.charting.renderer.scatter.SquareShapeRenderer
import com.github.mikephil.charting.renderer.scatter.TriangleShapeRenderer
import com.github.mikephil.charting.renderer.scatter.XShapeRenderer
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * One series of points in a scatter chart, all drawn with the same shape.
 *
 * @param D the payload type of the entries.
 * @param entries the points, sorted by x; an `ArrayList` is kept by reference, other lists are copied.
 * @param label name of the set, shown in the legend.
 */
open class ScatterDataSet<D>(entries: List<Entry<D>>, label: String) : LineScatterCandleRadarDataSet<Entry<D>>(entries, label), IScatterDataSet<D> {

    override var scatterShapeSize = 15f

    /** Draws the shape at each point. Default a square; use [setScatterShape] for the built-in shapes. */
    override var shapeRenderer: IShapeRenderer = SquareShapeRenderer()

    override var scatterShapeHoleRadius = 0f

    /** Color of the hole inside each shape. [ColorTemplate.COLOR_NONE], the default, leaves the hole transparent. */
    override var scatterShapeHoleColor = ColorTemplate.COLOR_NONE

    override fun copy(): DataSet<Entry<D>> {
        val copiedEntries = entries.mapTo(mutableListOf()) { it.copy() }
        val copied = ScatterDataSet(copiedEntries, label)
        copy(copied)
        return copied
    }

    /** Copies the styling from this set into [scatterDataSet]; entries are not copied, the renderer is shared. */
    protected fun copy(scatterDataSet: ScatterDataSet<D>) {
        super.copy(scatterDataSet)
        scatterDataSet.scatterShapeSize = scatterShapeSize
        scatterDataSet.shapeRenderer = shapeRenderer
        scatterDataSet.scatterShapeHoleRadius = scatterShapeHoleRadius
        scatterDataSet.scatterShapeHoleColor = scatterShapeHoleColor
    }

    /** Replaces [shapeRenderer] with the built-in renderer for [shape]. */
    fun setScatterShape(shape: ScatterChart.ScatterShape) {
        shapeRenderer = getRendererForShape(shape)
    }

    companion object {

        /** Returns a new built-in renderer that draws [shape]. */
        fun getRendererForShape(shape: ScatterChart.ScatterShape): IShapeRenderer {
            return when (shape) {
                ScatterChart.ScatterShape.SQUARE -> SquareShapeRenderer()
                ScatterChart.ScatterShape.CIRCLE -> CircleShapeRenderer()
                ScatterChart.ScatterShape.TRIANGLE -> TriangleShapeRenderer()
                ScatterChart.ScatterShape.CROSS -> CrossShapeRenderer()
                ScatterChart.ScatterShape.X -> XShapeRenderer()
                ScatterChart.ScatterShape.CHEVRON_UP -> ChevronUpShapeRenderer()
                ScatterChart.ScatterShape.CHEVRON_DOWN -> ChevronDownShapeRenderer()
            }
        }
    }
}
