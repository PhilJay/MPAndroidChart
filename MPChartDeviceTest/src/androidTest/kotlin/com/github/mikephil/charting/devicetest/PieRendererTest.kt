package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.cos
import kotlin.math.sin

@RunWith(AndroidJUnit4::class)
class PieRendererTest {

    private fun pie(vararg entries: PieEntry<Nothing>, configure: PieChart.(PieDataSet<Nothing>) -> Unit = {}): PieChart {
        val chart = Fixtures.lay { PieChart(Fixtures.context()) }
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.isDrawCenterTextEnabled = false
        val set = PieDataSet(entries.toList(), "pie")
        chart.configure(set)
        chart.data = PieData(set)
        return chart
    }

    private fun texts(chart: PieChart): List<TextDraw> {
        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)
        return canvas.texts
    }

    @Test
    fun aLabelWithLineBreaksIsDrawnAsLinesCenteredOnTheSingleLineSpot() {
        val labelOnly: PieChart.(PieDataSet<Nothing>) -> Unit = { it.isDrawValuesEnabled = false }
        val single = texts(pie(PieEntry(1f, "Single"), configure = labelOnly)).single()
        val twoLines = pie(PieEntry(1f, "Top\nBottom"), configure = labelOnly)
        val lines = texts(twoLines)

        assertEquals(listOf("Top", "Bottom"), lines.map { it.text })
        assertEquals("Top Bottom", twoLines.legend.entries.first().label)
        assertEquals(single.x, lines[0].x, 0.01f)
        assertEquals(single.x, lines[1].x, 0.01f)
        assertEquals(single.y, (lines[0].y + lines[1].y) / 2f, 0.01f)
    }

    /** The pixel of [chart] drawn at [angle] degrees and [radiusPercent] of the pie radius from the center. */
    private fun pixelAt(chart: PieChart, angle: Float, radiusPercent: Float): Int {
        val bitmap = Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888)
        chart.draw(android.graphics.Canvas(bitmap))
        val center = chart.centerCircleBox
        val r = chart.radius * radiusPercent / 100f
        val radians = Math.toRadians(angle.toDouble())
        return bitmap.getPixel((center.x + r * cos(radians)).toInt(), (center.y + r * sin(radians)).toInt())
    }

    @Test
    fun reversedRoundedSlicesBulgeTheOtherWay() {
        fun roundedPie(reversed: Boolean) = pie(PieEntry(1f), PieEntry(1f)) { set ->
            set.colors = listOf(Color.RED, Color.BLUE)
            set.isDrawValuesEnabled = false
            isDrawEntryLabelsEnabled = false
            isDrawRoundedSlicesEnabled = true
            isRoundedSlicesReversed = reversed
            rotationAngle = 270f
        }

        // The red slice starts at the top; the blue one ends there.
        val normal = roundedPie(false)
        assertEquals(Color.BLUE, pixelAt(normal, 262f, 75f))
        assertEquals(Color.BLUE, pixelAt(normal, 278f, 75f))

        val reversed = roundedPie(true)
        assertEquals(Color.RED, pixelAt(reversed, 262f, 75f))
        assertEquals(Color.RED, pixelAt(reversed, 278f, 75f))
    }

    @Test
    fun aClippedTransparentCircleLeavesTheSliceSpaceClear() {
        fun spacedPie(clipped: Boolean) = pie(PieEntry(1f), PieEntry(1f)) { set ->
            set.colors = listOf(Color.RED, Color.BLUE)
            set.sliceSpace = 12f
            set.isDrawValuesEnabled = false
            isDrawEntryLabelsEnabled = false
            transparentCircleRadius = 70f
            isTransparentCircleClippedToSlices = clipped
            rotationAngle = 270f
        }

        // The space between the slices runs straight down from the center.
        assertNotEquals(0, Color.alpha(pixelAt(spacedPie(false), 90f, 60f)))

        val clipped = spacedPie(true)
        assertEquals(0, Color.alpha(pixelAt(clipped, 90f, 60f)))
        assertNotEquals(Color.RED, pixelAt(clipped, 0f, 60f))
        assertEquals(Color.RED, pixelAt(clipped, 0f, 85f))
    }
}
