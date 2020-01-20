package com.xxmassdeveloper.mpchartexample.custom.utils

import android.graphics.PointF
import android.graphics.RectF

class Extensions {

    companion object {
        fun RectF.minus(o: RectF) = RectF(left - o.left, right - o.right, top - o.top, bottom - o.bottom)

        // compares two RectF, ignoring rounding errors
        fun PointF.almostEquals(a: PointF): Boolean {
            return Math.abs(a.x - x) < 0.0001 && Math.abs(a.y - y) < 0.0001
        }
    }



}