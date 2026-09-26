package com.github.mikephil.charting.data.filter

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * Thins out a polyline with the Douglas-Peucker algorithm: points closer than a tolerance to the line
 * between their neighbours are dropped. Useful to draw large data sets with fewer points.
 */
public class Approximator {

    /**
     * Reduces [points] so that no dropped point is farther than [tolerance] from the simplified line.
     * The first and last point are always kept.
     *
     * @param points the polyline as a flat array `x0, y0, x1, y1, ...`, in any consistent unit (value space or
     * pixels; [tolerance] is measured in the same unit). With two points or fewer it is returned as a copy.
     * @param tolerance largest allowed distance between a dropped point and the simplified line.
     * @return the kept points in the same flat layout.
     */
    public fun reduceWithDouglasPeucker(points: FloatArray, tolerance: Float): FloatArray {
        if (points.size <= 4) return points.copyOf()

        var greatestIndex = 0
        var greatestDistance = 0f

        val line = Line(points[0], points[1], points[points.size - 2], points[points.size - 1])

        var i = 2
        while (i < points.size - 2) {
            val distance = line.distance(points[i], points[i + 1])
            if (distance > greatestDistance) {
                greatestDistance = distance
                greatestIndex = i
            }
            i += 2
        }

        return if (greatestDistance > tolerance) {
            val reduced1 = reduceWithDouglasPeucker(points.copyOfRange(0, greatestIndex + 2), tolerance)
            val reduced2 = reduceWithDouglasPeucker(points.copyOfRange(greatestIndex, points.size), tolerance)
            reduced1 + reduced2.copyOfRange(2, reduced2.size)
        } else {
            line.points
        }
    }

    private class Line(x1: Float, y1: Float, x2: Float, y2: Float) {

        val points = floatArrayOf(x1, y1, x2, y2)
        private val dx = x1 - x2
        private val dy = y1 - y2
        private val sxey = x1 * y2
        private val exsy = x2 * y1
        private val length = sqrt(dx * dx + dy * dy)

        fun distance(x: Float, y: Float): Float {
            if (length == 0f) return hypot(x - points[0], y - points[1])
            return abs(dy * x - dx * y + sxey - exsy) / length
        }
    }
}
