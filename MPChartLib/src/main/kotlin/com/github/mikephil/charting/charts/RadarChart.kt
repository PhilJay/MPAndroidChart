package com.github.mikephil.charting.charts

import android.graphics.Path
import android.graphics.RectF
import android.graphics.Paint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.highlight.RadarHighlighter
import com.github.mikephil.charting.renderer.RadarChartRenderer
import com.github.mikephil.charting.renderer.XAxisRendererRadarChart
import com.github.mikephil.charting.renderer.YAxisRendererRadarChart
import com.github.mikephil.charting.utils.Utils
import kotlin.math.max
import kotlin.math.min

/**
 * Chart that draws [RadarData] as a spider web: each entry sits on its own spoke from the center, and
 * the entries of a data set are joined into a polygon. Works best with 5 to 10 entries per data set.
 * The [xAxis] labels the spokes and [yAxis] scales the distance from the center.
 */
public open class RadarChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : PieRadarChartBase<RadarData>(context, attrs, defStyle) {

    /** Width in dp of the web lines running from the center outwards. Default 1.5. */
    public var webLineWidth: Float = 1.5f

    /** Width in dp of the web rings between the spokes. Default 0.75. */
    public var webLineWidthInner: Float = 0.75f

    /** Color of the web lines running from the center outwards. */
    public var webColor: Int = Color.rgb(122, 122, 122)

    /** Color of the web rings between the spokes. */
    public var webColorInner: Int = Color.rgb(122, 122, 122)

    /** Opacity of all web lines from 0 (invisible) to 255 (opaque). Default 150. */
    public var webAlpha: Int = 150

    /** Whether the web is drawn at all. */
    public var isDrawWebEnabled: Boolean = true

    /**
     * Number of spokes and their labels left out between two drawn ones, for charts with many entries.
     * Negative values are raised to 0. Default 0.
     */
    public var skipWebLineCount: Int = 0
        set(value) {
            field = max(0, value)
        }

    /** The single y axis, scaling the distance from the center. */
    public lateinit var yAxis: YAxis
        private set

    /** Draws [yAxis]. */
    public lateinit var yAxisRenderer: YAxisRendererRadarChart

    /** Draws the [xAxis] labels around the web. */
    public lateinit var xAxisRenderer: XAxisRendererRadarChart

    override fun init() {
        super.init()

        yAxis = YAxis(YAxis.AxisDependency.LEFT)
        yAxis.labelXOffset = 10f

        renderer = RadarChartRenderer(this, animator, viewPortHandler)
        yAxisRenderer = YAxisRendererRadarChart(viewPortHandler, yAxis, this)
        xAxisRenderer = XAxisRendererRadarChart(viewPortHandler, xAxis, this)

        highlighter = RadarHighlighter(this)
    }

    override fun calcMinMax() {
        super.calcMinMax()

        val data = data ?: return

        yAxis.calculate(data.getYMin(YAxis.AxisDependency.LEFT), data.getYMax(YAxis.AxisDependency.LEFT))
        xAxis.calculate(0f, (data.maxEntryCountSet?.entryCount ?: 0).toFloat())
    }

    override fun notifyDataSetChanged() {
        val data = data ?: return

        data.notifyDataChanged()
        calcMinMax()
        renderer.initBuffers()

        computeAxes()

        legendRenderer.computeLegend(data)

        calculateOffsets()
        invalidate()
    }

    internal override fun computeAxes() {
        yAxisRenderer.computeAxis(yAxis.axisMinimum, yAxis.axisMaximum, yAxis.isInverted)
        xAxisRenderer.computeAxis(xAxis.axisMinimum, xAxis.axisMaximum, false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (showsEmptyState) return

        if (xAxis.isEnabled) xAxisRenderer.computeAxis(xAxis.axisMinimum, xAxis.axisMaximum, false)

        xAxisRenderer.renderAxisLabels(canvas)

        if (isDrawWebEnabled) renderer.drawExtras(canvas)

        if (yAxis.isEnabled && yAxis.isDrawLimitLinesBehindDataEnabled) yAxisRenderer.renderLimitLines(canvas)

        renderer.drawData(canvas)

        val highlighted = highlighted
        if (valuesToHighlight()) renderer.drawHighlighted(canvas, highlighted)

        if (yAxis.isEnabled && !yAxis.isDrawLimitLinesBehindDataEnabled) yAxisRenderer.renderLimitLines(canvas)

        yAxisRenderer.renderAxisLabels(canvas)

        renderer.drawValues(canvas)

        legendRenderer.renderLegend(canvas)

        drawDescription(canvas)

        drawMarkers(canvas)
    }

    /** Pixels per y value unit: the radius divided by the y axis range. */
    public val factor: Float
        get() {
            val content = viewPortHandler.contentRect
            return min(content.width() / 2f, content.height() / 2f) / yAxis.axisRange
        }

    /** Angle in degrees between two neighbouring spokes: 360 divided by the largest entry count. */
    public val sliceAngle: Float
        get() = 360f / (data?.maxEntryCountSet?.entryCount ?: 1).toFloat()

    /** Index of the entry whose spoke is nearest to [angle]. Never -1: the angle always maps to a spoke. */
    override fun getIndexForAngle(angle: Float): Int {
        val a = Utils.getNormalizedAngle(angle - rotationAngle)

        val sliceangle = sliceAngle

        val max = data?.maxEntryCountSet?.entryCount ?: 0

        var index = 0

        for (i in 0 until max) {
            val referenceAngle = sliceangle * (i + 1) - sliceangle / 2f

            if (referenceAngle > a) {
                index = i
                break
            }
        }

        return index
    }

    override val requiredLegendOffset: Float
        get() = legendRenderer.labelPaint.textSize * 2f

    override val requiredBaseOffset: Float
        get() = if (xAxis.isEnabled && xAxis.isDrawLabelsEnabled) xAxis.labelRotatedWidth.toFloat() else Utils.convertDpToPixel(10f)

    // A label is drawn half its width beyond the web, so above and below that is half a width plus its height.
    override val requiredVerticalBaseOffset: Float
        get() = if (xAxis.isEnabled && xAxis.isDrawLabelsEnabled) {
            xAxis.labelRotatedWidth / 2f + xAxis.labelRotatedHeight
        } else {
            Utils.convertDpToPixel(10f)
        }

    override val radius: Float
        get() {
            val content = viewPortHandler.contentRect
            return min(content.width() / 2f, content.height() / 2f)
        }

    override val yChartMax: Float
        get() = yAxis.axisMaximum

    override val yChartMin: Float
        get() = yAxis.axisMinimum

    /** Range of [yAxis] in values. */
    public val yRange: Float
        get() = yAxis.axisRange

    override fun drawNoDataIcon(canvas: Canvas, bounds: RectF, paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = bounds.height() / 12f
        paint.strokeJoin = Paint.Join.ROUND
        val outer = (bounds.height() - paint.strokeWidth) / 2f
        for (radius in floatArrayOf(outer, outer * 0.5f)) {
            val path = Path()
            for (i in 0..5) {
                val angle = Math.toRadians(60.0 * i - 90.0)
                val x = bounds.centerX() + radius * kotlin.math.cos(angle).toFloat()
                val y = bounds.centerY() + radius * kotlin.math.sin(angle).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            canvas.drawPath(path, paint)
        }
    }
}
