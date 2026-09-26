package com.github.mikephil.charting.renderer.scatter

import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws the shape of one scatter entry. Assign an implementation to `ScatterDataSet.shapeRenderer`, either one
 * of the built-in shapes or a lambda: `IShapeRenderer { c, set, vph, x, y, paint -> c.drawCircle(x, y, 4f, paint) }`.
 */
public fun interface IShapeRenderer {

    /**
     * Draws the shape centered at the pixel position ([posX], [posY]).
     *
     * @param dataSet supplies the shape size and the hole radius, both in dp, and the hole color
     * @param viewPortHandler content bounds of the chart, in pixels
     * @param renderPaint paint already set to the color of the entry; style and stroke width may be changed
     */
    public fun renderShape(c: Canvas, dataSet: IScatterDataSet<*>, viewPortHandler: ViewPortHandler, posX: Float, posY: Float, renderPaint: Paint)
}
