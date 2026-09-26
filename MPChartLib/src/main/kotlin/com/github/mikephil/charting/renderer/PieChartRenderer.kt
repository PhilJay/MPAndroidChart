package com.github.mikephil.charting.renderer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.lang.ref.WeakReference
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Draws a [PieChart]. The slices are drawn onto an offscreen bitmap in [drawData], enlarged slices for
 * highlights in [drawHighlighted], and [drawExtras] adds the hole, composes the bitmap and draws the center
 * text. [drawValues] draws the entry labels and values inside or outside the slices. Angles are in degrees
 * clockwise from 3 o'clock, offset by the chart's rotation angle; radii are pixels.
 *
 * @property chart The pie chart, which supplies the data, the circle box, the slice angles and the hole settings.
 */
public open class PieChartRenderer(protected val chart: PieChart, animator: ChartAnimator, viewPortHandler: ViewPortHandler) : DataRenderer(animator, viewPortHandler) {

    /**
     * Paint for the hole in the middle of the chart; white by default. A fully transparent color skips the hole.
     */
    public var paintHole: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    /**
     * Paint for the translucent ring between the hole and the transparent circle radius; white at alpha 105 by
     * default. The alpha is scaled by the animation phases while drawing.
     */
    public val paintTransparentCircle: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        alpha = 105
    }

    /**
     * Text paint for the center text; black, 12 dp by default.
     */
    public var paintCenterText: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = Utils.convertDpToPixel(12f)
    }

    /**
     * Paint for the entry labels drawn at the slices; white, centered, 13 dp by default.
     */
    public val paintEntryLabels: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = Utils.convertDpToPixel(13f)
    }

    /**
     * Stroke paint for the lines that connect outside labels to their slice; color and width (dp) come from the
     * data set.
     */
    protected val valueLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private var centerTextLayout: StaticLayout? = null
    private var centerTextLastValue: CharSequence? = null
    private val centerTextLastBounds = RectF()
    private val rectBuffer = arrayOf(RectF(), RectF(), RectF())

    /**
     * Weak reference to the offscreen bitmap that slices and the hole are drawn onto; recreated when the chart size
     * changes.
     */
    protected var drawBitmap: WeakReference<Bitmap>? = null
    /**
     * Canvas onto [drawBitmap]; null before the first [drawData] and after [releaseBitmap].
     */
    protected var bitmapCanvas: Canvas? = null

    private val pathBuffer = Path()
    private val innerRectBuffer = RectF()
    private val roundedCircleBox = RectF()
    private val holeCirclePath = Path()
    /**
     * Path used to clip the center text to the hole.
     */
    protected val drawCenterTextPathBuffer: Path = Path()
    /**
     * Circle box enlarged by the selection shift, used for highlighted slices.
     */
    protected val drawHighlightedRectF: RectF = RectF()

    init {
        valuePaint.textSize = Utils.convertDpToPixel(13f)
        valuePaint.color = Color.WHITE
        valuePaint.textAlign = Paint.Align.CENTER
    }

    /**
     * Does nothing; slices are drawn without a buffer.
     */
    override fun initBuffers() {
    }

    /**
     * Creates the offscreen bitmap when needed, clears it and draws every visible data set onto it. The bitmap is
     * composed onto the canvas later by [drawExtras]. Does nothing while the chart has no size.
     */
    override fun drawData(c: Canvas) {
        val width = viewPortHandler.chartWidth.toInt()
        val height = viewPortHandler.chartHeight.toInt()

        var bitmap = drawBitmap?.get()

        if (bitmap == null || bitmap.width != width || bitmap.height != height) {
            if (width > 0 && height > 0) {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                drawBitmap = WeakReference(bitmap)
                bitmapCanvas = Canvas(bitmap)
            } else {
                return
            }
        }

        bitmap.eraseColor(Color.TRANSPARENT)

        val pieData = chart.data ?: return

        for (set in pieData.dataSets) {
            if (set.isVisible && set.entryCount > 0) drawDataSet(c, set)
        }
    }

    /**
     * Returns the smallest inner radius (pixels) at which a slice with slice spacing still has room for the space
     * on both of its sides, so that the slice keeps a straight inner edge.
     *
     * @param center center of the pie in pixels
     * @param radius outer radius in pixels
     * @param angle sweep of the slice in degrees, before the spacing is removed
     * @param arcStartPointX x pixel where the outer arc starts
     * @param arcStartPointY y pixel where the outer arc starts
     * @param startAngle start angle of the outer arc in degrees
     * @param sweepAngle sweep of the outer arc in degrees
     */
    protected fun calculateMinimumRadiusForSpacedSlice(
        center: MPPointF,
        radius: Float,
        angle: Float,
        arcStartPointX: Float,
        arcStartPointY: Float,
        startAngle: Float,
        sweepAngle: Float
    ): Float {
        val angleMiddle = startAngle + sweepAngle / 2f

        val arcEndPointX = center.x + radius * cos((startAngle + sweepAngle) * Utils.FDEG2RAD)
        val arcEndPointY = center.y + radius * sin((startAngle + sweepAngle) * Utils.FDEG2RAD)

        val arcMidPointX = center.x + radius * cos(angleMiddle * Utils.FDEG2RAD)
        val arcMidPointY = center.y + radius * sin(angleMiddle * Utils.FDEG2RAD)

        val basePointsDistance = sqrt(
            (arcEndPointX - arcStartPointX).toDouble().pow(2.0) +
                (arcEndPointY - arcStartPointY).toDouble().pow(2.0)
        )

        // the triangle inside the slice keeps its angle when the space is removed from both sides
        val containedTriangleHeight = (basePointsDistance / 2.0 * tan((180.0 - angle) / 2.0 * Utils.DEG2RAD)).toFloat()

        var spacedRadius = radius - containedTriangleHeight

        spacedRadius -= sqrt(
            (arcMidPointX - (arcEndPointX + arcStartPointX) / 2f).toDouble().pow(2.0) +
                (arcMidPointY - (arcEndPointY + arcStartPointY) / 2f).toDouble().pow(2.0)
        ).toFloat()

        return spacedRadius
    }

    /**
     * Returns the slice space of [dataSet] in pixels, or 0 when the data set disables spacing automatically and
     * its smallest slice would be swallowed by the space.
     */
    protected fun getSliceSpace(dataSet: IPieDataSet<*>): Float {
        if (!dataSet.isAutomaticallyDisableSliceSpacingEnabled) return Utils.convertDpToPixel(dataSet.sliceSpace)

        val yValueSum = chart.data?.yValueSum ?: return Utils.convertDpToPixel(dataSet.sliceSpace)
        val spaceSizeRatio = Utils.convertDpToPixel(dataSet.sliceSpace) / viewPortHandler.smallestContentExtension
        val minValueRatio = dataSet.yMin / yValueSum * 2

        return if (spaceSizeRatio > minValueRatio) 0f else Utils.convertDpToPixel(dataSet.sliceSpace)
    }

    /**
     * Draws all slices of [dataSet] onto the bitmap canvas, as arcs around the hole with the slice space removed
     * on both sides. Empty slices are skipped, and so are highlighted ones (drawn by [drawHighlighted]) unless the
     * chart uses rounded slices. Does nothing before the first [drawData].
     */
    protected open fun drawDataSet(c: Canvas, dataSet: IPieDataSet<*>) {
        val bitmapCanvas = bitmapCanvas ?: return

        var angle = 0f
        val rotationAngle = chart.rotationAngle

        val phaseX = animator.phaseX
        val phaseY = animator.phaseY

        val circleBox = chart.circleBox

        val drawAngles = chart.drawAngles
        val entryCount = min(dataSet.entryCount, drawAngles.size)
        val center = chart.centerCircleBox
        val radius = chart.radius
        val drawInnerArc = chart.isDrawHoleEnabled && !chart.isDrawSlicesUnderHoleEnabled
        val userInnerRadius = if (drawInnerArc) radius * (chart.holeRadius / 100f) else 0f
        val roundedRadius = (radius - (radius * chart.holeRadius / 100f)) / 2f
        val drawRoundedSlices = drawInnerArc && chart.isDrawRoundedSlicesEnabled
        val roundedStartSweep = if (chart.isRoundedSlicesReversed) 180f else -180f

        var visibleAngleCount = 0
        for (j in 0 until entryCount) {
            if (abs(dataSet.getEntryForIndex(j).y) > Utils.FLOAT_EPSILON) {
                visibleAngleCount++
            }
        }

        val sliceSpace = if (visibleAngleCount <= 1) 0f else getSliceSpace(dataSet)

        for (j in 0 until entryCount) {
            val sliceAngle = drawAngles[j]
            var innerRadius = userInnerRadius

            val e = dataSet.getEntryForIndex(j)

            if (!(abs(e.y) > Utils.FLOAT_EPSILON)) {
                angle += sliceAngle * phaseX
                continue
            }

            if (dataSet.isHighlightEnabled && chart.needsHighlight(j)) {
                angle += sliceAngle * phaseX
                continue
            }

            val accountForSliceSpacing = sliceSpace > 0f && sliceAngle <= 180f

            renderPaint.color = dataSet.getColor(j)

            val sliceSpaceAngleOuter = if (visibleAngleCount == 1) 0f else sliceSpace / (Utils.FDEG2RAD * radius)
            val startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2f) * phaseY
            var sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY
            if (sweepAngleOuter < 0f) {
                sweepAngleOuter = 0f
            }

            pathBuffer.reset()

            if (drawRoundedSlices) {
                val x = center.x + (radius - roundedRadius) * cos(startAngleOuter * Utils.FDEG2RAD)
                val y = center.y + (radius - roundedRadius) * sin(startAngleOuter * Utils.FDEG2RAD)
                roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius)
            }

            val arcStartPointX = center.x + radius * cos(startAngleOuter * Utils.FDEG2RAD)
            val arcStartPointY = center.y + radius * sin(startAngleOuter * Utils.FDEG2RAD)

            if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                // Android draws arcs "mod 360", so a full sweep needs a circle
                pathBuffer.addCircle(center.x, center.y, radius, Path.Direction.CW)
            } else {
                if (drawRoundedSlices) {
                    pathBuffer.arcTo(roundedCircleBox, startAngleOuter + 180, roundedStartSweep)
                }

                pathBuffer.arcTo(circleBox, startAngleOuter, sweepAngleOuter)
            }

            innerRectBuffer.set(
                center.x - innerRadius,
                center.y - innerRadius,
                center.x + innerRadius,
                center.y + innerRadius
            )

            if (drawInnerArc && (innerRadius > 0f || accountForSliceSpacing)) {
                if (accountForSliceSpacing) {
                    var minSpacedRadius = calculateMinimumRadiusForSpacedSlice(
                        center, radius,
                        sliceAngle * phaseY,
                        arcStartPointX, arcStartPointY,
                        startAngleOuter,
                        sweepAngleOuter
                    )

                    if (minSpacedRadius < 0f) minSpacedRadius = -minSpacedRadius

                    innerRadius = max(innerRadius, minSpacedRadius)
                }

                val sliceSpaceAngleInner = if (visibleAngleCount == 1 || innerRadius == 0f) 0f else sliceSpace / (Utils.FDEG2RAD * innerRadius)
                val startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2f) * phaseY
                var sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY
                if (sweepAngleInner < 0f) {
                    sweepAngleInner = 0f
                }
                val endAngleInner = startAngleInner + sweepAngleInner

                if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                    pathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW)
                } else {
                    if (drawRoundedSlices) {
                        val x = center.x + (radius - roundedRadius) * cos(endAngleInner * Utils.FDEG2RAD)
                        val y = center.y + (radius - roundedRadius) * sin(endAngleInner * Utils.FDEG2RAD)
                        roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius)
                        pathBuffer.arcTo(roundedCircleBox, endAngleInner, -roundedStartSweep)
                    } else {
                        pathBuffer.lineTo(
                            center.x + innerRadius * cos(endAngleInner * Utils.FDEG2RAD),
                            center.y + innerRadius * sin(endAngleInner * Utils.FDEG2RAD)
                        )
                    }

                    pathBuffer.arcTo(innerRectBuffer, endAngleInner, -sweepAngleInner)
                }
            } else {
                if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
                    if (accountForSliceSpacing) {
                        val angleMiddle = startAngleOuter + sweepAngleOuter / 2f

                        val sliceSpaceOffset = calculateMinimumRadiusForSpacedSlice(
                            center,
                            radius,
                            sliceAngle * phaseY,
                            arcStartPointX,
                            arcStartPointY,
                            startAngleOuter,
                            sweepAngleOuter
                        )

                        val arcEndPointX = center.x + sliceSpaceOffset * cos(angleMiddle * Utils.FDEG2RAD)
                        val arcEndPointY = center.y + sliceSpaceOffset * sin(angleMiddle * Utils.FDEG2RAD)

                        pathBuffer.lineTo(arcEndPointX, arcEndPointY)
                    } else {
                        pathBuffer.lineTo(center.x, center.y)
                    }
                }
            }

            pathBuffer.close()

            bitmapCanvas.drawPath(pathBuffer, renderPaint)

            angle += sliceAngle * phaseX
        }

        MPPointF.recycleInstance(center)
    }

    /**
     * Draws the value and the entry label of each slice, either inside the slice or outside it at the end of a
     * two-part value line, and the entry icon at the label radius.
     */
    override fun drawValues(c: Canvas) {
        val center = chart.centerCircleBox

        val radius = chart.radius
        var rotationAngle = chart.rotationAngle
        val drawAngles = chart.drawAngles
        val absoluteAngles = chart.absoluteAngles

        val phaseX = animator.phaseX
        val phaseY = animator.phaseY

        val roundedRadius = (radius - (radius * chart.holeRadius / 100f)) / 2f
        val holeRadiusPercent = chart.holeRadius / 100f
        var labelRadiusOffset = radius / 10f * 3.6f

        if (chart.isDrawHoleEnabled) {
            labelRadiusOffset = (radius - (radius * holeRadiusPercent)) / 2f

            if (!chart.isDrawSlicesUnderHoleEnabled && chart.isDrawRoundedSlicesEnabled) {
                // shift by the rounded slice so the label sits inside it
                val roundedAngle = (roundedRadius * 360 / (Math.PI * 2 * radius)).toFloat()
                rotationAngle += if (chart.isRoundedSlicesReversed) -roundedAngle else roundedAngle
            }
        }

        val labelRadius = radius - labelRadiusOffset

        val data = chart.data ?: run {
            MPPointF.recycleInstance(center)
            return
        }
        val dataSets = data.dataSets

        val yValueSum = chart.absoluteValueSum

        val drawEntryLabels = chart.isDrawEntryLabelsEnabled

        var angle: Float
        var xIndex = 0

        c.save()

        val offset = Utils.convertDpToPixel(5f)

        for (i in dataSets.indices) {
            val dataSet = dataSets[i]

            val drawValues = dataSet.isDrawValuesEnabled

            if (!drawValues && !drawEntryLabels) continue

            val xValuePosition = dataSet.xValuePosition
            val yValuePosition = dataSet.yValuePosition

            applyValueTextStyle(dataSet)

            val lineHeight = Utils.calcTextHeight(valuePaint, "Q") + Utils.convertDpToPixel(4f)

            val formatter = dataSet.valueFormatter

            val entryCount = dataSet.entryCount

            val isUseValueColorForLineEnabled = dataSet.isUseValueColorForLineEnabled
            val valueLineColor = dataSet.valueLineColor

            valueLinePaint.strokeWidth = Utils.convertDpToPixel(dataSet.valueLineWidth)

            val sliceSpace = getSliceSpace(dataSet)

            val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

            for (j in 0 until entryCount) {
                if (xIndex >= drawAngles.size || xIndex >= absoluteAngles.size) break

                val entry = dataSet.getEntryForIndex(j)

                angle = if (xIndex == 0) 0f else absoluteAngles[xIndex - 1] * phaseX

                val sliceAngle = drawAngles[xIndex]
                val sliceSpaceMiddleAngle = sliceSpace / (Utils.FDEG2RAD * labelRadius)

                val angleOffset = (sliceAngle - sliceSpaceMiddleAngle / 2f) / 2f

                angle += angleOffset

                val transformedAngle = rotationAngle + angle * phaseY

                val value = if (!chart.isUsePercentValuesEnabled) entry.y else if (yValueSum == 0f) 0f else entry.y / yValueSum * 100f
                val entryLabel = entry.label

                val sliceXBase = cos(transformedAngle * Utils.FDEG2RAD)
                val sliceYBase = sin(transformedAngle * Utils.FDEG2RAD)

                val drawXOutside = drawEntryLabels && xValuePosition == PieDataSet.ValuePosition.OUTSIDE_SLICE
                val drawYOutside = drawValues && yValuePosition == PieDataSet.ValuePosition.OUTSIDE_SLICE
                val drawXInside = drawEntryLabels && xValuePosition == PieDataSet.ValuePosition.INSIDE_SLICE
                val drawYInside = drawValues && yValuePosition == PieDataSet.ValuePosition.INSIDE_SLICE

                if (drawXOutside || drawYOutside) {
                    val valueLineLength1 = dataSet.valueLinePart1Length
                    val valueLineLength2 = dataSet.valueLinePart2Length
                    val valueLinePart1OffsetPercentage = dataSet.valueLinePart1OffsetPercentage / 100f

                    val pt2x: Float
                    val pt2y: Float
                    val labelPtx: Float
                    val labelPty: Float

                    val line1Radius = if (chart.isDrawHoleEnabled) {
                        (radius - (radius * holeRadiusPercent)) * valueLinePart1OffsetPercentage + (radius * holeRadiusPercent)
                    } else {
                        radius * valueLinePart1OffsetPercentage
                    }

                    val polyline2Width = if (dataSet.isValueLineVariableLength) {
                        labelRadius * valueLineLength2 * abs(sin(transformedAngle * Utils.FDEG2RAD))
                    } else {
                        labelRadius * valueLineLength2
                    }

                    val pt0x = line1Radius * sliceXBase + center.x
                    val pt0y = line1Radius * sliceYBase + center.y

                    val pt1x = labelRadius * (1 + valueLineLength1) * sliceXBase + center.x
                    val pt1y = labelRadius * (1 + valueLineLength1) * sliceYBase + center.y

                    if (transformedAngle % 360.0 >= 90.0 && transformedAngle % 360.0 <= 270.0) {
                        pt2x = pt1x - polyline2Width
                        pt2y = pt1y

                        valuePaint.textAlign = Paint.Align.RIGHT

                        if (drawXOutside) paintEntryLabels.textAlign = Paint.Align.RIGHT

                        labelPtx = pt2x - offset
                        labelPty = pt2y
                    } else {
                        pt2x = pt1x + polyline2Width
                        pt2y = pt1y
                        valuePaint.textAlign = Paint.Align.LEFT

                        if (drawXOutside) paintEntryLabels.textAlign = Paint.Align.LEFT

                        labelPtx = pt2x + offset
                        labelPty = pt2y
                    }

                    var lineColor = ColorTemplate.COLOR_NONE

                    if (isUseValueColorForLineEnabled) {
                        lineColor = dataSet.getColor(j)
                    } else if (valueLineColor != ColorTemplate.COLOR_NONE) {
                        lineColor = valueLineColor
                    }

                    if (lineColor != ColorTemplate.COLOR_NONE) {
                        valueLinePaint.color = lineColor
                        c.drawLine(pt0x, pt0y, pt1x, pt1y, valueLinePaint)
                        c.drawLine(pt1x, pt1y, pt2x, pt2y, valueLinePaint)
                    }

                    if (drawXOutside && drawYOutside) {
                        drawValue(c, formatter, value, entry, 0, labelPtx, labelPty, dataSet.getValueTextColor(j))

                        if (j < data.entryCount && entryLabel != null) {
                            val labelY = labelPty + lineHeight + extraLineShift(formatter, value, entry, entryLabel)
                            drawEntryLabel(c, entryLabel, labelPtx, labelY)
                        }
                    } else if (drawXOutside) {
                        if (j < data.entryCount && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, labelPtx, labelPty + lineHeight / 2f)
                        }
                    } else if (drawYOutside) {
                        drawValue(c, formatter, value, entry, 0, labelPtx, labelPty + lineHeight / 2f, dataSet.getValueTextColor(j))
                    }
                }

                if (drawXInside || drawYInside) {
                    val x = labelRadius * sliceXBase + center.x
                    val y = labelRadius * sliceYBase + center.y

                    valuePaint.textAlign = Paint.Align.CENTER

                    if (drawXInside && drawYInside) {
                        drawValue(c, formatter, value, entry, 0, x, y, dataSet.getValueTextColor(j))

                        if (j < data.entryCount && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, x, y + lineHeight + extraLineShift(formatter, value, entry, entryLabel))
                        }
                    } else if (drawXInside) {
                        if (j < data.entryCount && entryLabel != null) {
                            drawEntryLabel(c, entryLabel, x, y + lineHeight / 2f)
                        }
                    } else if (drawYInside) {
                        drawValue(c, formatter, value, entry, 0, x, y + lineHeight / 2f, dataSet.getValueTextColor(j))
                    }
                }

                val icon = entry.icon
                if (icon != null && dataSet.isDrawIconsEnabled) {
                    val x = (labelRadius + iconsOffset.y) * sliceXBase + center.x
                    var y = (labelRadius + iconsOffset.y) * sliceYBase + center.y
                    y += iconsOffset.x

                    Utils.drawImage(c, icon, x.toInt(), y.toInt(), icon.intrinsicWidth, icon.intrinsicHeight)
                }

                xIndex++
            }

            MPPointF.recycleInstance(iconsOffset)
        }
        MPPointF.recycleInstance(center)
        c.restore()
    }

    /**
     * Draws an entry label with [paintEntryLabels] at the pixel position ([x], [y]), where [y] is the text baseline.
     * A label containing line breaks is drawn one line per part, with the lines centered vertically on [y].
     */
    protected open fun drawEntryLabel(c: Canvas, label: String, x: Float, y: Float) {
        drawTextLines(c, label, x, y, paintEntryLabels)
    }

    /**
     * Draws one value label with [valuePaint] in [color] at the pixel position ([x], [y]), where [y] is the text
     * baseline. A formatted value containing line breaks is drawn one line per part, with the lines centered
     * vertically on [y].
     */
    override fun drawValue(c: Canvas, formatter: IValueFormatter, value: Float, entry: Entry<*>, dataSetIndex: Int, x: Float, y: Float, color: Int) {
        valuePaint.color = color
        drawTextLines(c, formatter.getFormattedValue(value, entry, dataSetIndex, viewPortHandler), x, y, valuePaint)
    }

    private fun drawTextLines(c: Canvas, text: String, x: Float, y: Float, paint: Paint) {
        if ('\n' !in text) {
            c.drawText(text, x, y, paint)
            return
        }

        val lines = text.split('\n')
        val lineSpacing = paint.fontSpacing
        var lineY = y - (lines.size - 1) * lineSpacing / 2f
        for (line in lines) {
            c.drawText(line, x, lineY, paint)
            lineY += lineSpacing
        }
    }

    /** How far a label below a value moves down so that the extra lines of both do not overlap. */
    private fun extraLineShift(formatter: IValueFormatter, value: Float, entry: Entry<*>, label: String): Float {
        val extraValueLines = formatter.getFormattedValue(value, entry, 0, viewPortHandler).count { it == '\n' }
        val extraLabelLines = label.count { it == '\n' }
        return (extraValueLines * valuePaint.fontSpacing + extraLabelLines * paintEntryLabels.fontSpacing) / 2f
    }

    /**
     * Draws the hole onto the bitmap, composes the bitmap onto [c] and draws the center text on top.
     */
    override fun drawExtras(c: Canvas) {
        drawHole(c)
        drawBitmap?.get()?.let { c.drawBitmap(it, 0f, 0f, null) }
        drawCenterText(c)
    }

    /**
     * Draws the hole and the transparent circle onto the bitmap canvas when the chart enables the hole. The
     * transparent circle is only drawn when its radius is larger than the hole radius.
     */
    protected open fun drawHole(c: Canvas) {
        val bitmapCanvas = bitmapCanvas ?: return
        if (!chart.isDrawHoleEnabled) return

        val radius = chart.radius
        val holeRadius = radius * (chart.holeRadius / 100)
        val center = chart.centerCircleBox

        if (Color.alpha(paintHole.color) > 0) {
            bitmapCanvas.drawCircle(center.x, center.y, holeRadius, paintHole)
        }

        // only visible when it reaches beyond the hole
        if (Color.alpha(paintTransparentCircle.color) > 0 && chart.transparentCircleRadius > chart.holeRadius) {
            val alpha = paintTransparentCircle.alpha
            val secondHoleRadius = radius * (chart.transparentCircleRadius / 100)

            paintTransparentCircle.alpha = (alpha.toFloat() * animator.phaseX * animator.phaseY).toInt()

            holeCirclePath.reset()
            holeCirclePath.addCircle(center.x, center.y, secondHoleRadius, Path.Direction.CW)
            holeCirclePath.addCircle(center.x, center.y, holeRadius, Path.Direction.CCW)
            bitmapCanvas.drawPath(holeCirclePath, paintTransparentCircle)

            paintTransparentCircle.alpha = alpha
        }
        MPPointF.recycleInstance(center)
    }

    /**
     * Draws the center text of the chart, wrapped to the center text radius percent of the hole, centered and
     * clipped to the hole (or to the whole pie without a hole). The text layout is cached until the text or its
     * bounds change. Does nothing when the chart disables the center text.
     */
    protected open fun drawCenterText(c: Canvas) {
        val centerText = chart.centerText

        if (!chart.isDrawCenterTextEnabled) return

        val center = chart.centerCircleBox
        val offset = chart.centerTextOffset

        val x = center.x + Utils.convertDpToPixel(offset.x)
        val y = center.y + Utils.convertDpToPixel(offset.y)

        val innerRadius = if (chart.isDrawHoleEnabled && !chart.isDrawSlicesUnderHoleEnabled) {
            chart.radius * (chart.holeRadius / 100f)
        } else {
            chart.radius
        }

        val holeRect = rectBuffer[0]
        holeRect.left = x - innerRadius
        holeRect.top = y - innerRadius
        holeRect.right = x + innerRadius
        holeRect.bottom = y + innerRadius
        val boundingRect = rectBuffer[1]
        boundingRect.set(holeRect)

        val radiusPercent = chart.centerTextRadiusPercent / 100f
        if (radiusPercent > 0.0) {
            boundingRect.inset(
                (boundingRect.width() - boundingRect.width() * radiusPercent) / 2f,
                (boundingRect.height() - boundingRect.height() * radiusPercent) / 2f
            )
        }

        var layout = centerTextLayout
        if (layout == null || centerText != centerTextLastValue || boundingRect != centerTextLastBounds) {
            centerTextLastBounds.set(boundingRect)
            centerTextLastValue = centerText

            val width = centerTextLastBounds.width()

            // a width of 0 crashes the layout
            layout = StaticLayout.Builder
                .obtain(centerText, 0, centerText.length, paintCenterText, max(ceil(width), 1f).toInt())
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
            centerTextLayout = layout
        }

        val layoutHeight = layout.height.toFloat()

        c.save()
        val path = drawCenterTextPathBuffer
        path.reset()
        path.addOval(holeRect, Path.Direction.CW)
        c.clipPath(path)

        c.translate(boundingRect.left, boundingRect.top + (boundingRect.height() - layoutHeight) / 2f)
        layout.draw(c)

        c.restore()

        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(offset)
    }

    /**
     * Redraws each highlighted slice onto the bitmap canvas, enlarged by the selection shift (dp) of its data set
     * and in the highlight color when the data set has one.
     */
    override fun drawHighlighted(c: Canvas, indices: List<Highlight>) {
        val drawInnerArc = chart.isDrawHoleEnabled && !chart.isDrawSlicesUnderHoleEnabled
        val drawRoundedSlices = drawInnerArc && chart.isDrawRoundedSlicesEnabled
        val roundedStartSweep = if (chart.isRoundedSlicesReversed) 180f else -180f

        val bitmapCanvas = bitmapCanvas ?: return
        val data = chart.data ?: return

        val phaseX = animator.phaseX
        val phaseY = animator.phaseY

        var angle: Float
        val rotationAngle = chart.rotationAngle

        val drawAngles = chart.drawAngles
        val absoluteAngles = chart.absoluteAngles
        val center = chart.centerCircleBox
        val radius = chart.radius
        val userInnerRadius = if (drawInnerArc) radius * (chart.holeRadius / 100f) else 0f

        val highlightedCircleBox = drawHighlightedRectF
        highlightedCircleBox.set(0f, 0f, 0f, 0f)

        for (i in indices.indices) {
            val index = indices[i].x.toInt()

            if (index !in drawAngles.indices || index > absoluteAngles.size) continue

            val set = data.getDataSetByIndex(indices[i].dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val entryCount = set.entryCount
            var visibleAngleCount = 0
            for (j in 0 until entryCount) {
                if (abs(set.getEntryForIndex(j).y) > Utils.FLOAT_EPSILON) {
                    visibleAngleCount++
                }
            }

            angle = if (index == 0) 0f else absoluteAngles[index - 1] * phaseX

            val sliceSpace = if (visibleAngleCount <= 1) 0f else Utils.convertDpToPixel(set.sliceSpace)

            val sliceAngle = drawAngles[index]
            var innerRadius = userInnerRadius

            val shift = Utils.convertDpToPixel(set.selectionShift)
            val highlightedRadius = radius + shift
            highlightedCircleBox.set(chart.circleBox)
            highlightedCircleBox.inset(-shift, -shift)

            val accountForSliceSpacing = sliceSpace > 0f && sliceAngle <= 180f

            renderPaint.color = set.highlightColor ?: set.getColor(index)

            val sliceSpaceAngleOuter = if (visibleAngleCount == 1) 0f else sliceSpace / (Utils.FDEG2RAD * radius)

            val sliceSpaceAngleShifted = if (visibleAngleCount == 1) 0f else sliceSpace / (Utils.FDEG2RAD * highlightedRadius)

            val startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2f) * phaseY
            var sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY
            if (sweepAngleOuter < 0f) {
                sweepAngleOuter = 0f
            }

            val startAngleShifted = rotationAngle + (angle + sliceSpaceAngleShifted / 2f) * phaseY
            var sweepAngleShifted = (sliceAngle - sliceSpaceAngleShifted) * phaseY
            if (sweepAngleShifted < 0f) {
                sweepAngleShifted = 0f
            }

            pathBuffer.reset()

            val roundedRadius = (highlightedRadius - innerRadius) / 2f

            if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                pathBuffer.addCircle(center.x, center.y, highlightedRadius, Path.Direction.CW)
            } else {
                if (drawRoundedSlices) {
                    val x = center.x + (highlightedRadius - roundedRadius) * cos(startAngleShifted * Utils.FDEG2RAD)
                    val y = center.y + (highlightedRadius - roundedRadius) * sin(startAngleShifted * Utils.FDEG2RAD)
                    roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius)
                    pathBuffer.arcTo(roundedCircleBox, startAngleShifted + 180f, roundedStartSweep)
                } else {
                    pathBuffer.moveTo(
                        center.x + highlightedRadius * cos(startAngleShifted * Utils.FDEG2RAD),
                        center.y + highlightedRadius * sin(startAngleShifted * Utils.FDEG2RAD)
                    )
                }

                pathBuffer.arcTo(highlightedCircleBox, startAngleShifted, sweepAngleShifted)
            }

            var sliceSpaceRadius = 0f
            if (accountForSliceSpacing) {
                sliceSpaceRadius = calculateMinimumRadiusForSpacedSlice(
                    center, radius,
                    sliceAngle * phaseY,
                    center.x + radius * cos(startAngleOuter * Utils.FDEG2RAD),
                    center.y + radius * sin(startAngleOuter * Utils.FDEG2RAD),
                    startAngleOuter,
                    sweepAngleOuter
                )
            }

            innerRectBuffer.set(
                center.x - innerRadius,
                center.y - innerRadius,
                center.x + innerRadius,
                center.y + innerRadius
            )

            if (drawInnerArc && (innerRadius > 0f || accountForSliceSpacing)) {
                if (accountForSliceSpacing) {
                    var minSpacedRadius = sliceSpaceRadius

                    if (minSpacedRadius < 0f) minSpacedRadius = -minSpacedRadius

                    innerRadius = max(innerRadius, minSpacedRadius)
                }

                val sliceSpaceAngleInner = if (visibleAngleCount == 1 || innerRadius == 0f) 0f else sliceSpace / (Utils.FDEG2RAD * innerRadius)
                val startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2f) * phaseY
                var sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY
                if (sweepAngleInner < 0f) {
                    sweepAngleInner = 0f
                }
                val endAngleInner = startAngleInner + sweepAngleInner

                if (sweepAngleOuter >= 360f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                    pathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW)
                } else {
                    if (drawRoundedSlices) {
                        val x = center.x + (highlightedRadius - roundedRadius) * cos(endAngleInner * Utils.FDEG2RAD)
                        val y = center.y + (highlightedRadius - roundedRadius) * sin(endAngleInner * Utils.FDEG2RAD)
                        roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius)
                        pathBuffer.arcTo(roundedCircleBox, endAngleInner, -roundedStartSweep)
                    } else {
                        pathBuffer.lineTo(
                            center.x + innerRadius * cos(endAngleInner * Utils.FDEG2RAD),
                            center.y + innerRadius * sin(endAngleInner * Utils.FDEG2RAD)
                        )
                    }

                    pathBuffer.arcTo(innerRectBuffer, endAngleInner, -sweepAngleInner)
                }
            } else {
                if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
                    if (accountForSliceSpacing) {
                        val angleMiddle = startAngleOuter + sweepAngleOuter / 2f

                        val arcEndPointX = center.x + sliceSpaceRadius * cos(angleMiddle * Utils.FDEG2RAD)
                        val arcEndPointY = center.y + sliceSpaceRadius * sin(angleMiddle * Utils.FDEG2RAD)

                        pathBuffer.lineTo(arcEndPointX, arcEndPointY)
                    } else {
                        pathBuffer.lineTo(center.x, center.y)
                    }
                }
            }

            pathBuffer.close()

            bitmapCanvas.drawPath(pathBuffer, renderPaint)
        }

        MPPointF.recycleInstance(center)
    }

    /**
     * Draws a circle in the slice color at the outer end of every non-empty slice, rounding the slice off. Not
     * part of the default draw pass; call it from a subclass when needed.
     */
    protected open fun drawRoundedSlices(c: Canvas) {
        if (!chart.isDrawRoundedSlicesEnabled) return

        val bitmapCanvas = bitmapCanvas ?: return
        val dataSet = chart.data?.dataSet ?: return

        if (!dataSet.isVisible) return

        val phaseX = animator.phaseX
        val phaseY = animator.phaseY

        val center = chart.centerCircleBox
        val r = chart.radius

        val circleRadius = (r - (r * chart.holeRadius / 100f)) / 2f

        val drawAngles = chart.drawAngles
        var angle = chart.rotationAngle

        for (j in 0 until min(dataSet.entryCount, drawAngles.size)) {
            val sliceAngle = drawAngles[j]

            val e = dataSet.getEntryForIndex(j)

            if (abs(e.y) > Utils.FLOAT_EPSILON) {
                val x = ((r - circleRadius) * cos(Math.toRadians(((angle + sliceAngle) * phaseY).toDouble())) + center.x).toFloat()
                val y = ((r - circleRadius) * sin(Math.toRadians(((angle + sliceAngle) * phaseY).toDouble())) + center.y).toFloat()

                renderPaint.color = dataSet.getColor(j)
                bitmapCanvas.drawCircle(x, y, circleRadius, renderPaint)
            }

            angle += sliceAngle * phaseX
        }
        MPPointF.recycleInstance(center)
    }

    /**
     * Frees the offscreen bitmap and its canvas. The next [drawData] creates a new one.
     */
    public fun releaseBitmap() {
        bitmapCanvas?.setBitmap(null)
        bitmapCanvas = null

        drawBitmap?.let { reference ->
            reference.get()?.recycle()
            reference.clear()
        }
        drawBitmap = null
    }
}
