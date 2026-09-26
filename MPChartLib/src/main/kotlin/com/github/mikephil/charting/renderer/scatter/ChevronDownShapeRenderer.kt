package com.github.mikephil.charting.renderer.scatter

import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Draws a scatter entry as a downward pointing chevron (an open V) with a 1 dp stroke. The tip sits one shape
 * size below the entry position.
 */
public class ChevronDownShapeRenderer : IShapeRenderer {

    override fun renderShape(c: Canvas, dataSet: IScatterDataSet<*>, viewPortHandler: ViewPortHandler, posX: Float, posY: Float, renderPaint: Paint) {
        val shapeHalf = Utils.convertDpToPixel(dataSet.scatterShapeSize) / 2f

        renderPaint.style = Paint.Style.STROKE
        renderPaint.strokeWidth = Utils.convertDpToPixel(1f)

        c.drawLine(posX, posY + (2 * shapeHalf), posX + (2 * shapeHalf), posY, renderPaint)
        c.drawLine(posX, posY + (2 * shapeHalf), posX - (2 * shapeHalf), posY, renderPaint)
    }
}
