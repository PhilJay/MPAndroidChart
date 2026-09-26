package com.github.mikephil.charting.devicetest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.MPPointD
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class DecimationTest {

    @Test
    fun drawsFarFewerSegmentsWhenEnabled() {
        val withDecimation = segmentsDrawn(decimation = true)
        val without = segmentsDrawn(decimation = false)

        assertTrue("decimated $withDecimation vs $without", withDecimation < without / 4)
    }

    @Test
    fun drawsEverySegmentWhenDisabled() {
        val chart = chartWith(decimation = false, points = 5_000)
        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)

        // Four floats per segment. XBounds also takes in the entries just off screen, so this is a floor.
        assertTrue(
            "drew ${canvas.lineFloatsDrawn} floats for 5000 entries",
            canvas.lineFloatsDrawn >= 4 * (5_000 - 1),
        )
    }

    @Test
    fun highlightingIgnoresDecimation() {
        val on = highlightAt(decimation = true)
        val off = highlightAt(decimation = false)

        assertEquals(off?.x, on?.x)
        assertEquals(off?.y, on?.y)
        assertEquals(off?.dataSetIndex, on?.dataSetIndex)
    }

    @Test
    fun theFillOfACurveCoversTheColumnsItsLineDoes() {
        val filled = paintedColumns(decimation = true, filled = true)

        assertEquals("decimation moved the fill", paintedColumns(decimation = false, filled = true), filled)
        assertEquals("the fill outran its line", paintedColumns(decimation = true, filled = false), filled)
    }

    @Test
    fun theFillOfAStraightLineCoversTheColumnsItsLineDoes() {
        val mode = LineDataSet.Mode.LINEAR
        val filled = paintedColumns(decimation = true, filled = true, mode = mode)

        assertEquals("decimation moved the fill", paintedColumns(decimation = false, filled = true, mode = mode), filled)
        assertEquals("the fill outran its line", paintedColumns(decimation = true, filled = false, mode = mode), filled)
    }

    @Test
    fun crowdedCandlesAreReducedWithOrWithoutDecimation() {
        val decimated = candleShadows(decimation = true)
        val plain = candleShadows(decimation = false)

        // Eight floats per candle: the shadow is drawn as two lines.
        assertTrue("${plain.lineFloatsDrawn} of ${8 * CANDLES} floats drawn", plain.lineFloatsDrawn < 8 * CANDLES / 3)
        assertEquals("decimation changed a crowded candle chart", plain.lineFloatsDrawn, decimated.lineFloatsDrawn)
        assertEquals(plain.topLineY, decimated.topLineY, 0f)
        assertEquals(plain.bottomLineY, decimated.bottomLineY, 0f)
    }

    @Test
    fun scatterPointsAreReducedAndKeepTheColorOfTheirEntry() {
        val total = 50_000
        val decimated = scatterColorIndices(decimation = true, total = total)
        val plain = scatterColorIndices(decimation = false, total = total)

        assertEquals("with decimation off every point is drawn", total, plain.size)
        assertTrue("decimated ${decimated.size} vs ${plain.size} points", decimated.size < plain.size / 3)
        assertTrue(
            "the last color came from index ${decimated.last()} of $total entries, which is a position, not an entry",
            decimated.last() > total - total / 100,
        )
    }

    @Test
    fun barsAreReducedToTheColumnTheyPaint() {
        val decimated = barsDrawn(decimation = true)
        val plain = barsDrawn(decimation = false)

        assertEquals("with decimation off every bar is drawn", BARS, plain)
        assertTrue("decimated $decimated vs $plain bars", decimated < plain / 4)
    }

    @Test
    fun aDecimatedBarChartPaintsTheSameColumns() {
        val decimated = paintedBarColumns(decimation = true)
        val plain = paintedBarColumns(decimation = false)

        assertEquals("first, last and count of the columns with bars in them", plain, decimated)
    }

    @Test
    fun aDecimatedStackedBarPaintsTheSectionsOfItsOwnEntry() {
        val entries = raggedStacks()
        val set = LabellingBarSet(Fixtures.barSet(entries, stacked = true))
        val chart = Fixtures.lay { BarChart(Fixtures.context()) }.apply {
            isDrawGridBackgroundEnabled = false
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            isDecimationEnabled = true
            data = BarData(set)
            Fixtures.resetViewport(this)
        }

        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)

        // A section without width is the room a short stack leaves unused; nothing is painted there.
        val painted = canvas.rects.filter { it.left != it.right }.map { it.color and 0xFFFFFF }
        val sections = STACKED_BARS * STACK_SIZE

        assertTrue("drew ${painted.size} of $sections sections, so the reduction did nothing", painted.size < sections / 4)

        var previous = -1
        for (position in painted) {
            assertTrue("section $position of $sections sections", position in 0 until sections)
            assertTrue("section $position was painted after $previous", position > previous)
            previous = position

            val carried = entries[position / STACK_SIZE].stackValues?.size ?: 1
            assertTrue("section ${position % STACK_SIZE} of an entry carrying $carried values", position % STACK_SIZE < carried)
        }

        assertTrue(
            "the last section was $previous of $sections, which is a position in the reduction, not a bar of the set",
            previous > sections - sections / 100,
        )
    }

    @Test
    fun theBiggestBubbleOfAColumnSurvivesTheReduction() {
        val decimated = bubbleRadii(decimation = true)
        val plain = bubbleRadii(decimation = false)

        assertEquals("with decimation off every bubble is drawn", BUBBLES, plain.size)
        assertTrue("decimated ${decimated.size} vs ${plain.size} bubbles", decimated.size < plain.size / 4)
        assertEquals("big bubbles an undecimated chart drew", BIG_BUBBLES, plain.count { it > BIG_BUBBLE_PIXELS })
        assertEquals(
            "big bubbles left after the reduction",
            plain.count { it > BIG_BUBBLE_PIXELS },
            decimated.count { it > BIG_BUBBLE_PIXELS },
        )
    }

    @Test
    fun aDenseStretchStaysAsSolidAsTheBarsThatFillIt() {
        val decimated = denseStretchInk(decimation = true)
        val plain = denseStretchInk(decimation = false)

        assertTrue(
            "the dense stretch painted $decimated against $plain without decimation",
            decimated >= plain / 2,
        )
    }

    @Test
    fun anEmptyBarSetDrawsNothing() {
        val empty = barRects(BarData(Fixtures.barSet(emptyList())))

        assertEquals("rectangles drawn for a set without entries", 0, empty)
        assertTrue(
            "rectangles drawn for the set that has entries",
            barRects(BarData(Fixtures.barSet(emptyList()), Fixtures.barSet(Fixtures.barEntries(20)))) > 0,
        )
    }

    /** Bar set whose color for a bar says which bar of the whole set it is, so a painted rect names its own section. */
    private class LabellingBarSet(private val delegate: IBarDataSet<Nothing>) : IBarDataSet<Nothing> by delegate {
        override fun getColor(index: Int) = Color.rgb(index shr 16 and 0xFF, index shr 8 and 0xFF, index and 0xFF)
    }

    /** Entries carrying three, two and one value, so the stack size of the set is larger than most entries need. */
    private fun raggedStacks() = List(STACKED_BARS) {
        when (it % STACK_SIZE) {
            0 -> BarEntry(it.toFloat(), listOf(4f, 6f, 3f))
            1 -> BarEntry(it.toFloat(), listOf(5f, 7f))
            else -> BarEntry(it.toFloat(), 9f)
        }
    }

    private fun barChart(decimation: Boolean): BarChart =
        Fixtures.lay { BarChart(Fixtures.context()) }.apply {
            isDrawGridBackgroundEnabled = false
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            isDecimationEnabled = decimation
            data = BarData(Fixtures.barSet(Fixtures.barEntries(BARS)))
            Fixtures.resetViewport(this)
        }

    private fun barsDrawn(decimation: Boolean): Int {
        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        barChart(decimation).draw(canvas)
        return canvas.rects.size
    }

    /** The first and the last column a bar chart puts ink in, and how many columns it inked between them. */
    private fun paintedBarColumns(decimation: Boolean): Triple<Int, Int, Int> {
        val bitmap = Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888)
        barChart(decimation).draw(Canvas(bitmap))

        val pixels = IntArray(Fixtures.WIDTH * Fixtures.HEIGHT)
        bitmap.getPixels(pixels, 0, Fixtures.WIDTH, 0, 0, Fixtures.WIDTH, Fixtures.HEIGHT)

        val painted = (0 until Fixtures.WIDTH).filter { x ->
            (0 until Fixtures.HEIGHT).any { y -> pixels[y * Fixtures.WIDTH + x] != 0 }
        }
        return Triple(painted.first(), painted.last(), painted.size)
    }

    /**
     * Pixel radii of the bubbles drawn for a set whose x values sit close enough together for a bubble to be tens
     * of pixels wide, whose y values are spread over the whole axis, and where every [BIG_EVERY] entries carries
     * a big bubble. The spread is what matters: a column holds bubbles reaching well above and well below the big
     * one, so keeping only the lowest and the highest of the column would drop it.
     */
    private fun bubbleRadii(decimation: Boolean): List<Float> {
        val random = Random(7)
        val entries = List(BUBBLES) {
            BubbleEntry(it * 0.001f, random.nextFloat() * 100f, if (it % BIG_EVERY == 0) 50f else 1f)
        }
        val chart = Fixtures.lay { BubbleChart(Fixtures.context()) }.apply {
            isDrawGridBackgroundEnabled = false
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            isDecimationEnabled = decimation
            data = BubbleData(Fixtures.bubbleSet(entries))
            Fixtures.resetViewport(this)
        }

        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)
        return canvas.circleRadii
    }

    private fun barRects(data: BarData): Int {
        val chart = Fixtures.lay { BarChart(Fixtures.context()) }.apply {
            isDrawBarShadowEnabled = true
            this.data = data
            Fixtures.resetViewport(this)
        }

        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)
        return canvas.rects.count { it.left != it.right }
    }

    /**
     * How solid the bars are in the columns showing the crowded end of a set whose entries are spread unevenly:
     * [DENSE_BARS] of them share the first [DENSE_X] x values and the rest are strewn over a range ten times as
     * wide. Reported as the mean of the strongest alpha of each of those columns, 255 being a column painted
     * solid. The bars are all the same height, so this measures how much of a column they cover and nothing else.
     */
    private fun denseStretchInk(decimation: Boolean): Int {
        val dense = List(DENSE_BARS) { BarEntry(it * DENSE_X / DENSE_BARS, 100f) }
        val sparse = List(SPARSE_BARS) { BarEntry(DENSE_X + it * DENSE_X * 9f / SPARSE_BARS, 100f) }

        val chart = Fixtures.lay { BarChart(Fixtures.context()) }.apply {
            isDrawGridBackgroundEnabled = false
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            isDecimationEnabled = decimation
            data = BarData(Fixtures.barSet(dense + sparse)).apply { barWidth = DENSE_X / DENSE_BARS * 0.95f }
            Fixtures.resetViewport(this)
        }

        val bitmap = Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888)
        chart.draw(Canvas(bitmap))

        val pixels = IntArray(Fixtures.WIDTH * Fixtures.HEIGHT)
        bitmap.getPixels(pixels, 0, Fixtures.WIDTH, 0, 0, Fixtures.WIDTH, Fixtures.HEIGHT)

        val transformer = chart.getTransformer(YAxis.AxisDependency.LEFT)
        val value = MPPointD.getInstance(0.0, 0.0)
        var columns = 0
        var alphaSum = 0L

        for (x in 0 until Fixtures.WIDTH) {
            transformer.getValuesByTouchPoint(x + 0.5f, 0f, value)
            if (value.x < 0.0 || value.x > DENSE_X) continue

            var strongest = 0
            for (y in 0 until Fixtures.HEIGHT) {
                val alpha = pixels[y * Fixtures.WIDTH + x] ushr 24
                if (alpha > strongest) strongest = alpha
            }
            alphaSum += strongest
            columns++
        }

        MPPointD.recycleInstance(value)
        return if (columns == 0) 0 else (alphaSum / columns).toInt()
    }

    /** Counts the entry indices the scatter renderer asks a color for, which it does once per point it draws. */
    private class CountingScatterSet(private val delegate: IScatterDataSet<Nothing>) : IScatterDataSet<Nothing> by delegate {
        val colorIndices = mutableListOf<Int>()

        override fun getColor(index: Int) = delegate.getColor(index).also { colorIndices.add(index) }
    }

    private fun scatterColorIndices(decimation: Boolean, total: Int): List<Int> {
        val countingSet = CountingScatterSet(Fixtures.scatterSet(Fixtures.lineEntries(total)))
        val chart = Fixtures.lay { ScatterChart(Fixtures.context()) }.apply {
            isDecimationEnabled = decimation
            data = ScatterData(countingSet)
            Fixtures.resetViewport(this)
        }

        chart.draw(Fixtures.canvas())

        return countingSet.colorIndices
    }

    /** Draws [CANDLES] candles with only the shadow lines on the canvas, so their extent is the extent of the data. */
    private fun candleShadows(decimation: Boolean): RecordingCanvas {
        val chart = Fixtures.lay { CandleStickChart(Fixtures.context()) }.apply {
            isDrawGridBackgroundEnabled = false
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            isDecimationEnabled = decimation
            data = CandleData(Fixtures.candleSet(Fixtures.candleEntries(CANDLES)))
            Fixtures.resetViewport(this)
        }

        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)
        return canvas
    }

    /** First and last column a data set paints, which is where a fill that outruns its line shows up. */
    private fun paintedColumns(
        decimation: Boolean,
        filled: Boolean,
        mode: LineDataSet.Mode = LineDataSet.Mode.CUBIC_BEZIER,
    ): Pair<Int, Int> {
        val chart = Fixtures.lay { LineChart(Fixtures.context()) }.apply {
            isDrawGridBackgroundEnabled = false
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            isDecimationEnabled = decimation
            data = LineData(
                Fixtures.lineSet(Fixtures.lineEntries(50_000), mode = mode).apply {
                    isDrawFilledEnabled = filled
                }
            )
            Fixtures.resetViewport(this)
        }

        val bitmap = Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888)
        chart.draw(Canvas(bitmap))

        val pixels = IntArray(Fixtures.WIDTH * Fixtures.HEIGHT)
        bitmap.getPixels(pixels, 0, Fixtures.WIDTH, 0, 0, Fixtures.WIDTH, Fixtures.HEIGHT)

        val painted = (0 until Fixtures.WIDTH).filter { x ->
            (0 until Fixtures.HEIGHT).any { y -> pixels[y * Fixtures.WIDTH + x] != 0 }
        }
        return painted.first() to painted.last()
    }

    private fun chartWith(decimation: Boolean, points: Int): LineChart =
        Fixtures.lay { LineChart(Fixtures.context()) }.apply {
            isDecimationEnabled = decimation
            data = LineData(Fixtures.lineSet(Fixtures.lineEntries(points)))
            Fixtures.resetViewport(this)
        }

    private fun segmentsDrawn(decimation: Boolean): Int {
        val chart = chartWith(decimation, 50_000)
        val canvas = RecordingCanvas(Bitmap.createBitmap(Fixtures.WIDTH, Fixtures.HEIGHT, Bitmap.Config.ARGB_8888))
        chart.draw(canvas)
        return canvas.lineFloatsDrawn
    }

    private fun highlightAt(decimation: Boolean) =
        chartWith(decimation, 50_000).getHighlightByTouchPoint(Fixtures.WIDTH / 2f, Fixtures.HEIGHT / 2f)

    private companion object {
        const val CANDLES = 50_000
        const val BARS = 50_000
        const val STACKED_BARS = 30_000
        const val STACK_SIZE = 3
        const val BUBBLES = 20_000
        const val BIG_EVERY = 500
        const val BIG_BUBBLES = BUBBLES / BIG_EVERY
        const val BIG_BUBBLE_PIXELS = 10f
        const val DENSE_BARS = 19_000
        const val SPARSE_BARS = 1_000
        const val DENSE_X = 1_000f
    }
}
