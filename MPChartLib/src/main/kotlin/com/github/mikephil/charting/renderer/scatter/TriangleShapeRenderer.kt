package com.github.mikephil.charting.renderer.scatter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws a scatter entry as an upward pointing triangle of the shape size. With a hole radius on the data
 * set only the outline of that thickness is drawn, and the inner triangle is filled with the hole color when
 * one is set.
 */
class TriangleShapeRenderer : IShapeRenderer {

    /**
     * Path reused for the triangle outline and its hole.
     */
    protected val trianglePathBuffer = Path()

    override fun renderShape(c: Canvas, dataSet: IScatterDataSet<*>, viewPortHandler: ViewPortHandler, posX: Float, posY: Float, renderPaint: Paint) {
        val shapeSize = dataSet.scatterShapeSize
        val shapeHalf = shapeSize / 2f
        val shapeHoleSizeHalf = dataSet.scatterShapeHoleRadius
        val shapeHoleSize = shapeHoleSizeHalf * 2f
        val shapeStrokeSize = (shapeSize - shapeHoleSize) / 2f

        val shapeHoleColor = dataSet.scatterShapeHoleColor

        renderPaint.style = Paint.Style.FILL

        val tri = trianglePathBuffer
        tri.reset()

        tri.moveTo(posX, posY - shapeHalf)
        tri.lineTo(posX + shapeHalf, posY + shapeHalf)
        tri.lineTo(posX - shapeHalf, posY + shapeHalf)

        if (shapeSize > 0.0) {
            tri.lineTo(posX, posY - shapeHalf)

            tri.moveTo(posX - shapeHalf + shapeStrokeSize, posY + shapeHalf - shapeStrokeSize)
            tri.lineTo(posX + shapeHalf - shapeStrokeSize, posY + shapeHalf - shapeStrokeSize)
            tri.lineTo(posX, posY - shapeHalf + shapeStrokeSize)
            tri.lineTo(posX - shapeHalf + shapeStrokeSize, posY + shapeHalf - shapeStrokeSize)
        }

        tri.close()

        c.drawPath(tri, renderPaint)
        tri.reset()

        if (shapeSize > 0.0 && shapeHoleColor != ColorTemplate.COLOR_NONE) {
            renderPaint.color = shapeHoleColor

            tri.moveTo(posX, posY - shapeHalf + shapeStrokeSize)
            tri.lineTo(posX + shapeHalf - shapeStrokeSize, posY + shapeHalf - shapeStrokeSize)
            tri.lineTo(posX - shapeHalf + shapeStrokeSize, posY + shapeHalf - shapeStrokeSize)
            tri.close()

            c.drawPath(tri, renderPaint)
            tri.reset()
        }
    }
}
