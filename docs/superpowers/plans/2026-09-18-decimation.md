# Point decimation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make a chart frame cost the same whatever the data set holds, by drawing only the entries a pixel column can actually show.

**Architecture:** One shared `PointReducer` turns a visible index range into a shorter list of entry indices, keeping the first, lowest, highest and last entry of every pixel column. It returns indices of real entries, never synthetic points, so colours, circles, value labels, highlighting and the fill path keep referring to entries that exist. The line, scatter and candle renderers iterate that list instead of the raw `XBounds` range. Nothing is cached: the reduction is a flat scan costing about 0.5ms at 100k points, against the 157ms of drawing it removes.

**Tech Stack:** Kotlin 2.4.20, AGP 9.4, Gradle 9.7.1, JUnit 4 for the JVM suite in `MPChartLib/src/test`, AndroidX test runner for the instrumented suite in `MPChartBenchmark/src/androidTest`.

**Spec:** `docs/superpowers/specs/2026-09-18-decimation-design.md`

## Global Constraints

- Branch `feature/kotlin-port`. Never push to a remote.
- Commits carry only the user's authorship. Never add a Claude or Co-Authored-By trailer.
- Kotlin API conventions of this repo: properties instead of getter and setter pairs, booleans named `isSomethingEnabled`, KDoc on every public declaration, no `m` field prefix.
- Comments are minimal. Default to none, at most one short line where the reason is not obvious. Never mention a previous implementation or a fixed bug in a comment.
- Markdown files are never hard wrapped and contain no em dashes.
- Decimation activates above 1.0 points per pixel and drops to two points per column above 8.0 points per pixel. Both are internal constants, not public settings.
- Run the JVM suite with `./gradlew :MPChartLib:test` and the instrumented suite with `./gradlew :MPChartBenchmark:connectedDebugAndroidTest`. The emulator `Medium_Phone_API_36.1` must be running; `adb` lives at `~/Library/Android/sdk/platform-tools/adb`.

---

### Task 1: The reducer

**Files:**
- Create: `MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/PointReducer.kt`
- Test: `MPChartLib/src/test/kotlin/com/github/mikephil/charting/test/PointReducerTest.kt`

**Interfaces:**
- Consumes: nothing.
- Produces: `interface ColumnValues { fun xAt(index: Int): Float; fun lowAt(index: Int): Float; fun highAt(index: Int): Float }` and `class PointReducer` with `val indices: IntArray`, `val count: Int`, `fun reduce(values: ColumnValues, from: Int, to: Int, firstX: Float, valuesPerPixel: Float, pixelWidth: Float)` and `fun reduceNothing(from: Int, to: Int)`. Only `indices[0 until count]` is meaningful; the array is reused between calls and is usually longer than `count`.

- [ ] **Step 1: Write the failing tests**

Create `MPChartLib/src/test/kotlin/com/github/mikephil/charting/test/PointReducerTest.kt`:

```kotlin
package com.github.mikephil.charting.test

import com.github.mikephil.charting.renderer.ColumnValues
import com.github.mikephil.charting.renderer.PointReducer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sin

class PointReducerTest {

    private class Points(private val xs: FloatArray, private val ys: FloatArray) : ColumnValues {
        override fun xAt(index: Int) = xs[index]
        override fun lowAt(index: Int) = ys[index]
        override fun highAt(index: Int) = ys[index]
    }

    private fun kept(reducer: PointReducer) = reducer.indices.copyOf(reducer.count).toList()

    @Test
    fun keepsTheHighestAndLowestOfEveryColumn() {
        val xs = FloatArray(40) { it.toFloat() }
        val ys = FloatArray(40) { 10f }
        ys[21] = -5f
        ys[22] = 99f

        val reducer = PointReducer()
        reducer.reduce(Points(xs, ys), from = 0, to = 39, firstX = 0f, valuesPerPixel = 4f, pixelWidth = 10f)

        assertTrue("the spike survives", kept(reducer).contains(22))
        assertTrue("the dip survives", kept(reducer).contains(21))
    }

    @Test
    fun keepsEveryEntryBelowOnePointPerPixel() {
        val xs = FloatArray(10) { it.toFloat() }
        val ys = FloatArray(10) { it.toFloat() }

        val reducer = PointReducer()
        reducer.reduce(Points(xs, ys), from = 0, to = 9, firstX = 0f, valuesPerPixel = 0.1f, pixelWidth = 100f)

        assertEquals((0..9).toList(), kept(reducer))
    }

    @Test
    fun returnsAscendingIndices() {
        val xs = FloatArray(1000) { it.toFloat() }
        val ys = FloatArray(1000) { sin(it * 0.1f) * 100f }

        val reducer = PointReducer()
        reducer.reduce(Points(xs, ys), from = 0, to = 999, firstX = 0f, valuesPerPixel = 10f, pixelWidth = 100f)

        val indices = kept(reducer)
        assertTrue("reduced below the input size", indices.size < 1000)
        for (i in 1 until indices.size) {
            assertTrue("index $i is after index ${i - 1}", indices[i] > indices[i - 1])
        }
    }
}
```

- [ ] **Step 2: Run the tests and watch them fail**

Run: `./gradlew :MPChartLib:test --tests '*PointReducerTest*'`

Expected: compilation fails with `Unresolved reference 'PointReducer'` and `Unresolved reference 'ColumnValues'`. That is the correct failure; the class does not exist yet.

- [ ] **Step 3: Write the reducer**

Create `MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/PointReducer.kt`:

```kotlin
package com.github.mikephil.charting.renderer

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * The x position and the y range of the entries a [PointReducer] works on. Charts with one y value per entry
 * return that value from both [lowAt] and [highAt]; candle charts return the low and the high.
 */
interface ColumnValues {

    /** X value of the entry at [index], in value space. */
    fun xAt(index: Int): Float

    /** Bottom of the y range of the entry at [index], in value space. */
    fun lowAt(index: Int): Float

    /** Top of the y range of the entry at [index], in value space. */
    fun highAt(index: Int): Float
}

/**
 * Picks the entries worth drawing when several of them fall into the same pixel column.
 *
 * Per column it keeps the first entry, the lowest, the highest and the last, so the vertical extent survives
 * and the line enters and leaves the column where it really does. Above [COARSE_POINTS_PER_PIXEL] the column is
 * a solid smear and only the lowest and highest are kept. Below [ACTIVATION_POINTS_PER_PIXEL] nothing is
 * dropped. Every index returned is an index of a real entry, so anything that looks entries up still works.
 */
class PointReducer {

    /** Entry indices to draw, ascending. Only the first [count] are meaningful; the array is reused. */
    var indices = IntArray(0)
        private set

    /** How many entries of [indices] the last [reduce] filled. */
    var count = 0
        private set

    /**
     * Fills [indices] with the entries between [from] and [to] that are worth drawing.
     *
     * @param values the entries to reduce, indexed as the data set indexes them.
     * @param from index of the first entry to consider.
     * @param to index of the last entry to consider, inclusive.
     * @param firstX x value at the left edge of the content rectangle; entries left of it share its column.
     * @param valuesPerPixel width of one pixel in value space.
     * @param pixelWidth width of the content rectangle in pixels.
     */
    fun reduce(values: ColumnValues, from: Int, to: Int, firstX: Float, valuesPerPixel: Float, pixelWidth: Float) {
        count = 0
        if (to < from) return

        val entryCount = to - from + 1
        val pointsPerPixel = entryCount / max(pixelWidth, 1f)

        if (pointsPerPixel <= ACTIVATION_POINTS_PER_PIXEL || valuesPerPixel <= 0f) {
            ensureCapacity(entryCount)
            for (i in from..to) indices[count++] = i
            return
        }

        val keepEntryAndExit = pointsPerPixel <= COARSE_POINTS_PER_PIXEL
        var columnStart = from

        while (columnStart <= to) {
            val column = columnOf(values, columnStart, firstX, valuesPerPixel)
            var lowest = columnStart
            var highest = columnStart
            var index = columnStart

            while (index <= to && columnOf(values, index, firstX, valuesPerPixel) == column) {
                if (values.lowAt(index) < values.lowAt(lowest)) lowest = index
                if (values.highAt(index) > values.highAt(highest)) highest = index
                index++
            }

            val columnEnd = index - 1

            if (keepEntryAndExit) append(columnStart)
            append(min(lowest, highest))
            append(max(lowest, highest))
            if (keepEntryAndExit) append(columnEnd)

            columnStart = index
        }
    }

    private fun columnOf(values: ColumnValues, index: Int, firstX: Float, valuesPerPixel: Float): Int =
        floor((values.xAt(index) - firstX) / valuesPerPixel).toInt()

    /** Appends [index] unless it would repeat or precede the last one, which keeps the result ascending. */
    private fun append(index: Int) {
        if (count > 0 && indices[count - 1] >= index) return
        ensureCapacity(count + 1)
        indices[count++] = index
    }

    /** Fills [indices] with every index from [from] to [to], as if no column held more than one entry. */
    fun reduceNothing(from: Int, to: Int) {
        count = 0
        if (to < from) return
        ensureCapacity(to - from + 1)
        for (i in from..to) indices[count++] = i
    }

    private fun ensureCapacity(needed: Int) {
        if (indices.size < needed) indices = indices.copyOf(max(needed, indices.size * 2))
    }

    companion object {

        /** Points per pixel below which every entry is kept. */
        const val ACTIVATION_POINTS_PER_PIXEL = 1f

        /** Points per pixel above which only the lowest and highest of a column are kept. */
        const val COARSE_POINTS_PER_PIXEL = 8f
    }
}
```

- [ ] **Step 4: Run the tests and watch them pass**

Run: `./gradlew :MPChartLib:test --tests '*PointReducerTest*'`

Expected: 3 tests pass. Then run the whole suite to make sure nothing else moved: `./gradlew :MPChartLib:test`. Expected: 41 tests, 0 failures.

- [ ] **Step 5: Commit**

```bash
git add MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/PointReducer.kt MPChartLib/src/test/kotlin/com/github/mikephil/charting/test/PointReducerTest.kt
git commit -m "Add a reducer that keeps one column worth of points"
```

---

### Task 2: The chart setting and the line renderer

**Files:**
- Modify: `MPChartLib/src/main/kotlin/com/github/mikephil/charting/interfaces/dataprovider/BarLineScatterCandleBubbleDataProvider.kt`
- Modify: `MPChartLib/src/main/kotlin/com/github/mikephil/charting/charts/BarLineChartBase.kt`
- Modify: `MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/LineChartRenderer.kt`
- Test: `MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/DecimationTest.kt`

**Interfaces:**
- Consumes: `PointReducer`, `ColumnValues` from Task 1.
- Produces: `BarLineScatterCandleBubbleDataProvider.isDecimationEnabled: Boolean` with a default getter returning true, overridden by `BarLineChartBase.isDecimationEnabled: Boolean` as a settable `var`.

- [ ] **Step 1: Write the failing tests**

Create `MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/DecimationTest.kt`:

```kotlin
package com.github.mikephil.charting.benchmark

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

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
}
```

Then add the counter to `MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/RecordingCanvas.kt`, inside the class body:

```kotlin
    /** How many floats the renderers passed to `drawLines`, which is four per segment. */
    var lineFloatsDrawn = 0
        private set
```

and add `lineFloatsDrawn += count` as the first line of the existing `drawLines` override, before `lineColors.add(paint.color)`.

- [ ] **Step 2: Run the tests and watch them fail**

Run: `./gradlew :MPChartBenchmark:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.github.mikephil.charting.benchmark.DecimationTest`

Expected: compilation fails with `Unresolved reference 'isDecimationEnabled'`. That is the correct failure; the property does not exist yet.

- [ ] **Step 3: Add the setting**

In `BarLineScatterCandleBubbleDataProvider.kt`, add inside the interface body:

```kotlin
    /**
     * True while the renderers may drop entries that share a pixel column with another entry. The entries they
     * keep are the first, the lowest, the highest and the last of each column, so peaks survive.
     */
    val isDecimationEnabled: Boolean get() = true
```

In `BarLineChartBase.kt`, next to the existing `override var maxVisibleCount = 100`, add:

```kotlin
    /**
     * Whether entries that share a pixel column may be dropped while drawing. On by default. Turning it off
     * draws every visible entry, which costs time in proportion to how many are on screen.
     */
    override var isDecimationEnabled = true
```

- [ ] **Step 4: Wire the reducer into the line renderer**

In `LineChartRenderer.kt`, add two fields next to the existing `segmentBuffer`:

```kotlin
    private val reducer = PointReducer()

    private val lineValues = object : ColumnValues {
        var dataSet: ILineDataSet<*>? = null
        override fun xAt(index: Int) = dataSet!!.getEntryForIndex(index).x
        override fun lowAt(index: Int) = dataSet!!.getEntryForIndex(index).y
        override fun highAt(index: Int) = dataSet!!.getEntryForIndex(index).y
    }
```

Add a private helper that both branches of `drawLinear` and `drawLinearFill` call:

```kotlin
    /** Fills [reducer] with the entries of [dataSet] worth drawing in the current viewport. */
    private fun reduceVisible(dataSet: ILineDataSet<*>) {
        val from = xBounds.min
        val to = xBounds.min + xBounds.range
        lineValues.dataSet = dataSet

        if (!chart.isDecimationEnabled) {
            reducer.reduceNothing(from, to)
            return
        }

        val pixelWidth = viewPortHandler.contentWidth
        val valuesPerPixel = (chart.highestVisibleX - chart.lowestVisibleX) / max(pixelWidth, 1f)
        reducer.reduce(lineValues, from, to, chart.lowestVisibleX, valuesPerPixel, pixelWidth)
    }
```

`reduceNothing` comes from Task 1 and makes the disabled path share one walk with the enabled one.

Then change the three loops that walk the visible range so they walk `reducer.indices` instead. Call `reduceVisible(dataSet)` once in `drawLinear` right after `xBounds.set(chart, dataSet)`, and once in `drawLinearFill` before the chunk loop.

- In the single colour branch, replace `for (x in xBounds.min..xBounds.range + xBounds.min)` with `for (k in 0 until reducer.count)`, and read `val e2 = dataSet.getEntryForIndex(reducer.indices[k])` and `val e1 = dataSet.getEntryForIndex(reducer.indices[max(k - 1, 0)])`.
- In the multi colour branch, replace `segmentCount = xBounds.range` with `segmentCount = max(reducer.count - 1, 0)`, take the segment start from `reducer.indices[j]` and the end from `reducer.indices[j + 1]`, and take the colour from `dataSet.getColor(reducer.indices[j])`.
- In `drawLinearFill`, chunk over `0 until reducer.count` instead of `bounds.min .. bounds.range + bounds.min`, and change `generateFilledPath` to take the reduced positions so the fill follows the same outline as the stroke. Its `startIndex` and `endIndex` parameters become positions in `reducer.indices`, and every `dataSet.getEntryForIndex(i)` inside it becomes `dataSet.getEntryForIndex(reducer.indices[i])`.
- In `drawCubicBezier` (line 218) and `drawHorizontalBezier` (line 167), call `reduceVisible(dataSet)` right after their own `xBounds.set(chart, dataSet)`, then walk positions in `reducer.indices` instead of entry indices. In `drawCubicBezier` the guard `if (xBounds.range >= 1)` becomes `if (reducer.count >= 2)`, `firstIndex` becomes position `1`, the loop `for (j in xBounds.min + 1..xBounds.range + xBounds.min)` becomes `for (j in 1 until reducer.count)`, and every `dataSet.getEntryForIndex(i)` becomes `dataSet.getEntryForIndex(reducer.indices[i])` with `i` clamped into `0 until reducer.count` where the old code clamped with `max(firstIndex - 2, 0)`. The `nextIndex == j` lookahead compares positions, not entry indices, so it needs no other change. Apply the same substitution to `drawHorizontalBezier`.

The spline is built through the reduced points on purpose. Because a column holding one entry yields that entry from all four candidates, the reduced set equals the original set at the activation threshold and the curve does not jump as the user zooms.

- [ ] **Step 5: Run the tests and watch them pass**

Run: `./gradlew :MPChartBenchmark:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.github.mikephil.charting.benchmark.DecimationTest`

Expected: both tests pass. Then run the existing renderer tests, which must be untouched by this: `./gradlew :MPChartBenchmark:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.github.mikephil.charting.benchmark.LineRendererTest`. Expected: 4 tests pass. `multiColorLineSpansTheFullContentWidth` passing is the check that the fill and the stroke still agree on where the line ends.

- [ ] **Step 6: Commit**

```bash
git add MPChartLib/src/main/kotlin/com/github/mikephil/charting/interfaces/dataprovider/BarLineScatterCandleBubbleDataProvider.kt MPChartLib/src/main/kotlin/com/github/mikephil/charting/charts/BarLineChartBase.kt MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/LineChartRenderer.kt MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/PointReducer.kt MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/DecimationTest.kt MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/RecordingCanvas.kt
git commit -m "Draw one column worth of points on line charts"
```

---

### Task 3: Scatter charts clip to the visible range

This is a defect fix that stands on its own and must land before scatter decimation, because the decimated range is derived from `XBounds`.

**Files:**
- Modify: `MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/ScatterChartRenderer.kt`
- Test: `MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/ScatterBoundsTest.kt`

**Interfaces:**
- Consumes: the `XBounds` inner class already available from `BarLineScatterCandleBubbleRenderer`.
- Produces: nothing new.

`ScatterChartRenderer.drawDataSet` currently loops `for (i in 0 until max)` where `max` is the whole entry count, transforms every entry, and only `break`s once past the right edge. Entries left of the viewport are transformed and then skipped with `continue`, so panning to the right hand end of a 100k point scatter transforms 100k points every frame.

- [ ] **Step 1: Write the failing test**

Create `MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/ScatterBoundsTest.kt`:

```kotlin
package com.github.mikephil.charting.benchmark

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.ScatterData
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScatterBoundsTest {

    @Test
    fun costsTheSameWhereverTheViewportSits() {
        val total = 100_000
        val chart = Fixtures.lay { ScatterChart(Fixtures.context()) }
        chart.data = ScatterData(Fixtures.scatterSet(Fixtures.lineEntries(total)))
        val canvas = Fixtures.canvas()

        fun drawAt(panPixels: Float): Double {
            Fixtures.resetViewport(chart)
            Fixtures.zoomToVisiblePoints(chart, total, 300)
            Fixtures.panBy(chart, panPixels)
            repeat(5) { chart.draw(canvas) }
            val start = System.nanoTime()
            repeat(10) { chart.draw(canvas) }
            return (System.nanoTime() - start) / 1e6 / 10
        }

        val atStart = drawAt(0f)
        val nearEnd = drawAt(-300_000f)

        assertTrue("start ${atStart}ms vs near end ${nearEnd}ms", nearEnd < atStart * 3)
    }
}
```

- [ ] **Step 2: Run the test and watch it fail**

Run: `./gradlew :MPChartBenchmark:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.github.mikephil.charting.benchmark.ScatterBoundsTest`

Expected: FAIL, with the near-end draw far slower than the draw at the start, because every frame scans from index 0.

- [ ] **Step 3: Clip to the visible range**

In `ScatterChartRenderer.drawDataSet`, add `xBounds.set(chart, dataSet)` after the `trans` line, and replace

```kotlin
        val max = min(ceil(dataSet.entryCount.toFloat() * animator.phaseX), dataSet.entryCount.toFloat()).toInt()

        for (i in 0 until max) {
```

with

```kotlin
        for (i in xBounds.min..xBounds.min + xBounds.range) {
```

Leave the two bounds checks inside the loop as they are; they still trim the entries just outside the edges. Remove the now unused `ceil` import if nothing else in the file uses it.

- [ ] **Step 4: Run the test and watch it pass**

Run: `./gradlew :MPChartBenchmark:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.github.mikephil.charting.benchmark.ScatterBoundsTest`

Expected: PASS. Then `./gradlew :MPChartBenchmark:connectedDebugAndroidTest` for the whole instrumented suite. Expected: 0 failures.

- [ ] **Step 5: Commit**

```bash
git add MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/ScatterChartRenderer.kt MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/ScatterBoundsTest.kt
git commit -m "Draw only the scatter points that are on screen"
```

---

### Task 4: Scatter and candle decimation

**Files:**
- Modify: `MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/ScatterChartRenderer.kt`
- Modify: `MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/CandleStickChartRenderer.kt`
- Test: `MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/DecimationTest.kt`

**Interfaces:**
- Consumes: `PointReducer`, `ColumnValues`, `chart.isDecimationEnabled`, all from Tasks 1 and 2.
- Produces: nothing new.

- [ ] **Step 1: Write the failing test**

Add to `DecimationTest.kt`:

```kotlin
    @Test
    fun candlesAreReducedToTheirColumnExtremes() {
        val total = 50_000
        val chart = Fixtures.lay { com.github.mikephil.charting.charts.CandleStickChart(Fixtures.context()) }
        chart.data = com.github.mikephil.charting.data.CandleData(Fixtures.candleSet(Fixtures.candleEntries(total)))
        Fixtures.resetViewport(chart)
        val canvas = Fixtures.canvas()

        repeat(3) { chart.draw(canvas) }
        val start = System.nanoTime()
        repeat(5) { chart.draw(canvas) }
        val decimated = (System.nanoTime() - start) / 1e6 / 5

        chart.isDecimationEnabled = false
        repeat(3) { chart.draw(canvas) }
        val plainStart = System.nanoTime()
        repeat(5) { chart.draw(canvas) }
        val plain = (System.nanoTime() - plainStart) / 1e6 / 5

        assertTrue("decimated ${decimated}ms vs ${plain}ms", decimated < plain / 3)
    }
```

- [ ] **Step 2: Run the test and watch it fail**

Run: `./gradlew :MPChartBenchmark:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.github.mikephil.charting.benchmark.DecimationTest#candlesAreReducedToTheirColumnExtremes`

Expected: FAIL, because the candle renderer draws every visible entry whatever the flag says, so the two timings match.

- [ ] **Step 3: Wire the reducer into both renderers**

In `ScatterChartRenderer`, add the same three members the line renderer got, with the scatter data set type:

```kotlin
    private val reducer = PointReducer()

    private val scatterValues = object : ColumnValues {
        var dataSet: IScatterDataSet<*>? = null
        override fun xAt(index: Int) = dataSet!!.getEntryForIndex(index).x
        override fun lowAt(index: Int) = dataSet!!.getEntryForIndex(index).y
        override fun highAt(index: Int) = dataSet!!.getEntryForIndex(index).y
    }
```

and replace the loop from Task 3 with a walk over `reducer.indices`, using `reducer.indices[k]` wherever the old loop used `i`, including in `dataSet.getColor(i)`.

In `CandleStickChartRenderer`, add the same, using the candle range:

```kotlin
    private val candleValues = object : ColumnValues {
        var dataSet: ICandleDataSet<*>? = null
        override fun xAt(index: Int) = dataSet!!.getEntryForIndex(index).x
        override fun lowAt(index: Int) = dataSet!!.getEntryForIndex(index).low
        override fun highAt(index: Int) = dataSet!!.getEntryForIndex(index).high
    }
```

and replace `for (j in xBounds.min..xBounds.range + xBounds.min)` in `drawDataSet` with a walk over `reducer.indices`, using the reduced index wherever the old loop used `j`, including in `dataSet.getColor(j)` and in the highlight colour lookups.

Both renderers get their own copy of the helper, with their own data set type substituted for `ILineDataSet` and their own `ColumnValues` field:

```kotlin
    private fun reduceVisible(dataSet: IScatterDataSet<*>) {
        val from = xBounds.min
        val to = xBounds.min + xBounds.range
        scatterValues.dataSet = dataSet

        if (!chart.isDecimationEnabled) {
            reducer.reduceNothing(from, to)
            return
        }

        val pixelWidth = viewPortHandler.contentWidth
        val valuesPerPixel = (chart.highestVisibleX - chart.lowestVisibleX) / max(pixelWidth, 1f)
        reducer.reduce(scatterValues, from, to, chart.lowestVisibleX, valuesPerPixel, pixelWidth)
    }
```

Each renderer calls it once per data set, right after its own `xBounds.set(chart, dataSet)`.

- [ ] **Step 4: Run the tests and watch them pass**

Run: `./gradlew :MPChartBenchmark:connectedDebugAndroidTest`

Expected: the whole instrumented suite passes, 0 failures. Then `./gradlew :MPChartLib:test`. Expected: 41 tests, 0 failures.

- [ ] **Step 5: Commit**

```bash
git add MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/ScatterChartRenderer.kt MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/CandleStickChartRenderer.kt MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/DecimationTest.kt
git commit -m "Draw one column worth of points on scatter and candle charts"
```

---

### Task 5: Measure and tune the coarse threshold

**Files:**
- Modify: `MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/FullDrawTest.kt`
- Create: `MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/DecimationImageTest.kt`
- Modify: `MPChartLib/src/main/kotlin/com/github/mikephil/charting/renderer/PointReducer.kt` only if the images say the threshold is wrong

**Interfaces:**
- Consumes: everything from Tasks 1 to 4.
- Produces: nothing new.

- [ ] **Step 1: Add a decimation dimension to the draw benchmark**

In `FullDrawTest.kt`, give `measure` a `decimation: Boolean = true` parameter, set `chart.isDecimationEnabled = decimation` right after the chart is laid out, add `decimation=$decimation` to the log line, and add these tests:

```kotlin
    @Test fun draw100kNoDecimation() = measure(100_000, decimation = false)

    @Test fun draw50kMultiColorNoDecimation() = measure(50_000, colors = 8, decimation = false)
```

- [ ] **Step 2: Run the A and B measurement**

Run each in its own process so a warming JIT and a growing heap cannot hide the trend:

```bash
export PATH=$PATH:~/Library/Android/sdk/platform-tools
adb logcat -c
for m in draw10k draw50k draw100k draw100kNoDecimation draw50kCircles draw50kMultiColor draw50kMultiColorNoDecimation; do
  ./gradlew :MPChartBenchmark:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.github.mikephil.charting.benchmark.FullDrawTest#$m -q > /dev/null 2>&1
done
adb logcat -d -s MPBENCH
```

Expected: the decimated 100k draw lands near 5 to 9ms against about 157ms without, and the decimated 50k multi colour draw lands in the same range against about 152ms without.

- [ ] **Step 3: Render comparison images**

Create `DecimationImageTest.kt` that draws the same 100k line at four zoom levels, at 2, 5, 10 and 50 points per pixel, once with decimation on and once off, and writes each bitmap as a PNG to the app's external files directory:

```kotlin
package com.github.mikephil.charting.benchmark

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class DecimationImageTest {

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
                Log.i(Bench.TAG, "IMAGE ${file.absolutePath}")
            }
        }
    }
}
```

Run it, then pull the images:

```bash
./gradlew :MPChartBenchmark:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.github.mikephil.charting.benchmark.DecimationImageTest -q
adb pull /sdcard/Android/data/com.github.mikephil.charting.benchmark.test/files/decimation ./decimation-images
```

- [ ] **Step 4: Look at the images and decide**

Compare each pair. The on and off images should be hard to tell apart at 10 and 50 points per pixel. If the 5 and 10 point pairs differ visibly, raise `COARSE_POINTS_PER_PIXEL` above 8 so more columns keep their entry and exit; if even the 5 point pair is indistinguishable, lower it. Record the chosen value and why in the spec under a new short "Tuning" section. Do not delete the images before the user has seen them.

- [ ] **Step 5: Commit**

```bash
git add MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/FullDrawTest.kt MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark/DecimationImageTest.kt docs/superpowers/specs/2026-09-18-decimation-design.md
git commit -m "Measure decimation against the plain draw and pin the threshold"
```

---

## Notes for the executor

- `Fixtures`, `Bench`, `RecordingCanvas` and `zoomToVisiblePoints` already exist in `MPChartBenchmark/src/androidTest/kotlin/com/github/mikephil/charting/benchmark`. Read them before writing a new test; they cover chart layout on the main thread, deterministic data and viewport control.
- A chart has to be built on the main thread because it sets up a `GestureDetector`. `Fixtures.lay { ... }` does that for you.
- `MPChartExample:testDebugUnitTest` fails before this work starts, because `ReadmeSamplesCompile.kt` holds no test methods and Gradle 9 rejects a test source set that discovers nothing. Leave it alone and do not let it block a commit.
