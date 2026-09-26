package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

/** Renders the same 100k data at several points-per-pixel levels, once decimated and once not, for visual comparison. */
@RunWith(AndroidJUnit4::class)
class DecimationImageTest {

    private val TAG = "MPBENCH"

    @Test
    fun writesComparisonImages() {
        val total = 100_000
        val dir = File(Fixtures.context().getExternalFilesDir(null), "decimation")
        dir.mkdirs()

        for (perPixel in listOf(2, 5, 10, 50)) {
            for (decimation in listOf(true, false)) {
                val chart = Fixtures.lay { LineChart(Fixtures.context()) }
                chart.isDecimationEnabled = decimation
                chart.data = LineData(Fixtures.lineSet(Fixtures.lineEntries(total)))
                Fixtures.resetViewport(chart)
                Fixtures.zoomToVisiblePoints(chart, total, Fixtures.WIDTH * perPixel)

                val bitmap = Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888)
                chart.draw(Canvas(bitmap))

                val file = File(dir, "ppx$perPixel-${if (decimation) "on" else "off"}.png")
                FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                Log.i(TAG, "IMAGE ${file.absolutePath}")
            }
        }
    }

    @Test
    fun writesBarComparisonImages() {
        val total = 100_000
        val dir = File(Fixtures.context().getExternalFilesDir(null), "decimation")
        dir.mkdirs()

        for (perPixel in listOf(2, 10, 50)) {
            for (decimation in listOf(true, false)) {
                val chart = Fixtures.lay { BarChart(Fixtures.context()) }
                chart.isDrawGridBackgroundEnabled = false
                chart.xAxis.isEnabled = false
                chart.axisLeft.isEnabled = false
                chart.axisRight.isEnabled = false
                chart.legend.isEnabled = false
                chart.description.isEnabled = false
                chart.isDecimationEnabled = decimation
                chart.data = BarData(Fixtures.barSet(Fixtures.barEntries(total)))
                Fixtures.resetViewport(chart)
                Fixtures.zoomToVisiblePoints(chart, total, Fixtures.WIDTH * perPixel)

                val bitmap = Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888)
                chart.draw(Canvas(bitmap))

                val file = File(dir, "bar-ppx$perPixel-${if (decimation) "on" else "off"}.png")
                FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                Log.i(TAG, "IMAGE ${file.absolutePath} ${ink(bitmap)}")
            }
        }
    }

    /** How much of the picture the bars painted: the share of columns with ink in them and how solid that ink is. */
    private fun ink(bitmap: Bitmap): String {
        val pixels = IntArray(Fixtures.WIDTH * Fixtures.HEIGHT)
        bitmap.getPixels(pixels, 0, Fixtures.WIDTH, 0, 0, Fixtures.WIDTH, Fixtures.HEIGHT)

        var painted = 0
        var alphaSum = 0L

        for (x in 0 until Fixtures.WIDTH) {
            var strongest = 0
            for (y in 0 until Fixtures.HEIGHT) {
                val alpha = pixels[y * Fixtures.WIDTH + x] ushr 24
                if (alpha > strongest) strongest = alpha
            }
            if (strongest > 0) painted++
            alphaSum += strongest
        }

        return "columnsWithInk=$painted/${Fixtures.WIDTH} meanStrongestAlpha=${alphaSum / Fixtures.WIDTH}"
    }
}
