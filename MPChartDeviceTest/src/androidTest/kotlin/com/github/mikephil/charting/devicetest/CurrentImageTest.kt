package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/** Writes out what the current sources draw, so the picture can be compared by eye against another build. */
@RunWith(AndroidJUnit4::class)
class CurrentImageTest {

    /** A set holding one value that is not a number must still leave a gap, now that the scan is conditional. */
    @Test fun gap() {
        val entries = Fixtures.lineEntries(400)
        entries[200] = com.github.mikephil.charting.data.Entry(200f, Float.NaN)
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }
        chart.isDecimationEnabled = false
        chart.data = LineData(Fixtures.lineSet(entries))
        Fixtures.resetViewport(chart)
        val bmp = Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888)
        chart.draw(Canvas(bmp))
        File(Fixtures.context().getExternalFilesDir(null), "gap.png").outputStream().use {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @Test fun shot() {
        for (decimation in listOf(true, false)) {
            val chart = Fixtures.lay { LineChart(Fixtures.context()) }
            chart.isDecimationEnabled = decimation
            chart.data = LineData(Fixtures.lineSet(Fixtures.lineEntries(10_000)))
            Fixtures.resetViewport(chart)
            val bmp = Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888)
            chart.draw(Canvas(bmp))
            val name = if (decimation) "current10k-decim.png" else "current10k-nodecim.png"
            File(Fixtures.context().getExternalFilesDir(null), name).outputStream().use {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }
}
