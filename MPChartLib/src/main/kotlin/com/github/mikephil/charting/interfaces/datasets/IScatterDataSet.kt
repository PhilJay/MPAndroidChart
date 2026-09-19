package com.github.mikephil.charting.interfaces.datasets

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.renderer.scatter.IShapeRenderer

/**
 * What the scatter chart renderer reads from a data set: shape size, hole and the renderer that draws
 * the shape. Implemented by [com.github.mikephil.charting.data.ScatterDataSet].
 *
 * @param D the type of the payload attached to each entry.
 */
interface IScatterDataSet<D> : ILineScatterCandleRadarDataSet<Entry<D>> {

    /** Size of each shape in pixels. Default 15. */
    val scatterShapeSize: Float

    /** Radius of the hole in the middle of each shape, in the same pixels as [scatterShapeSize]. 0 draws no hole. */
    val scatterShapeHoleRadius: Float

    /** Color of the hole. [com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE] leaves it transparent. */
    val scatterShapeHoleColor: Int

    /** Draws the shape for each entry. Default the square renderer. */
    val shapeRenderer: IShapeRenderer
}
