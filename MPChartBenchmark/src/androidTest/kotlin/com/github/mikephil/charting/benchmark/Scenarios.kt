package com.github.mikephil.charting.benchmark

import android.graphics.Canvas
import android.graphics.Matrix
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.data.ChartData

/**
 * One chart under test: how to fill it with fresh data and how to append a single point to it.
 *
 * @param append adds one point and calls `notifyDataSetChanged`, the way a live feed does.
 */
class Bed(
    val variant: String,
    val points: Int,
    val chart: BarLineChartBase<*>,
    val assign: () -> Unit,
    val append: (() -> Unit)? = null,
)

/** Runs every scenario against [bed] and logs one row per scenario. */
fun runScenarios(bed: Bed) {
    val canvas = Fixtures.canvas()
    val chart = bed.chart
    val variant = bed.variant
    val points = bed.points

    Bench.run("setData", variant, points, warmups = 1, iterations = 5) { bed.assign() }

    bed.assign()

    Bench.run(
        scenario = "drawAll",
        variant = variant,
        points = points,
        setup = { Fixtures.resetViewport(chart) },
    ) { chart.draw(canvas) }

    Bench.run(
        scenario = "panFrame",
        variant = variant,
        points = points,
        iterations = 5,
        unitsPerRun = PAN_STEPS,
        setup = {
            Fixtures.resetViewport(chart)
            Fixtures.zoomToVisiblePoints(chart, points)
        },
    ) {
        repeat(PAN_STEPS) {
            Fixtures.panBy(chart, -PAN_PIXELS)
            chart.draw(canvas)
        }
    }

    Bench.run(
        scenario = "zoomFrame",
        variant = variant,
        points = points,
        iterations = 5,
        unitsPerRun = ZOOM_STEPS,
        setup = { Fixtures.resetViewport(chart) },
    ) {
        repeat(ZOOM_STEPS) {
            val matrix = Matrix(chart.viewPortHandler.matrixTouch)
            matrix.postScale(ZOOM_FACTOR, 1f, 0f, 0f)
            chart.viewPortHandler.refresh(matrix, chart, false)
            chart.draw(canvas)
        }
    }

    Bench.run(
        scenario = "highlight",
        variant = variant,
        points = points,
        warmups = 8,
        iterations = 9,
        unitsPerRun = HIGHLIGHT_LOOKUPS,
        setup = {
            Fixtures.resetViewport(chart)
            Fixtures.zoomToVisiblePoints(chart, points)
        },
    ) {
        val step = Fixtures.WIDTH.toFloat() / HIGHLIGHT_LOOKUPS
        val y = Fixtures.HEIGHT / 2f
        for (i in 0 until HIGHLIGHT_LOOKUPS) chart.getHighlightByTouchPoint(i * step, y)
    }

    val append = bed.append ?: return
    Bench.run(
        scenario = "stream100",
        variant = variant,
        points = points,
        warmups = 4,
        iterations = 7,
        setup = {
            bed.assign()
            Fixtures.resetViewport(chart)
            Fixtures.zoomToVisiblePoints(chart, points)
        },
    ) {
        repeat(STREAM_TICKS) { append() }
        chart.draw(canvas)
    }
}

const val PAN_STEPS = 30
const val PAN_PIXELS = 12f
const val ZOOM_STEPS = 20
const val ZOOM_FACTOR = 1.3f
const val HIGHLIGHT_LOOKUPS = 200
const val STREAM_TICKS = 100

/** Appends one point to [data] and tells [chart] about it. */
fun appendTo(chart: BarLineChartBase<*>, data: ChartData<*>, next: () -> com.github.mikephil.charting.data.Entry<*>) {
    data.addEntry(next(), 0)
    chart.notifyDataSetChanged()
}
