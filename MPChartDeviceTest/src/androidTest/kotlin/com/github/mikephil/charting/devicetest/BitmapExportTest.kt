package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapExportTest {

    private fun <T> onMain(block: () -> T): T {
        var result: T? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync { result = block() }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    private fun drawnPixels(bitmap: Bitmap): Int {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return pixels.count { it != Color.WHITE }
    }

    @Test
    fun aChartThatWasNeverLaidOutExportsAtTheAskedSize() {
        val (unsized, exported) = onMain {
            val chart = LineChart(Fixtures.context())
            chart.data = LineData(Fixtures.lineSet(Fixtures.lineEntries(50)))
            chart.toBitmap() to chart.toBitmap(400, 300)
        }

        assertEquals(1, unsized.width)
        assertEquals(1, unsized.height)
        assertEquals(400, exported.width)
        assertEquals(300, exported.height)
        assertTrue("the exported image holds no chart", drawnPixels(exported) > 1000)
    }

    @Test
    fun aChartOnScreenExportsAtAnotherSizeAndKeepsItsOwn() {
        val chart = Fixtures.lay { LineChart(Fixtures.context()).apply { data = LineData(Fixtures.lineSet(Fixtures.lineEntries(50))) } }
        val contentBefore = onMain { chart.viewPortHandler.contentRect.width() }

        val exported = onMain { chart.toBitmap(Fixtures.WIDTH / 2, Fixtures.HEIGHT / 2) }

        assertEquals(Fixtures.WIDTH / 2, exported.width)
        assertEquals(Fixtures.HEIGHT / 2, exported.height)
        assertTrue("the exported image holds no chart", drawnPixels(exported) > 1000)
        onMain {
            assertEquals(Fixtures.WIDTH, chart.width)
            assertEquals(Fixtures.HEIGHT, chart.height)
            assertEquals(contentBefore, chart.viewPortHandler.contentRect.width(), 0.5f)
        }
    }
}
