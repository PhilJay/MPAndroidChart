package com.github.mikephil.charting.benchmark

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.ceil

@RunWith(AndroidJUnit4::class)
class LineRendererTest {

    @Test
    fun denseCirclesAreDrawnAtMostOncePerDiameter() {
        val chart = bareChart()
        val set = Fixtures.lineSet(Fixtures.lineEntries(50_000), circles = true)
        chart.data = LineData(set)
        Fixtures.resetViewport(chart)

        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)

        val diameter = Utils.convertDpToPixel(set.circleRadius) * 2f
        val room = ceil(chart.viewPortHandler.contentWidth / diameter).toInt() + 2
        assertTrue(
            "drew ${canvas.bitmapDraws} circles across ${chart.viewPortHandler.contentWidth}px, room for $room",
            canvas.bitmapDraws <= room,
        )
    }

    @Test
    fun circlesAreDrawnAtTheirEntryPositions() {
        val chart = bareChart()
        val entries = Fixtures.lineEntries(10)
        val set = Fixtures.lineSet(entries, circles = true)
        chart.data = LineData(set)
        Fixtures.resetViewport(chart)

        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)

        val radius = Utils.convertDpToPixel(set.circleRadius)
        val expected = entries.map { entry ->
            val pixel = chart.getPixelForValues(entry.x, entry.y, set.axisDependency)
            (pixel.x.toFloat() - radius) to (pixel.y.toFloat() - radius)
        }

        assertEquals(expected.size, canvas.bitmapCorners.size)
        expected.forEachIndexed { i, (x, y) ->
            assertEquals("circle $i x", x, canvas.bitmapCorners[i].first, 0.01f)
            assertEquals("circle $i y", y, canvas.bitmapCorners[i].second, 0.01f)
        }
    }

    @Test
    fun multiColorLineSpansTheFullContentWidth() {
        val singleColor = rightmostPaintedColumn(colors = 1)
        val multiColor = rightmostPaintedColumn(colors = 8)

        assertTrue(
            "multi colour line stopped at column $multiColor, single colour reached $singleColor",
            multiColor >= singleColor - 5,
        )
    }

    @Test
    fun multiColorLineUsesEveryColorOfTheDataSet() {
        val palette = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
        val chart = bareChart()
        val set = Fixtures.lineSet(Fixtures.lineEntries(400))
        set.colors = palette
        chart.data = LineData(set)
        Fixtures.resetViewport(chart)

        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)

        assertEquals(palette.toSet(), canvas.lineColors.toSet())
    }

    private fun rightmostPaintedColumn(colors: Int): Int {
        val chart = bareChart()
        chart.data = LineData(Fixtures.lineSet(Fixtures.lineEntries(50_000), colors = colors))
        Fixtures.resetViewport(chart)

        val bitmap = Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888)
        chart.draw(android.graphics.Canvas(bitmap))

        val pixels = IntArray(Fixtures.WIDTH * Fixtures.HEIGHT)
        bitmap.getPixels(pixels, 0, Fixtures.WIDTH, 0, 0, Fixtures.WIDTH, Fixtures.HEIGHT)

        for (x in Fixtures.WIDTH - 1 downTo 0) {
            for (y in 0 until Fixtures.HEIGHT) {
                if (pixels[y * Fixtures.WIDTH + x] != 0) return x
            }
        }
        return -1
    }

    private fun bareChart(): LineChart = Fixtures.lay { LineChart(Fixtures.context()) }.apply {
        isDrawGridBackgroundEnabled = false
        xAxis.isEnabled = false
        axisLeft.isEnabled = false
        axisRight.isEnabled = false
        legend.isEnabled = false
        description.isEnabled = false
    }
}
