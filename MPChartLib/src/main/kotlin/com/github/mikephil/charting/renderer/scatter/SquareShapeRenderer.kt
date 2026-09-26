package com.github.mikephil.charting.renderer.scatter

import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws a scatter entry as a square frame whose outer side is the shape size and whose hole has the hole radius
 * of the data set, a full square when the hole radius is 0. The hole is filled with the hole color when one
 * is set. This is the default shape.
 */
public class SquareShapeRenderer : IShapeRenderer {

    override fun renderShape(c: Canvas, dataSet: IScatterDataSet<*>, viewPortHandler: ViewPortHandler, posX: Float, posY: Float, renderPaint: Paint) {
        val shapeSize = Utils.convertDpToPixel(dataSet.scatterShapeSize)
        val shapeHalf = shapeSize / 2f
        val shapeHoleSizeHalf = Utils.convertDpToPixel(dataSet.scatterShapeHoleRadius)
        val shapeHoleSize = shapeHoleSizeHalf * 2f
        val shapeStrokeSize = (shapeSize - shapeHoleSize) / 2f
        val shapeStrokeSizeHalf = shapeStrokeSize / 2f

        val shapeHoleColor = dataSet.scatterShapeHoleColor

        if (shapeSize > 0.0) {
            renderPaint.style = Paint.Style.STROKE
            renderPaint.strokeWidth = shapeStrokeSize

            c.drawRect(
                posX - shapeHoleSizeHalf - shapeStrokeSizeHalf,
                posY - shapeHoleSizeHalf - shapeStrokeSizeHalf,
                posX + shapeHoleSizeHalf + shapeStrokeSizeHalf,
                posY + shapeHoleSizeHalf + shapeStrokeSizeHalf,
                renderPaint
            )

            if (shapeHoleColor != ColorTemplate.COLOR_NONE) {
                renderPaint.style = Paint.Style.FILL
                renderPaint.color = shapeHoleColor
                c.drawRect(
                    posX - shapeHoleSizeHalf,
                    posY - shapeHoleSizeHalf,
                    posX + shapeHoleSizeHalf,
                    posY + shapeHoleSizeHalf,
                    renderPaint
                )
            }
        } else {
            renderPaint.style = Paint.Style.FILL
            c.drawRect(
                posX - shapeHalf,
                posY - shapeHalf,
                posX + shapeHalf,
                posY + shapeHalf,
                renderPaint
            )
        }
    }
}
