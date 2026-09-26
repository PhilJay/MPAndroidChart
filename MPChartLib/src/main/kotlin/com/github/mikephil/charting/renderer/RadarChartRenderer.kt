package com.github.mikephil.charting.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.hypot
import kotlin.math.min

/**
 * Draws a [RadarChart]: the web in [drawExtras], one closed polygon per data set in [drawData] (filled when the
 * data set enables it), the values at the corners in [drawValues], and highlight lines and circles in
 * [drawHighlighted]. Angles are in degrees clockwise from 3 o'clock, offset by the chart's rotation angle.
 *
 * @property chart The radar chart, which supplies the data, the center, the slice angle and the web settings.
 */
public open class RadarChartRenderer(
    protected val chart: RadarChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : LineRadarRenderer(animator, viewPortHandler) {

    /**
     * Paint for the web lines; color, alpha and line width (dp) are copied from the chart on every draw.
     */
    public val webPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    /**
     * Paint for the fill and the stroke of the highlight circle.
     */
    protected val highlightCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val drawDataSetSurfacePathBuffer = Path()

    private val drawHighlightCirclePathBuffer = Path()

    /**
     * Does nothing; radar polygons are drawn without a buffer.
     */
    override fun initBuffers() {
    }

    override fun drawData(c: Canvas) {
        val radarData = chart.data ?: return
        val mostEntries = radarData.maxEntryCountSet?.entryCount ?: return

        for (set in radarData.dataSets) {
            if (set.isVisible) {
                drawDataSet(c, set, mostEntries)
            }
        }
    }


    /** Corners of the polygon being drawn, as x and y pairs, so a draw pass allocates nothing. */
    private var cornerBuffer = FloatArray(32)

    /** Appends one corner to [cornerBuffer] and returns the new corner count. */
    private fun addCorner(count: Int, x: Float, y: Float): Int {
        if ((count + 1) * 2 > cornerBuffer.size) {
            cornerBuffer = cornerBuffer.copyOf(cornerBuffer.size * 2)
        }
        cornerBuffer[count * 2] = x
        cornerBuffer[count * 2 + 1] = y
        return count + 1
    }

    /**
     * Builds the closed polygon of the first [count] corners in [cornerBuffer] into [path]. With a [radius] above
     * zero each corner is cut back along both of its edges and joined with a curve, clamped to half the shorter
     * edge so a small polygon keeps its shape.
     */
    private fun buildPolygon(path: Path, count: Int, radius: Float) {
        if (count == 0) return

        if (radius <= 0f || count < 3) {
            path.moveTo(cornerBuffer[0], cornerBuffer[1])
            for (i in 1 until count) {
                path.lineTo(cornerBuffer[i * 2], cornerBuffer[i * 2 + 1])
            }
            path.close()
            return
        }

        for (i in 0 until count) {
            val previous = (i + count - 1) % count
            val next = (i + 1) % count

            val x = cornerBuffer[i * 2]
            val y = cornerBuffer[i * 2 + 1]
            val previousX = cornerBuffer[previous * 2]
            val previousY = cornerBuffer[previous * 2 + 1]
            val nextX = cornerBuffer[next * 2]
            val nextY = cornerBuffer[next * 2 + 1]

            val toPrevious = hypot(previousX - x, previousY - y)
            val toNext = hypot(nextX - x, nextY - y)
            val cut = min(radius, min(toPrevious, toNext) / 2f)

            val startX = if (toPrevious == 0f) x else x + (previousX - x) / toPrevious * cut
            val startY = if (toPrevious == 0f) y else y + (previousY - y) / toPrevious * cut
            val endX = if (toNext == 0f) x else x + (nextX - x) / toNext * cut
            val endY = if (toNext == 0f) y else y + (nextY - y) / toNext * cut

            if (i == 0) path.moveTo(startX, startY) else path.lineTo(startX, startY)
            path.quadTo(x, y, endX, endY)
        }

        path.close()
    }

    /**
     * Draws the polygon of [dataSet]: one corner per entry at the radius of its value and the angle of its index,
     * rounded by the data set's corner radius when it has one,
     * filled with the fill drawable or fill color when the data set enables it, and outlined with its line width
     * (dp) unless the fill is fully opaque.
     *
     * @param mostEntries entry count of the largest data set; a data set with more entries is closed through the
     * center
     */
    protected open fun drawDataSet(c: Canvas, dataSet: IRadarDataSet<*>, mostEntries: Int) {
        val phaseX = animator.phaseX
        val phaseY = animator.phaseY

        val sliceangle = chart.sliceAngle
        val factor = chart.factor

        val center = chart.centerOffsets
        val pOut = MPPointF.getInstance(0f, 0f)
        val surface = drawDataSetSurfacePathBuffer
        surface.reset()

        renderPaint.color = dataSet.color

        var count = 0

        for (j in 0 until dataSet.entryCount) {
            val e = dataSet.getEntryForIndex(j)

            Utils.getPosition(
                center,
                (e.y - chart.yChartMin) * factor * phaseY,
                sliceangle * j * phaseX + chart.rotationAngle,
                pOut
            )

            if (pOut.x.isNaN()) continue

            count = addCorner(count, pOut.x, pOut.y)
        }

        if (dataSet.entryCount > mostEntries) {
            count = addCorner(count, center.x, center.y)
        }

        buildPolygon(surface, count, Utils.convertDpToPixel(dataSet.cornerRadius))

        if (dataSet.isDrawFilledEnabled) {
            val drawable = dataSet.fillDrawable
            if (drawable != null) {
                drawFilledPath(c, surface, drawable)
            } else {
                drawFilledPath(c, surface, dataSet.fillColor, dataSet.fillAlpha)
            }
        }

        renderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.lineWidth)
        renderPaint.style = Paint.Style.STROKE

        if (!dataSet.isDrawFilledEnabled || dataSet.fillAlpha < 255) {
            c.drawPath(surface, renderPaint)
        }

        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(pOut)
    }

    /**
     * Draws the y value and icon of each entry just above its corner of the polygon.
     */
    override fun drawValues(c: Canvas) {
        val radarData = chart.data ?: return

        val phaseX = animator.phaseX
        val phaseY = animator.phaseY

        val sliceangle = chart.sliceAngle
        val factor = chart.factor

        val center = chart.centerOffsets
        val pOut = MPPointF.getInstance(0f, 0f)
        val pIcon = MPPointF.getInstance(0f, 0f)

        val yoffset = Utils.convertDpToPixel(5f)

        for (i in 0 until radarData.dataSetCount) {
            val dataSet = radarData.getDataSetByIndex(i) ?: continue

            if (!shouldDrawValues(dataSet)) continue

            applyValueTextStyle(dataSet)

            val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

            for (j in 0 until dataSet.entryCount) {
                val entry = dataSet.getEntryForIndex(j)

                Utils.getPosition(
                    center,
                    (entry.y - chart.yChartMin) * factor * phaseY,
                    sliceangle * j * phaseX + chart.rotationAngle,
                    pOut
                )

                if (dataSet.isDrawValuesEnabled) {
                    drawValue(
                        c,
                        dataSet.valueFormatter,
                        entry.y,
                        entry,
                        i,
                        pOut.x,
                        pOut.y - yoffset,
                        dataSet.getValueTextColor(j)
                    )
                }

                val icon = entry.icon
                if (icon != null && dataSet.isDrawIconsEnabled) {
                    Utils.getPosition(
                        center,
                        entry.y * factor * phaseY + iconsOffset.y,
                        sliceangle * j * phaseX + chart.rotationAngle,
                        pIcon
                    )

                    pIcon.y += iconsOffset.x

                    Utils.drawImage(
                        c,
                        icon,
                        pIcon.x.toInt(),
                        pIcon.y.toInt(),
                        icon.intrinsicWidth,
                        icon.intrinsicHeight
                    )
                }
            }

            MPPointF.recycleInstance(iconsOffset)
        }

        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(pOut)
        MPPointF.recycleInstance(pIcon)
    }

    /**
     * Draws the web, see [drawWeb].
     */
    override fun drawExtras(c: Canvas) {
        drawWeb(c)
    }

    /**
     * Draws the web: one spoke from the center per entry index (skipping spokes according to the chart's skip
     * count) and one ring per y axis entry, with the outer and inner web line widths (dp) of the chart.
     */
    protected open fun drawWeb(c: Canvas) {
        val radarData = chart.data ?: return

        val sliceangle = chart.sliceAngle
        val factor = chart.factor
        val rotationangle = chart.rotationAngle

        val center = chart.centerOffsets

        webPaint.strokeWidth = Utils.convertDpToPixel(chart.webLineWidth)
        webPaint.color = chart.webColor
        webPaint.alpha = chart.webAlpha

        val xIncrements = 1 + chart.skipWebLineCount
        val maxEntryCount = radarData.maxEntryCountSet?.entryCount ?: 0

        val p = MPPointF.getInstance(0f, 0f)
        var i = 0
        while (i < maxEntryCount) {
            Utils.getPosition(
                center,
                chart.yRange * factor,
                sliceangle * i + rotationangle,
                p
            )

            c.drawLine(center.x, center.y, p.x, p.y, webPaint)
            i += xIncrements
        }
        MPPointF.recycleInstance(p)

        webPaint.strokeWidth = Utils.convertDpToPixel(chart.webLineWidthInner)
        webPaint.color = chart.webColorInner
        webPaint.alpha = chart.webAlpha

        val yAxis = chart.yAxis
        val labelCount = yAxis.entryCount

        val p1out = MPPointF.getInstance(0f, 0f)
        val p2out = MPPointF.getInstance(0f, 0f)
        for (j in 0 until labelCount) {
            for (k in 0 until radarData.entryCount) {
                val r = (yAxis.entries[j] - chart.yChartMin) * factor

                Utils.getPosition(center, r, sliceangle * k + rotationangle, p1out)
                Utils.getPosition(center, r, sliceangle * (k + 1) + rotationangle, p2out)

                c.drawLine(p1out.x, p1out.y, p2out.x, p2out.y, webPaint)
            }
        }
        MPPointF.recycleInstance(p1out)
        MPPointF.recycleInstance(p2out)
    }

    /**
     * Draws the highlight lines through each highlighted corner and, when the data set enables it, a highlight
     * circle around it. The entry is picked by the highlight's x value as an index.
     */
    override fun drawHighlighted(c: Canvas, indices: List<Highlight>) {
        val radarData = chart.data ?: return

        val sliceangle = chart.sliceAngle
        val factor = chart.factor

        val center = chart.centerOffsets
        val pOut = MPPointF.getInstance(0f, 0f)

        for (high in indices) {
            val set = radarData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled) continue

            val index = high.x.toInt()
            if (index !in 0 until set.entryCount) continue

            val e = set.getEntryForIndex(index)

            if (!isInBoundsX(e, set)) continue

            val y = e.y - chart.yChartMin

            Utils.getPosition(
                center,
                y * factor * animator.phaseY,
                sliceangle * high.x * animator.phaseX + chart.rotationAngle,
                pOut
            )

            high.setDraw(pOut.x, pOut.y)

            drawHighlightLines(c, pOut.x, pOut.y, set)

            if (set.isDrawHighlightCircleEnabled) {
                if (!pOut.x.isNaN() && !pOut.y.isNaN()) {
                    var strokeColor = set.highlightCircleStrokeColor
                    if (strokeColor == ColorTemplate.COLOR_NONE) {
                        strokeColor = set.getColor(0)
                    }

                    if (set.highlightCircleStrokeAlpha < 255) {
                        strokeColor = ColorTemplate.colorWithAlpha(strokeColor, set.highlightCircleStrokeAlpha)
                    }

                    drawHighlightCircle(
                        c,
                        pOut,
                        set.highlightCircleInnerRadius,
                        set.highlightCircleOuterRadius,
                        set.highlightCircleFillColor,
                        strokeColor,
                        set.highlightCircleStrokeWidth
                    )
                }
            }
        }

        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(pOut)
    }

    /**
     * Draws a highlight ring around [point] (pixels).
     *
     * @param innerRadius inner radius of the fill in dp; 0 fills a full disc
     * @param outerRadius outer radius in dp
     * @param fillColor fill color, or `ColorTemplate.COLOR_NONE` for no fill
     * @param strokeColor stroke color, or `ColorTemplate.COLOR_NONE` for no stroke
     * @param strokeWidth stroke width in dp
     */
    public open fun drawHighlightCircle(
        c: Canvas,
        point: MPPointF,
        innerRadius: Float,
        outerRadius: Float,
        fillColor: Int,
        strokeColor: Int,
        strokeWidth: Float
    ) {
        c.save()

        val outerRadiusPx = Utils.convertDpToPixel(outerRadius)
        val innerRadiusPx = Utils.convertDpToPixel(innerRadius)

        if (fillColor != ColorTemplate.COLOR_NONE) {
            val p = drawHighlightCirclePathBuffer
            p.reset()
            p.addCircle(point.x, point.y, outerRadiusPx, Path.Direction.CW)
            if (innerRadiusPx > 0f) {
                p.addCircle(point.x, point.y, innerRadiusPx, Path.Direction.CCW)
            }
            highlightCirclePaint.color = fillColor
            highlightCirclePaint.style = Paint.Style.FILL
            c.drawPath(p, highlightCirclePaint)
        }

        if (strokeColor != ColorTemplate.COLOR_NONE) {
            highlightCirclePaint.color = strokeColor
            highlightCirclePaint.style = Paint.Style.STROKE
            highlightCirclePaint.strokeWidth = Utils.convertDpToPixel(strokeWidth)
            c.drawCircle(point.x, point.y, outerRadiusPx, highlightCirclePaint)
        }

        c.restore()
    }
}
