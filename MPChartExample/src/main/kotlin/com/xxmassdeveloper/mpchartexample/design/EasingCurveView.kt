package com.xxmassdeveloper.mpchartexample.design

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.animation.Easing.EasingFunction

/** Draws the curve of an easing function from 0 to 1, with room for curves that overshoot, like elastic and back. */
class EasingCurveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var easing: EasingFunction = Easing.Linear
        set(value) {
            field = value
            invalidate()
        }

    var curveColor: Int
        get() = curvePaint.color
        set(value) {
            curvePaint.color = value
            invalidate()
        }

    var guideColor: Int
        get() = guidePaint.color
        set(value) {
            guidePaint.color = value
            invalidate()
        }

    private val density = resources.displayMetrics.density
    private val curvePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.5f * density
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val guidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeWidth = density }
    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        val inset = 3f * density
        val w = width - 2 * inset
        val h = height - 2 * inset
        // The value range -0.4..1.4 keeps overshooting curves inside the view.
        fun y(value: Float) = inset + h * (1.4f - value) / 1.8f

        canvas.drawLine(inset, y(0f), inset + w, y(0f), guidePaint)
        canvas.drawLine(inset, y(1f), inset + w, y(1f), guidePaint)

        path.reset()
        val steps = 60
        for (i in 0..steps) {
            val t = i / steps.toFloat()
            val x = inset + w * t
            if (i == 0) path.moveTo(x, y(easing.getInterpolation(t))) else path.lineTo(x, y(easing.getInterpolation(t)))
        }
        canvas.drawPath(path, curvePaint)
    }
}
