package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

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
        val lines = texts(pie(PieEntry(1f, "Top\nBottom"), configure = labelOnly))

        assertEquals(listOf("Top", "Bottom"), lines.map { it.text })
        assertEquals(single.x, lines[0].x, 0.01f)
        assertEquals(single.x, lines[1].x, 0.01f)
        assertEquals(single.y, (lines[0].y + lines[1].y) / 2f, 0.01f)
    }
}
