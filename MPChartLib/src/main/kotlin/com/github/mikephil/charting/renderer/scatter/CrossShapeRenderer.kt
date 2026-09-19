package com.github.mikephil.charting.renderer.scatter

import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws a scatter entry as a plus sign of the shape size with a 1 dp stroke.
 */
class CrossShapeRenderer : IShapeRenderer {

    override fun renderShape(c: Canvas, dataSet: IScatterDataSet<*>, viewPortHandler: ViewPortHandler, posX: Float, posY: Float, renderPaint: Paint) {
        val shapeHalf = dataSet.scatterShapeSize / 2f

        renderPaint.style = Paint.Style.STROKE
        renderPaint.strokeWidth = Utils.convertDpToPixel(1f)

        c.drawLine(posX - shapeHalf, posY, posX + shapeHalf, posY, renderPaint)
        c.drawLine(posX, posY - shapeHalf, posX, posY + shapeHalf, renderPaint)
    }
}
