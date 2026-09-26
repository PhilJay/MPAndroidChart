package com.github.mikephil.charting.interfaces.datasets

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.renderer.scatter.IShapeRenderer

/**
 * What the scatter chart renderer reads from a data set: shape size, hole and the renderer that draws
 * the shape. Implemented by [com.github.mikephil.charting.data.ScatterDataSet].
 *
 * @param D the type of the payload attached to each entry.
 */
public interface IScatterDataSet<D> : ILineScatterCandleRadarDataSet<Entry<D>> {

    /** Size of each shape in dp. Default 7.5. */
    public val scatterShapeSize: Float

    /** Radius of the hole in the middle of each shape, in dp like [scatterShapeSize]. 0 draws no hole. */
    public val scatterShapeHoleRadius: Float

    /** Color of the hole. [com.github.mikephil.charting.utils.ColorTemplate.COLOR_NONE] leaves it transparent. */
    public val scatterShapeHoleColor: Int

    /** Draws the shape for each entry. Default the square renderer. */
    public val shapeRenderer: IShapeRenderer
}
