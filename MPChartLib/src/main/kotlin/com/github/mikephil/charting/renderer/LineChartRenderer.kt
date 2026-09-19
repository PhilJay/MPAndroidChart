package com.github.mikephil.charting.renderer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

/**
 * Draws the lines of a [LineDataProvider]. Supports the linear, stepped, cubic bezier and horizontal bezier
 * modes, filled areas, dashed lines and circles at the entries. Bezier and dashed lines are drawn onto an
 * offscreen bitmap the size of the chart, which [drawData] composes onto the canvas; plain linear lines are
 * drawn directly. Circles are drawn in [drawExtras] from cached bitmaps.
 *
 * @property chart The chart that supplies the line data and the transformers.
 */
open class LineChartRenderer(
    protected val chart: LineDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : LineRadarRenderer(animator, viewPortHandler) {

    /**
     * Paint for the hole inside each circle; its color is the circle hole color of the data set.
     */
    protected val circlePaintInner = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    /**
     * Weak reference to the offscreen bitmap that lines are drawn onto; recreated when the chart size changes.
     */
    protected var drawBitmap: WeakReference<Bitmap>? = null

    /**
     * Canvas onto [drawBitmap]; null before the first [drawData] and after [releaseBitmap].
     */
    protected var bitmapCanvas: Canvas? = null

    /**
     * Pixel format of the offscreen bitmap, ARGB_8888 by default. Setting it releases the current bitmap so the
     * next draw creates a new one.
     */
    var bitmapConfig: Bitmap.Config = Bitmap.Config.ARGB_8888
        set(value) {
            field = value
            releaseBitmap()
        }

    /**
     * Path of the bezier line, built in value space and converted to pixels before drawing.
     */
    protected val cubicPath = Path()
    /**
     * Copy of [cubicPath] closed down to the fill line, used for the filled area under a bezier line.
     */
    protected val cubicFillPath = Path()

    private var lineBuffer = FloatArray(4)

    /** Holds the visible segments of a data set with several colors, in value space and then in pixels. */
    private var segmentBuffer = FloatArray(4)

    /** Holds the visible points of a data set that draws circles, in value space and then in pixels. */
    private var circlePointBuffer = FloatArray(4)

    /**
     * Path reused for the filled area under a linear or stepped line.
     */
    protected val generateFilledPathBuffer = Path()

    private val imageCaches = HashMap<IDataSet<*>, DataSetImageCache>()

    /**
     * Lines are drawn without a buffer, so this only drops the cached circle bitmaps of data sets the chart no
     * longer holds. Without that the cache would keep every data set a realtime chart ever replaced.
     */
    override fun initBuffers() {
        val dataSets = chart.lineData?.dataSets ?: emptyList()
        imageCaches.keys.retainAll(dataSets.toSet())
    }

    /**
     * Draws every visible data set. Cubic, horizontal bezier and dashed lines need an offscreen bitmap the size of
     * the chart, which is created, cleared and composed onto [c] only while one of them is on screen. Does nothing
     * while the chart has no size.
     */
    override fun drawData(c: Canvas) {
        val lineData = chart.lineData ?: return

        if (lineData.dataSets.none { it.isVisible && needsOffscreenBitmap(it) }) {
            for (set in lineData.dataSets) {
                if (set.isVisible) drawDataSet(c, set)
            }
            return
        }

        val width = viewPortHandler.chartWidth.toInt()
        val height = viewPortHandler.chartHeight.toInt()

        var bitmap = drawBitmap?.get()

        if (bitmap == null || bitmap.width != width || bitmap.height != height) {
            if (width > 0 && height > 0) {
                bitmap = Bitmap.createBitmap(width, height, bitmapConfig)
                drawBitmap = WeakReference(bitmap)
                bitmapCanvas = Canvas(bitmap)
            } else {
                return
            }
        }

        bitmap.eraseColor(Color.TRANSPARENT)

        for (set in lineData.dataSets) {
            if (set.isVisible) drawDataSet(c, set)
        }

        // No paint: renderPaint still holds the last data set's color, and its alpha would fade the bitmap.
        c.drawBitmap(bitmap, 0f, 0f, null)
    }

    /** True for the line modes that are drawn through the offscreen bitmap instead of onto the chart canvas. */
    private fun needsOffscreenBitmap(dataSet: ILineDataSet<*>): Boolean =
        dataSet.isDashedLineEnabled ||
            dataSet.mode == LineDataSet.Mode.CUBIC_BEZIER ||
            dataSet.mode == LineDataSet.Mode.HORIZONTAL_BEZIER

    /**
     * Entry at [position] of the reduced list, which must not be empty. A position outside the list steps that
     * many entries on from the first or the last reduced entry, and stops at the first or the last entry of
     * [dataSet], so the curve reaches past the visible range for its control points as it did before any
     * reduction.
     */
    protected fun reducedEntry(dataSet: ILineDataSet<*>, position: Int): Entry<*> {
        val last = reducer.count - 1
        val index = when {
            position < 0 -> max(reducer.indices[0] + position, 0)
            position > last -> min(reducer.indices[last] + position - last, dataSet.entryCount - 1)
            else -> reducer.indices[position]
        }
        return dataSet.getEntryForIndex(index)
    }

    /**
     * Draws the line of [dataSet] in its mode, with the line width (dp) and dash effect of the data set.
     */
    protected open fun drawDataSet(c: Canvas, dataSet: ILineDataSet<*>) {
        if (dataSet.entryCount < 1) return

        renderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.lineWidth)
        renderPaint.pathEffect = dataSet.dashPathEffect

        when (dataSet.mode) {
            LineDataSet.Mode.LINEAR, LineDataSet.Mode.STEPPED -> drawLinear(c, dataSet)
            LineDataSet.Mode.CUBIC_BEZIER -> drawCubicBezier(dataSet)
            LineDataSet.Mode.HORIZONTAL_BEZIER -> drawHorizontalBezier(dataSet)
        }

        renderPaint.pathEffect = null
    }

    /**
     * Draws [dataSet] as a curve whose control points are horizontal, so the line never overshoots vertically
     * between two entries. Draws onto the bitmap canvas, so it needs a preceding [drawData].
     */
    protected open fun drawHorizontalBezier(dataSet: ILineDataSet<*>) {
        val phaseY = animator.phaseY

        val trans = chart.getTransformer(dataSet.axisDependency)

        xBounds.set(chart, dataSet)
        reduceVisible(chart, dataSet)

        cubicPath.reset()

        if (reducer.count >= 2) {
            var prev = reducedEntry(dataSet, 0)
            var cur = prev

            cubicPath.moveTo(cur.x, cur.y * phaseY)

            for (j in 1 until reducer.count) {
                prev = cur
                cur = reducedEntry(dataSet, j)

                val cpx = prev.x + (cur.x - prev.x) / 2.0f

                cubicPath.cubicTo(
                    cpx, prev.y * phaseY,
                    cpx, cur.y * phaseY,
                    cur.x, cur.y * phaseY
                )
            }
        }

        val canvas = bitmapCanvas ?: return

        if (dataSet.isDrawFilledEnabled) {
            cubicFillPath.reset()
            cubicFillPath.addPath(cubicPath)
            drawCubicFill(canvas, dataSet, cubicFillPath, trans)
        }

        renderPaint.color = dataSet.color
        renderPaint.style = Paint.Style.STROKE

        trans.pathValueToPixel(cubicPath)

        canvas.drawPath(cubicPath, renderPaint)

        renderPaint.pathEffect = null
    }

    /**
     * Draws [dataSet] as a smooth cubic curve; the cubic intensity of the data set controls how far the control
     * points reach. Draws onto the bitmap canvas, so it needs a preceding [drawData].
     */
    protected open fun drawCubicBezier(dataSet: ILineDataSet<*>) {
        val phaseY = animator.phaseY

        val trans = chart.getTransformer(dataSet.axisDependency)

        xBounds.set(chart, dataSet)
        reduceVisible(chart, dataSet)

        val intensity = dataSet.cubicIntensity

        cubicPath.reset()

        if (reducer.count >= 2) {
            var prevDx: Float
            var prevDy: Float
            var curDx: Float
            var curDy: Float

            // A cubic bezier needs one extra point on each side, otherwise the curve misbehaves at the chart edges.
            val firstPosition = 1

            var prevPrev: Entry<*>
            var prev = reducedEntry(dataSet, firstPosition - 2)
            var cur = reducedEntry(dataSet, firstPosition - 1)
            var next = cur
            var nextPosition = -1

            cubicPath.moveTo(cur.x, cur.y * phaseY)

            for (j in 1 until reducer.count) {
                prevPrev = prev
                prev = cur
                cur = if (nextPosition == j) next else reducedEntry(dataSet, j)

                nextPosition = j + 1
                next = reducedEntry(dataSet, nextPosition)

                prevDx = (cur.x - prevPrev.x) * intensity
                prevDy = (cur.y - prevPrev.y) * intensity
                curDx = (next.x - prev.x) * intensity
                curDy = (next.y - prev.y) * intensity

                cubicPath.cubicTo(
                    prev.x + prevDx, (prev.y + prevDy) * phaseY,
                    cur.x - curDx, (cur.y - curDy) * phaseY,
                    cur.x, cur.y * phaseY
                )
            }
        }

        val canvas = bitmapCanvas ?: return

        if (dataSet.isDrawFilledEnabled) {
            cubicFillPath.reset()
            cubicFillPath.addPath(cubicPath)
            drawCubicFill(canvas, dataSet, cubicFillPath, trans)
        }

        renderPaint.color = dataSet.color
        renderPaint.style = Paint.Style.STROKE

        trans.pathValueToPixel(cubicPath)

        canvas.drawPath(cubicPath, renderPaint)

        renderPaint.pathEffect = null
    }

    /**
     * Closes [spline] (value space) down to the fill line given by the fill formatter of [dataSet], converts it to
     * pixels with [trans] and fills it with the fill drawable, or the fill color and alpha, of the data set.
     * Reads the reduction its caller performed.
     */
    protected open fun drawCubicFill(c: Canvas, dataSet: ILineDataSet<*>, spline: Path, trans: Transformer) {
        if (reducer.count == 0) return

        val fillMin = dataSet.fillFormatter.getFillLinePosition(dataSet, chart)

        // The same ends the spline has, so the bottom edge of the fill cannot reach past the curve above it.
        spline.lineTo(reducedEntry(dataSet, reducer.count - 1).x, fillMin)
        spline.lineTo(reducedEntry(dataSet, 0).x, fillMin)
        spline.close()

        trans.pathValueToPixel(spline)

        val drawable = dataSet.fillDrawable
        if (drawable != null) {
            drawFilledPath(c, spline, drawable)
        } else {
            drawFilledPath(c, spline, dataSet.fillColor, dataSet.fillAlpha)
        }
    }

    /**
     * Draws [dataSet] as straight or stepped segments between consecutive entries in the visible range, after the
     * filled area when the data set enables it. Uses one draw call per segment when the data set has several
     * colors, otherwise one call per run of drawable segments. An entry whose y value is not finite leaves a gap:
     * the segments on both sides of it are skipped and the line starts again after it. Dashed lines go onto the
     * bitmap canvas, others directly onto [c].
     */
    protected open fun drawLinear(c: Canvas, dataSet: ILineDataSet<*>) {
        val entryCount = dataSet.entryCount

        val isDrawSteppedEnabled = dataSet.mode == LineDataSet.Mode.STEPPED
        val pointsPerEntryPair = if (isDrawSteppedEnabled) 4 else 2

        val trans = chart.getTransformer(dataSet.axisDependency)

        val phaseY = animator.phaseY

        renderPaint.style = Paint.Style.STROKE

        // dashed lines are drawn on the bitmap canvas
        val canvas = if (dataSet.isDashedLineEnabled) bitmapCanvas ?: return else c

        xBounds.set(chart, dataSet)
        reduceVisible(chart, dataSet)

        if (dataSet.isDrawFilledEnabled && entryCount > 0) {
            drawLinearFill(c, dataSet, trans)
        }

        if (dataSet.colors.size > 1) {
            val floatsPerSegment = pointsPerEntryPair * 2
            val segmentCount = max(reducer.count - 1, 0)

            if (segmentCount > 0) {
                val floatsNeeded = segmentCount * floatsPerSegment
                if (segmentBuffer.size < floatsNeeded) segmentBuffer = FloatArray(floatsNeeded * 2)

                var w = 0
                for (j in 0 until segmentCount) {
                    val start = dataSet.getEntryForIndex(reducer.indices[j])
                    val startX = start.x
                    val startY = start.y * phaseY

                    segmentBuffer[w++] = startX
                    segmentBuffer[w++] = startY

                    val end = dataSet.getEntryForIndex(reducer.indices[j + 1])

                    if (isDrawSteppedEnabled) {
                        segmentBuffer[w++] = end.x
                        segmentBuffer[w++] = startY
                        segmentBuffer[w++] = end.x
                        segmentBuffer[w++] = startY
                    }

                    segmentBuffer[w++] = end.x
                    segmentBuffer[w++] = end.y * phaseY
                }

                trans.pointValuesToPixel(segmentBuffer, floatsNeeded)

                for (segment in 0 until segmentCount) {
                    val base = segment * floatsPerSegment

                    val firstCoordinateX = segmentBuffer[base]
                    val firstCoordinateY = segmentBuffer[base + 1]
                    val lastCoordinateX = segmentBuffer[base + floatsPerSegment - 2]
                    val lastCoordinateY = segmentBuffer[base + floatsPerSegment - 1]

                    if (firstCoordinateX == lastCoordinateX && firstCoordinateY == lastCoordinateY) continue

                    if (!viewPortHandler.isInBoundsRight(firstCoordinateX)) break

                    if (!viewPortHandler.isInBoundsLeft(lastCoordinateX) ||
                        !viewPortHandler.isInBoundsTop(max(firstCoordinateY, lastCoordinateY)) ||
                        !viewPortHandler.isInBoundsBottom(min(firstCoordinateY, lastCoordinateY))
                    ) continue

                    renderPaint.color = dataSet.getColor(reducer.indices[segment])

                    canvas.drawLines(segmentBuffer, base, floatsPerSegment, renderPaint)
                }
            }
        } else {
            if (lineBuffer.size < max(entryCount * pointsPerEntryPair, pointsPerEntryPair) * 2) {
                lineBuffer = FloatArray(max(entryCount * pointsPerEntryPair, pointsPerEntryPair) * 4)
            }

            var j = 0
            var previous: Entry<*>? = null

            for (k in 0 until reducer.count) {
                val current = dataSet.getEntryForIndex(reducer.indices[k])
                val start = previous ?: current

                lineBuffer[j++] = start.x
                lineBuffer[j++] = start.y * phaseY

                if (isDrawSteppedEnabled) {
                    lineBuffer[j++] = current.x
                    lineBuffer[j++] = start.y * phaseY
                    lineBuffer[j++] = current.x
                    lineBuffer[j++] = start.y * phaseY
                }

                lineBuffer[j++] = current.x
                lineBuffer[j++] = current.y * phaseY

                previous = current
            }

            if (j > 0) {
                trans.pointValuesToPixel(lineBuffer)

                renderPaint.color = dataSet.color

                // One draw call per run of drawable segments. A single entry without a value would otherwise
                // invalidate the bounds of the whole array and the canvas would drop every segment with it.
                val floatsPerSegment = pointsPerEntryPair * 2
                var runStart = 0
                var segment = 0

                while (segment < j) {
                    var drawable = true
                    for (i in segment until segment + floatsPerSegment) {
                        if (!lineBuffer[i].isFinite()) {
                            drawable = false
                            break
                        }
                    }

                    if (!drawable) {
                        drawLineRun(canvas, runStart, segment - runStart, floatsPerSegment)
                        runStart = segment + floatsPerSegment
                    }

                    segment += floatsPerSegment
                }

                drawLineRun(canvas, runStart, j - runStart, floatsPerSegment)
            }
        }

        renderPaint.pathEffect = null
    }

    private companion object {
        /** Floats one drawLines call may hold. Android's own display list splits its line draws around this size. */
        const val MAX_LINE_FLOATS = 16384
    }

    /**
     * Draws [count] floats of the line buffer from [offset] as lines, in chunks of at most [MAX_LINE_FLOATS] floats.
     * A canvas drops a draw call that holds too many lines at once, which a data set of tens of thousands of
     * entries reaches easily.
     *
     * @param floatsPerSegment floats one segment of the line occupies, so that no chunk cuts a segment in half
     */
    private fun drawLineRun(canvas: Canvas, offset: Int, count: Int, floatsPerSegment: Int) {
        if (count <= 0) return

        val chunk = max(MAX_LINE_FLOATS / floatsPerSegment, 1) * floatsPerSegment
        var drawn = 0

        while (drawn < count) {
            canvas.drawLines(lineBuffer, offset + drawn, min(chunk, count - drawn), renderPaint)
            drawn += chunk
        }
    }

    /**
     * Fills the area between the line of [dataSet] and its fill line onto [c], in chunks of 128 entries to keep
     * the path small. Reads the reduction its caller performed.
     */
    protected open fun drawLinearFill(c: Canvas, dataSet: ILineDataSet<*>, trans: Transformer) {
        val filled = generateFilledPathBuffer

        val firstPosition = 0
        val lastPosition = reducer.count - 1
        val positionInterval = 128

        var chunkStart: Int
        var chunkEnd: Int
        var iterations = 0

        val drawable = dataSet.fillDrawable
        // A drawable is not painted quite to the edge of its chunk, so the chunks overlap by one position.
        val overlap = if (drawable != null) 1 else 0

        // Filled in chunks to avoid OutOfMemory errors on large bounds sets.
        do {
            chunkStart = firstPosition + iterations * positionInterval
            chunkEnd = chunkStart + positionInterval
            chunkEnd = if (chunkEnd > lastPosition) lastPosition else chunkEnd

            if (chunkStart <= chunkEnd) {
                generateFilledPath(
                    dataSet,
                    max(firstPosition, chunkStart - overlap),
                    min(lastPosition, chunkEnd + overlap),
                    filled
                )

                trans.pathValueToPixel(filled)

                if (drawable != null) {
                    drawFilledPath(c, filled, drawable)
                } else {
                    drawFilledPath(c, filled, dataSet.fillColor, dataSet.fillAlpha)
                }
            }

            iterations++
        } while (chunkStart <= chunkEnd)
    }

    /** Builds the outline of the filled area from the entries at [startPosition] to [endPosition] of the reduced list. */
    protected fun generateFilledPath(dataSet: ILineDataSet<*>, startPosition: Int, endPosition: Int, outputPath: Path) {
        val fillMin = dataSet.fillFormatter.getFillLinePosition(dataSet, chart)
        val phaseY = animator.phaseY
        val isDrawSteppedEnabled = dataSet.mode == LineDataSet.Mode.STEPPED

        val filled = outputPath
        filled.reset()

        val entry = dataSet.getEntryForIndex(reducer.indices[startPosition])

        filled.moveTo(entry.x, fillMin)
        filled.lineTo(entry.x, entry.y * phaseY)

        var currentEntry: Entry<*>? = null
        var previousEntry = entry
        for (x in startPosition + 1..endPosition) {
            val current = dataSet.getEntryForIndex(reducer.indices[x])
            currentEntry = current

            if (isDrawSteppedEnabled) {
                filled.lineTo(current.x, previousEntry.y * phaseY)
            }

            filled.lineTo(current.x, current.y * phaseY)

            previousEntry = current
        }

        if (currentEntry != null) {
            filled.lineTo(currentEntry.x, fillMin)
        }

        filled.close()
    }

    /**
     * Draws the y value and icon above each entry that was drawn, keeping clear of the circle radius.
     */
    override fun drawValues(c: Canvas) {
        val dataSets = chart.lineData?.dataSets ?: return

        for (i in dataSets.indices) {
            val dataSet = dataSets[i]

            if (!shouldDrawValues(chart, dataSet) || dataSet.entryCount < 1) continue

            applyValueTextStyle(dataSet)

            val trans = chart.getTransformer(dataSet.axisDependency)

            // keep the values clear of the circles
            var valOffset = (Utils.convertDpToPixel(dataSet.circleRadius) * 1.75f).toInt()

            if (!dataSet.isDrawCirclesEnabled) valOffset /= 2

            xBounds.set(chart, dataSet)
            reduceVisible(chart, dataSet)

            val positions = reducedPositions(dataSet, trans, animator.phaseY)

            val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

            for (k in 0 until reducer.count) {
                val x = positions[2 * k]
                val y = positions[2 * k + 1]

                if (!viewPortHandler.isInBoundsRight(x)) break

                if (!viewPortHandler.isInBoundsLeft(x) || !viewPortHandler.isInBoundsY(y)) continue

                val index = reducer.indices[k]
                val entry = dataSet.getEntryForIndex(index)

                if (dataSet.isDrawValuesEnabled) {
                    drawValue(c, dataSet.valueFormatter, entry.y, entry, i, x, y - valOffset, dataSet.getValueTextColor(index))
                }

                val icon = entry.icon
                if (icon != null && dataSet.isDrawIconsEnabled) {
                    Utils.drawImage(
                        c,
                        icon,
                        (x + iconsOffset.x).toInt(),
                        (y + iconsOffset.y).toInt(),
                        icon.intrinsicWidth,
                        icon.intrinsicHeight
                    )
                }
            }

            MPPointF.recycleInstance(iconsOffset)
        }
    }

    /**
     * Draws the circles, see [drawCircles].
     */
    override fun drawExtras(c: Canvas) {
        drawCircles(c)
    }

    /**
     * Draws a circle at each visible entry of every data set that enables circles, using one cached bitmap per
     * circle color. Circle radius and hole radius come from the data set in dp; the hole is transparent when the
     * hole color is none. While decimation is on, the circles that another circle would cover are left out.
     */
    protected open fun drawCircles(c: Canvas) {
        renderPaint.style = Paint.Style.FILL

        val phaseY = animator.phaseY

        val dataSets = chart.lineData?.dataSets ?: return

        for (dataSet in dataSets) {
            if (!dataSet.isVisible || !dataSet.isDrawCirclesEnabled || dataSet.entryCount == 0) continue

            circlePaintInner.color = dataSet.circleHoleColor

            val trans = chart.getTransformer(dataSet.axisDependency)

            xBounds.set(chart, dataSet)
            reduceVisible(chart, dataSet)

            val circleRadius = Utils.convertDpToPixel(dataSet.circleRadius)
            val circleHoleRadius = Utils.convertDpToPixel(dataSet.circleHoleRadius)
            val drawCircleHole = dataSet.isDrawCircleHoleEnabled &&
                circleHoleRadius < circleRadius &&
                circleHoleRadius > 0f
            val drawTransparentCircleHole = drawCircleHole &&
                dataSet.circleHoleColor == ColorTemplate.COLOR_NONE

            val imageCache = imageCaches.getOrPut(dataSet) { DataSetImageCache() }

            val changeRequired = imageCache.init(dataSet, drawCircleHole, drawTransparentCircleHole)

            if (changeRequired) {
                imageCache.fill(dataSet, drawCircleHole, drawTransparentCircleHole)
            }

            val floatsNeeded = reducer.count * 2

            if (circlePointBuffer.size < floatsNeeded) circlePointBuffer = FloatArray(floatsNeeded * 2)

            var w = 0
            for (k in 0 until reducer.count) {
                val e = dataSet.getEntryForIndex(reducer.indices[k])
                circlePointBuffer[w++] = e.x
                circlePointBuffer[w++] = e.y * phaseY
            }

            trans.pointValuesToPixel(circlePointBuffer, floatsNeeded)

            // Once the points are closer together than a circle is wide the circles cover each other, so only
            // every so many are worth drawing.
            val circleDiameter = circleRadius * 2f
            val cullCircles = chart.isDecimationEnabled
            var lastCircleX = Float.NEGATIVE_INFINITY

            for (i in 0 until reducer.count) {
                val x = circlePointBuffer[i * 2]
                val y = circlePointBuffer[i * 2 + 1]

                if (!viewPortHandler.isInBoundsRight(x)) break

                if (!viewPortHandler.isInBoundsLeft(x) || !viewPortHandler.isInBoundsY(y)) continue

                if (cullCircles && x - lastCircleX < circleDiameter) continue

                lastCircleX = x

                val circleBitmap = imageCache.getBitmap(reducer.indices[i])

                if (circleBitmap != null) {
                    c.drawBitmap(circleBitmap, x - circleRadius, y - circleRadius, null)
                }
            }
        }
    }

    private val highlightCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun drawHighlighted(c: Canvas, indices: List<Highlight>) {
        val lineData = chart.lineData ?: return

        for (high in indices) {
            val set = lineData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val e = set.getEntryForXValue(high.x, high.y)

            if (e == null || !isInBoundsX(e, set)) continue

            val pix = chart.getTransformer(set.axisDependency).getPixelForValues(e.x, e.y * animator.phaseY)

            high.setDraw(pix.x.toFloat(), pix.y.toFloat())

            drawHighlightLines(c, pix.x.toFloat(), pix.y.toFloat(), set)

            if (set.isDrawHighlightCircleEnabled) {
                drawHighlightCircle(c, pix.x.toFloat(), pix.y.toFloat(), set)
            }
        }
    }

    /** Draws a soft halo in the highlight color, a ring in the line color and a center in the circle hole color at ([x], [y]) px. */
    protected open fun drawHighlightCircle(c: Canvas, x: Float, y: Float, set: ILineDataSet<*>) {
        val radius = Utils.convertDpToPixel(set.highlightCircleRadius)
        highlightCirclePaint.style = Paint.Style.FILL
        highlightCirclePaint.color = set.highlightColor
        highlightCirclePaint.alpha = 70
        c.drawCircle(x, y, radius * 1.9f, highlightCirclePaint)
        highlightCirclePaint.color = set.circleHoleColor
        highlightCirclePaint.alpha = 255
        c.drawCircle(x, y, radius, highlightCirclePaint)
        highlightCirclePaint.style = Paint.Style.STROKE
        highlightCirclePaint.strokeWidth = Utils.convertDpToPixel(2.5f)
        highlightCirclePaint.color = set.color
        c.drawCircle(x, y, radius, highlightCirclePaint)
    }

    /**
     * Frees the offscreen bitmap and its canvas. The next [drawData] creates a new one.
     */
    fun releaseBitmap() {
        bitmapCanvas?.setBitmap(null)
        bitmapCanvas = null

        drawBitmap?.let {
            it.get()?.recycle()
            it.clear()
        }
        drawBitmap = null
    }

    private inner class DataSetImageCache {

        private val circlePathBuffer = Path()

        private var circleBitmaps: Array<Bitmap?>? = null

        private var cachedColors = IntArray(0)
        private var cachedRadius = Float.NaN
        private var cachedHoleRadius = Float.NaN
        private var cachedHoleColor = 0
        private var cachedHole = false
        private var cachedTransparentHole = false

        /**
         * Returns true, and drops the cached bitmaps, when anything the circles are drawn from has changed since
         * the last pass.
         */
        fun init(set: ILineDataSet<*>, drawCircleHole: Boolean, drawTransparentCircleHole: Boolean): Boolean {
            val size = set.circleColorCount
            val bitmaps = circleBitmaps

            if (bitmaps != null && bitmaps.size == size &&
                set.circleRadius == cachedRadius &&
                set.circleHoleRadius == cachedHoleRadius &&
                set.circleHoleColor == cachedHoleColor &&
                drawCircleHole == cachedHole &&
                drawTransparentCircleHole == cachedTransparentHole &&
                sameColors(set, size)
            ) {
                return false
            }

            circleBitmaps = arrayOfNulls(size)
            if (cachedColors.size != size) cachedColors = IntArray(size)
            for (i in 0 until size) cachedColors[i] = set.getCircleColor(i)
            cachedRadius = set.circleRadius
            cachedHoleRadius = set.circleHoleRadius
            cachedHoleColor = set.circleHoleColor
            cachedHole = drawCircleHole
            cachedTransparentHole = drawTransparentCircleHole
            return true
        }

        private fun sameColors(set: ILineDataSet<*>, size: Int): Boolean {
            if (cachedColors.size != size) return false
            for (i in 0 until size) {
                if (cachedColors[i] != set.getCircleColor(i)) return false
            }
            return true
        }

        fun fill(set: ILineDataSet<*>, drawCircleHole: Boolean, drawTransparentCircleHole: Boolean) {
            val bitmaps = circleBitmaps ?: return
            val colorCount = set.circleColorCount
            val circleRadius = Utils.convertDpToPixel(set.circleRadius)
            val circleHoleRadius = Utils.convertDpToPixel(set.circleHoleRadius)

            for (i in 0 until colorCount) {
                val circleBitmap = Bitmap.createBitmap((circleRadius * 2.1).toInt(), (circleRadius * 2.1).toInt(), Bitmap.Config.ARGB_8888)

                val canvas = Canvas(circleBitmap)
                bitmaps[i] = circleBitmap
                renderPaint.color = set.getCircleColor(i)

                if (drawTransparentCircleHole) {
                    circlePathBuffer.reset()

                    circlePathBuffer.addCircle(circleRadius, circleRadius, circleRadius, Path.Direction.CW)

                    circlePathBuffer.addCircle(circleRadius, circleRadius, circleHoleRadius, Path.Direction.CCW)

                    canvas.drawPath(circlePathBuffer, renderPaint)
                } else {
                    canvas.drawCircle(circleRadius, circleRadius, circleRadius, renderPaint)

                    if (drawCircleHole) {
                        canvas.drawCircle(circleRadius, circleRadius, circleHoleRadius, circlePaintInner)
                    }
                }
            }
        }

        fun getBitmap(index: Int): Bitmap? {
            val bitmaps = circleBitmaps ?: return null
            return bitmaps[index % bitmaps.size]
        }
    }
}
