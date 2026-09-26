package com.github.mikephil.charting.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.ChartHighlighter
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet
import com.github.mikephil.charting.jobs.AnimatedMoveViewJob
import com.github.mikephil.charting.jobs.AnimatedZoomJob
import com.github.mikephil.charting.jobs.MoveViewJob
import com.github.mikephil.charting.jobs.ZoomJob
import com.github.mikephil.charting.listener.BarLineChartTouchListener
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.MPPointD
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Base class of the charts with an x axis and two y axes: [LineChart], [BarChart], [ScatterChart],
 * [CandleStickChart], [BubbleChart] and [CombinedChart]. It adds the axes, zooming, dragging and the
 * viewport functions, and implements [BarLineScatterCandleBubbleDataProvider] for the renderers.
 *
 * @param T The [BarLineScatterCandleBubbleData] type this chart displays, fixed by each subclass.
 */
public abstract class BarLineChartBase<T : BarLineScatterCandleBubbleData<out IBarLineScatterCandleBubbleDataSet<out Entry<*>>>> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Chart<T>(context, attrs, defStyle), BarLineScatterCandleBubbleDataProvider {

    /**
     * Value labels of a data set are only drawn while at most this many of its entries are inside the visible
     * x range, so zooming in brings them back. Default 100.
     */
    override var maxVisibleCount: Int = 100

    /**
     * Whether entries that share a pixel column may be dropped while drawing. Off by default, so a chart draws every
     * entry you gave it. Line circles that another circle would cover are always skipped, with or without this.
     *
     * Turn it on when a lot of entries are on screen at once and each one costs a draw call of its own: a bar
     * chart, a scatter chart, or a line with a color per segment. Drawing 50,000 bars at their full extent
     * takes 157 ms with this off and 61 ms with it on. It does not pay for a plain single color line, which is
     * already one call to the canvas, and there it costs about half as much again as it saves. The reduction
     * keeps the first, lowest, highest and last entry of every pixel column, so peaks survive.
     */
    override var isDecimationEnabled: Boolean = false

    /** Whether the y axes are recalculated on every draw from the entries currently in view. */
    public var isAutoScaleMinMaxEnabled: Boolean = false

    /** Whether a pinch scales both axes together. When false, x and y can be scaled separately. */
    public var isPinchZoomEnabled: Boolean = false

    /** Whether a double tap zooms in. */
    public var isDoubleTapToZoomEnabled: Boolean = true

    /** Whether dragging highlights values while the chart is fully zoomed out and cannot be panned. */
    public var isHighlightPerDragEnabled: Boolean = true

    /** Whether the chart can be dragged horizontally. */
    public var isDragXEnabled: Boolean = true

    /** Whether the chart can be dragged vertically. */
    public var isDragYEnabled: Boolean = true

    /** Whether the chart can be scaled horizontally by gesture. */
    public var isScaleXEnabled: Boolean = true

    /** Whether the chart can be scaled vertically by gesture. */
    public var isScaleYEnabled: Boolean = true

    /** Paint used for the grid background, filled light grey by default. */
    public lateinit var gridBackgroundPaint: Paint

    /** Paint used for the border around the content area. */
    protected lateinit var borderPaint: Paint

    /** Whether the content area is filled with [gridBackgroundPaint]. */
    public var isDrawGridBackgroundEnabled: Boolean = false

    /** Whether a border is drawn around the content area. With it on, the axis lines are redundant. */
    public var isDrawBordersEnabled: Boolean = false

    /** Whether value labels are clipped to the content area instead of bleeding outside it. */
    public var isClipValuesToContentEnabled: Boolean = false

    /**
     * Whether data and highlights are clipped to the content area. Turn it off when thick lines or
     * large shapes inside the content get cut at its edges.
     */
    public var isClipDataToContentEnabled: Boolean = true

    /** Smallest space in dp between the view edge and the content area on every side. Default 15. */
    public var minOffset: Float = 15f

    /** Whether the top left corner stays at the same values when the view is resized, such as on rotation. */
    public var isKeepPositionOnRotation: Boolean = false


    /** The left y axis. */
    public lateinit var axisLeft: YAxis
        protected set

    /** The right y axis. */
    public lateinit var axisRight: YAxis
        protected set

    /** Draws [axisLeft]. */
    public lateinit var rendererLeftYAxis: YAxisRenderer

    /** Draws [axisRight]. */
    public lateinit var rendererRightYAxis: YAxisRenderer

    /** Converts values that depend on [axisLeft] to pixels and back. */
    protected lateinit var leftAxisTransformer: Transformer

    /** Converts values that depend on [axisRight] to pixels and back. */
    protected lateinit var rightAxisTransformer: Transformer

    /** Draws the [xAxis]. */
    public lateinit var rendererXAxis: XAxisRenderer

    override fun init() {
        super.init()

        axisLeft = YAxis(AxisDependency.LEFT)
        axisRight = YAxis(AxisDependency.RIGHT)

        leftAxisTransformer = Transformer(viewPortHandler)
        rightAxisTransformer = Transformer(viewPortHandler)

        rendererLeftYAxis = YAxisRenderer(viewPortHandler, axisLeft, leftAxisTransformer)
        rendererRightYAxis = YAxisRenderer(viewPortHandler, axisRight, rightAxisTransformer)

        rendererXAxis = XAxisRenderer(viewPortHandler, xAxis, leftAxisTransformer)

        highlighter = ChartHighlighter(this)

        chartTouchListener = BarLineChartTouchListener(this, viewPortHandler.matrixTouch, 3f)

        gridBackgroundPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.rgb(240, 240, 240)
        }

        borderPaint = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth = Utils.convertDpToPixel(1f)
        }
    }

    private var totalTime = 0L
    private var drawCycles = 0L

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (showsEmptyState) return

        val starttime = System.currentTimeMillis()

        drawGridBackground(canvas)

        if (isAutoScaleMinMaxEnabled) autoScale()

        if (axisLeft.isEnabled) rendererLeftYAxis.computeAxis(axisLeft.axisMinimum, axisLeft.axisMaximum, axisLeft.isInverted)
        if (axisRight.isEnabled) rendererRightYAxis.computeAxis(axisRight.axisMinimum, axisRight.axisMaximum, axisRight.isInverted)
        if (xAxis.isEnabled) rendererXAxis.computeAxis(xAxis.axisMinimum, xAxis.axisMaximum, false)

        // The gutters were sized from the axis labels that the lines above have just replaced.
        if (isAutoScaleMinMaxEnabled) {
            calculateOffsets()
            viewPortHandler.refresh(viewPortHandler.matrixTouch, this, false)
        }

        rendererXAxis.renderAxisLine(canvas)
        rendererLeftYAxis.renderAxisLine(canvas)
        rendererRightYAxis.renderAxisLine(canvas)

        if (xAxis.isDrawGridLinesBehindDataEnabled) rendererXAxis.renderGridLines(canvas)
        if (axisLeft.isDrawGridLinesBehindDataEnabled) rendererLeftYAxis.renderGridLines(canvas)
        if (axisRight.isDrawGridLinesBehindDataEnabled) rendererRightYAxis.renderGridLines(canvas)

        if (xAxis.isEnabled && xAxis.isDrawLimitLinesBehindDataEnabled) rendererXAxis.renderLimitLines(canvas)
        if (axisLeft.isEnabled && axisLeft.isDrawLimitLinesBehindDataEnabled) rendererLeftYAxis.renderLimitLines(canvas)
        if (axisRight.isEnabled && axisRight.isDrawLimitLinesBehindDataEnabled) rendererRightYAxis.renderLimitLines(canvas)

        val clipRestoreCount = canvas.save()

        if (isClipDataToContentEnabled) canvas.clipRect(viewPortHandler.contentRect)

        renderer.drawData(canvas)

        if (!xAxis.isDrawGridLinesBehindDataEnabled) rendererXAxis.renderGridLines(canvas)
        if (!axisLeft.isDrawGridLinesBehindDataEnabled) rendererLeftYAxis.renderGridLines(canvas)
        if (!axisRight.isDrawGridLinesBehindDataEnabled) rendererRightYAxis.renderGridLines(canvas)

        val highlighted = highlighted
        if (valuesToHighlight()) renderer.drawHighlighted(canvas, highlighted)

        canvas.restoreToCount(clipRestoreCount)

        renderer.drawExtras(canvas)

        if (xAxis.isEnabled && !xAxis.isDrawLimitLinesBehindDataEnabled) rendererXAxis.renderLimitLines(canvas)
        if (axisLeft.isEnabled && !axisLeft.isDrawLimitLinesBehindDataEnabled) rendererLeftYAxis.renderLimitLines(canvas)
        if (axisRight.isEnabled && !axisRight.isDrawLimitLinesBehindDataEnabled) rendererRightYAxis.renderLimitLines(canvas)

        rendererXAxis.renderAxisLabels(canvas)
        rendererLeftYAxis.renderAxisLabels(canvas)
        rendererRightYAxis.renderAxisLabels(canvas)

        if (isClipValuesToContentEnabled) {
            val valuesClipRestoreCount = canvas.save()
            canvas.clipRect(viewPortHandler.contentRect)
            renderer.drawValues(canvas)
            canvas.restoreToCount(valuesClipRestoreCount)
        } else {
            renderer.drawValues(canvas)
        }

        legendRenderer.renderLegend(canvas)

        drawDescription(canvas)

        drawMarkers(canvas)

        if (isLogEnabled) {
            val drawtime = System.currentTimeMillis() - starttime
            totalTime += drawtime
            drawCycles += 1
            val average = totalTime / drawCycles
            Log.i(LOG_TAG, "Drawtime: $drawtime ms, average: $average ms, cycles: $drawCycles")
        }
    }

    /** Resets the draw time statistics written to logcat while [isLogEnabled] is true. */
    public fun resetTracking() {
        totalTime = 0
        drawCycles = 0
    }

    /** Refreshes the value to pixel matrices of both transformers from the current axis ranges. */
    protected open fun prepareValuePxMatrix() {
        if (isLogEnabled) {
            Log.i(LOG_TAG, "Preparing Value-Px Matrix, xmin: ${xAxis.axisMinimum}, xmax: ${xAxis.axisMaximum}, xdelta: ${xAxis.axisRange}")
        }

        rightAxisTransformer.prepareMatrixValuePx(xAxis.axisMinimum, xAxis.axisRange, axisRight.axisRange, axisRight.axisMinimum)
        leftAxisTransformer.prepareMatrixValuePx(xAxis.axisMinimum, xAxis.axisRange, axisLeft.axisRange, axisLeft.axisMinimum)
    }

    /** Refreshes the offset matrices of both transformers, honouring inverted axes. */
    protected fun prepareOffsetMatrix() {
        rightAxisTransformer.prepareMatrixOffset(axisRight.isInverted)
        leftAxisTransformer.prepareMatrixOffset(axisLeft.isInverted)
    }

    override fun notifyDataSetChanged() {
        val data = data
        if (data == null) {
            if (isLogEnabled) Log.i(LOG_TAG, "Preparing... DATA NOT SET.")
            return
        } else {
            if (isLogEnabled) Log.i(LOG_TAG, "Preparing...")
        }

        data.notifyDataChanged()

        if (::renderer.isInitialized) renderer.initBuffers()

        calcMinMax()

        computeAxes()

        legendRenderer.computeLegend(data)

        calculateOffsets()
        invalidate()
    }

    internal override fun computeAxes() {
        rendererLeftYAxis.computeAxis(axisLeft.axisMinimum, axisLeft.axisMaximum, axisLeft.isInverted)
        rendererRightYAxis.computeAxis(axisRight.axisMinimum, axisRight.axisMaximum, axisRight.isInverted)
        rendererXAxis.computeAxis(xAxis.axisMinimum, xAxis.axisMaximum, false)
    }

    /**
     * Recalculates the y axes from the entries between [lowestVisibleX] and [highestVisibleX], then recalculates
     * the offsets. Called on every draw while [isAutoScaleMinMaxEnabled] is on; call it yourself, followed by
     * [invalidate], when you want the same after changing data or the visible range.
     */
    public open fun autoScale() {
        val data = data ?: return

        val fromX = lowestVisibleX
        val toX = highestVisibleX

        data.calcMinMaxY(fromX, toX)

        xAxis.calculate(data.xMin, data.xMax)

        axisLeft.calculate(data.getYMin(AxisDependency.LEFT), data.getYMax(AxisDependency.LEFT))
        axisRight.calculate(data.getYMin(AxisDependency.RIGHT), data.getYMax(AxisDependency.RIGHT))

        calculateOffsets()
    }

    override fun calcMinMax() {
        val data = data ?: return

        xAxis.calculate(data.xMin, data.xMax)

        axisLeft.calculate(data.getYMin(AxisDependency.LEFT), data.getYMax(AxisDependency.LEFT))
        axisRight.calculate(data.getYMin(AxisDependency.RIGHT), data.getYMax(AxisDependency.RIGHT))
    }

    /** Writes the space in pixels the legend needs on each side into [offsets]. Zero when it is drawn inside. */
    protected open fun calculateLegendOffsets(offsets: RectF) {
        offsets.left = 0f
        offsets.right = 0f
        offsets.top = 0f
        offsets.bottom = 0f

        val legend = legend
        if (!legend.isEnabled || legend.isDrawInsideEnabled) return

        when (legend.orientation) {
            Legend.LegendOrientation.VERTICAL -> {
                when (legend.horizontalAlignment) {
                    Legend.LegendHorizontalAlignment.LEFT -> {
                        offsets.left += min(legend.neededWidth, viewPortHandler.chartWidth * legend.maxSizePercent) + Utils.convertDpToPixel(legend.xOffset)
                    }
                    Legend.LegendHorizontalAlignment.RIGHT -> {
                        offsets.right += min(legend.neededWidth, viewPortHandler.chartWidth * legend.maxSizePercent) + Utils.convertDpToPixel(legend.xOffset)
                    }
                    Legend.LegendHorizontalAlignment.CENTER -> {
                        when (legend.verticalAlignment) {
                            Legend.LegendVerticalAlignment.TOP -> {
                                offsets.top += min(legend.neededHeight, viewPortHandler.chartHeight * legend.maxSizePercent) + Utils.convertDpToPixel(legend.yOffset)
                            }
                            Legend.LegendVerticalAlignment.BOTTOM -> {
                                offsets.bottom += min(legend.neededHeight, viewPortHandler.chartHeight * legend.maxSizePercent) + Utils.convertDpToPixel(legend.yOffset)
                            }
                            Legend.LegendVerticalAlignment.CENTER -> {}
                        }
                    }
                }
            }
            Legend.LegendOrientation.HORIZONTAL -> {
                when (legend.verticalAlignment) {
                    Legend.LegendVerticalAlignment.TOP -> {
                        offsets.top += min(legend.neededHeight, viewPortHandler.chartHeight * legend.maxSizePercent) + Utils.convertDpToPixel(legend.yOffset)
                    }
                    Legend.LegendVerticalAlignment.BOTTOM -> {
                        offsets.bottom += min(legend.neededHeight, viewPortHandler.chartHeight * legend.maxSizePercent) + Utils.convertDpToPixel(legend.yOffset)
                    }
                    Legend.LegendVerticalAlignment.CENTER -> {}
                }
            }
        }
    }

    private val offsetsBuffer = RectF()

    /**
     * Computes the content rectangle from legend, axis labels, extra offsets and [minOffset], then
     * refreshes the transformers. The rectangle is left alone while [setViewPortOffsets] is active.
     */
    public override fun calculateOffsets() {
        if (!customViewPortEnabled) {
            var offsetLeft = 0f
            var offsetRight = 0f
            var offsetTop = 0f
            var offsetBottom = 0f

            calculateLegendOffsets(offsetsBuffer)

            offsetLeft += offsetsBuffer.left
            offsetTop += offsetsBuffer.top
            offsetRight += offsetsBuffer.right
            offsetBottom += offsetsBuffer.bottom

            if (axisLeft.needsOffset) {
                offsetLeft += axisLeft.getRequiredWidthSpace(rendererLeftYAxis.paintAxisLabels)
            }

            if (axisRight.needsOffset) {
                offsetRight += axisRight.getRequiredWidthSpace(rendererRightYAxis.paintAxisLabels)
            }

            if (xAxis.isEnabled && xAxis.isDrawLabelsEnabled) {
                val xLabelHeight = xAxis.labelRotatedHeight + Utils.convertDpToPixel(xAxis.yOffset)

                when (xAxis.position) {
                    XAxisPosition.BOTTOM -> offsetBottom += xLabelHeight
                    XAxisPosition.TOP -> offsetTop += xLabelHeight
                    XAxisPosition.BOTH_SIDED -> {
                        offsetBottom += xLabelHeight
                        offsetTop += xLabelHeight
                    }
                    else -> {}
                }
            }

            offsetTop += Utils.convertDpToPixel(extraTopOffset)
            offsetRight += Utils.convertDpToPixel(extraRightOffset)
            offsetBottom += Utils.convertDpToPixel(extraBottomOffset)
            offsetLeft += Utils.convertDpToPixel(extraLeftOffset)

            val minOffset = Utils.convertDpToPixel(minOffset)

            viewPortHandler.restrainViewPort(
                max(minOffset, offsetLeft),
                max(minOffset, offsetTop),
                max(minOffset, offsetRight),
                max(minOffset, offsetBottom)
            )

            if (isLogEnabled) {
                Log.i(LOG_TAG, "offsetLeft: $offsetLeft, offsetTop: $offsetTop, offsetRight: $offsetRight, offsetBottom: $offsetBottom")
                Log.i(LOG_TAG, "Content: ${viewPortHandler.contentRect}")
            }
        }

        prepareOffsetMatrix()
        prepareValuePxMatrix()
    }

    /** Fills the content area and draws its border when the matching flags are on. */
    protected fun drawGridBackground(c: Canvas) {
        if (isDrawGridBackgroundEnabled) {
            c.drawRect(viewPortHandler.contentRect, gridBackgroundPaint)
        }

        if (isDrawBordersEnabled) {
            c.drawRect(viewPortHandler.contentRect, borderPaint)
        }
    }

    override fun getTransformer(axis: AxisDependency): Transformer {
        return if (axis == AxisDependency.LEFT) leftAxisTransformer else rightAxisTransformer
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        if (!::chartTouchListener.isInitialized || data == null) return false

        if (!isTouchEnabled) return false

        return chartTouchListener.onTouch(this, event)
    }

    override fun computeScroll() {
        (chartTouchListener as? BarLineChartTouchListener)?.computeScroll()
    }

    /** Scratch matrix reused by the zoom functions. */
    protected val zoomMatrixBuffer: Matrix = Matrix()

    /** Zooms in by a factor of 1.4 around the content center right away, recalculates offsets and redraws. */
    public fun zoomIn() {
        val center = viewPortHandler.contentCenter

        viewPortHandler.zoomIn(center.x, -center.y, zoomMatrixBuffer)
        viewPortHandler.refresh(zoomMatrixBuffer, this, false)

        MPPointF.recycleInstance(center)

        calculateOffsets()
        postInvalidate()
    }

    /** Zooms out by a factor of 0.7 around the content center right away, recalculates offsets and redraws. */
    public fun zoomOut() {
        val center = viewPortHandler.contentCenter

        viewPortHandler.zoomOut(center.x, -center.y, zoomMatrixBuffer)
        viewPortHandler.refresh(zoomMatrixBuffer, this, false)

        MPPointF.recycleInstance(center)

        calculateOffsets()
        postInvalidate()
    }

    /**
     * Takes the zoom back to 1 around the content center, recalculates offsets and redraws. [fitScreen]
     * additionally resets the scale minima.
     */
    public fun resetZoom() {
        viewPortHandler.resetZoom(zoomMatrixBuffer)
        viewPortHandler.refresh(zoomMatrixBuffer, this, false)

        calculateOffsets()
        postInvalidate()
    }

    /**
     * Zooms by the given factors around a pixel position right away, recalculates offsets and redraws.
     *
     * @param scaleX Horizontal factor: above 1 zooms in, below 1 zooms out.
     * @param scaleY Vertical factor: above 1 zooms in, below 1 zooms out.
     * @param x Zoom center x in pixels.
     * @param y Zoom center y in pixels.
     */
    public fun zoom(scaleX: Float, scaleY: Float, x: Float, y: Float) {
        if (!allFinite(scaleX, scaleY, x, y)) return
        viewPortHandler.zoom(scaleX, scaleY, x, -y, zoomMatrixBuffer)
        viewPortHandler.refresh(zoomMatrixBuffer, this, false)

        calculateOffsets()
        postInvalidate()
    }

    /**
     * Zooms by the given factors around a position given in values, not pixels. Runs as a viewport job,
     * so it takes effect once the chart has a size (see [addViewportJob]).
     *
     * @param scaleX Horizontal factor: above 1 zooms in, below 1 zooms out.
     * @param scaleY Vertical factor: above 1 zooms in, below 1 zooms out.
     * @param xValue Zoom center on the x axis, in values.
     * @param yValue Zoom center on the y axis, in values of [axis].
     * @param axis The y axis [yValue] refers to.
     */
    public fun zoom(scaleX: Float, scaleY: Float, xValue: Float, yValue: Float, axis: AxisDependency) {
        if (!allFinite(scaleX, scaleY, xValue, yValue)) return
        val job = ZoomJob.getInstance(viewPortHandler, scaleX, scaleY, xValue, yValue, getTransformer(axis), axis, this)
        addViewportJob(job)
    }

    /**
     * Zooms by the given factors around the content center right away, recalculates offsets and redraws.
     *
     * @param scaleX Horizontal factor: above 1 zooms in, below 1 zooms out.
     * @param scaleY Vertical factor: above 1 zooms in, below 1 zooms out.
     */
    public fun zoomToCenter(scaleX: Float, scaleY: Float) {
        if (!allFinite(scaleX, scaleY)) return
        val center = centerOffsets

        val save = zoomMatrixBuffer
        viewPortHandler.zoom(scaleX, scaleY, center.x, -center.y, save)
        viewPortHandler.refresh(save, this, false)
        calculateOffsets()
        postInvalidate()
    }

    /**
     * Animates from the current zoom and position to the given zoom factors, centered on a position given
     * in values. Runs as a viewport job, so it starts once the chart has a size (see [addViewportJob]).
     *
     * @param scaleX Target horizontal zoom factor, 1 being fully zoomed out.
     * @param scaleY Target vertical zoom factor, 1 being fully zoomed out.
     * @param xValue Target center on the x axis, in values.
     * @param yValue Target center on the y axis, in values of [axis].
     * @param axis The y axis [yValue] refers to.
     * @param duration Animation duration in milliseconds.
     */
    public fun zoomAndCenterAnimated(scaleX: Float, scaleY: Float, xValue: Float, yValue: Float, axis: AxisDependency, duration: Long) {
        if (!allFinite(scaleX, scaleY, xValue, yValue)) return
        val origin = getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentTop, axis)

        val job = AnimatedZoomJob.getInstance(
            viewPortHandler, this, getTransformer(axis), getAxis(axis), xAxis.axisRange, scaleX, scaleY,
            viewPortHandler.scaleX, viewPortHandler.scaleY, xValue, yValue, origin.x.toFloat(), origin.y.toFloat(), duration
        )
        addViewportJob(job)

        MPPointD.recycleInstance(origin)
    }

    /** Scratch matrix reused by [fitScreen]. */
    protected val fitScreenMatrixBuffer: Matrix = Matrix()

    /** Undoes all zooming and dragging so the data fills the content area, then recalculates offsets and redraws. */
    public fun fitScreen() {
        val save = fitScreenMatrixBuffer
        viewPortHandler.fitScreen(save)
        viewPortHandler.refresh(save, this, false)

        calculateOffsets()
        postInvalidate()
    }

    /**
     * Sets how far the chart can be zoomed out. A factor of 1 means fully zoomed out; smaller values
     * are raised to 1. The current zoom is clamped at once.
     */
    public fun setScaleMinima(scaleX: Float, scaleY: Float) {
        viewPortHandler.setMinimumScaleX(scaleX)
        viewPortHandler.setMinimumScaleY(scaleY)
    }

    /**
     * Limits how much of the x axis can be visible at once, so the user cannot zoom out further.
     * Computed from the current x axis range, so call it after [data] is set. A range that is not above 0 does nothing.
     *
     * @param maxXRange Largest visible span in x values, for example 10 shows at most 10 units without scrolling.
     */
    public open fun setVisibleXRangeMaximum(maxXRange: Float) {
        val xScale = scaleForVisibleRange(xAxis.axisRange, maxXRange) ?: return
        viewPortHandler.setMinimumScaleX(xScale)
    }

    /**
     * Limits how little of the x axis can be visible at once, so the user cannot zoom in further.
     * Computed from the current x axis range, so call it after [data] is set. A range that is not above 0 does nothing.
     *
     * @param minXRange Smallest visible span in x values.
     */
    public open fun setVisibleXRangeMinimum(minXRange: Float) {
        val xScale = scaleForVisibleRange(xAxis.axisRange, minXRange) ?: return
        viewPortHandler.setMaximumScaleX(xScale)
    }

    /**
     * Limits the visible span of the x axis in both directions. Computed from the current x axis range,
     * so call it after [data] is set. A range that is not above 0 does nothing.
     *
     * @param minXRange Smallest visible span in x values.
     * @param maxXRange Largest visible span in x values.
     */
    public open fun setVisibleXRange(minXRange: Float, maxXRange: Float) {
        val minScale = scaleForVisibleRange(xAxis.axisRange, maxXRange) ?: return
        val maxScale = scaleForVisibleRange(xAxis.axisRange, minXRange) ?: return
        viewPortHandler.setMinMaxScaleX(minScale, maxScale)
    }

    /**
     * Limits how much of a y axis can be visible at once, so the user cannot zoom out further.
     * Computed from the current axis range, so call it after [data] is set. A range that is not above 0 does nothing.
     *
     * @param maxYRange Largest visible span in y values.
     * @param axis The y axis the span refers to.
     */
    public open fun setVisibleYRangeMaximum(maxYRange: Float, axis: AxisDependency) {
        val yScale = scaleForVisibleRange(getAxisRange(axis), maxYRange) ?: return
        viewPortHandler.setMinimumScaleY(yScale)
    }

    /**
     * Limits how little of a y axis can be visible at once, so the user cannot zoom in further.
     * Computed from the current axis range, so call it after [data] is set. A range that is not above 0 does nothing.
     *
     * @param minYRange Smallest visible span in y values.
     * @param axis The y axis the span refers to.
     */
    public open fun setVisibleYRangeMinimum(minYRange: Float, axis: AxisDependency) {
        val yScale = scaleForVisibleRange(getAxisRange(axis), minYRange) ?: return
        viewPortHandler.setMaximumScaleY(yScale)
    }

    /**
     * Limits the visible span of a y axis in both directions. Computed from the current axis range,
     * so call it after [data] is set. A range that is not above 0 does nothing.
     *
     * @param minYRange Smallest visible span in y values.
     * @param maxYRange Largest visible span in y values.
     * @param axis The y axis the spans refer to.
     */
    public open fun setVisibleYRange(minYRange: Float, maxYRange: Float, axis: AxisDependency) {
        val minScale = scaleForVisibleRange(getAxisRange(axis), maxYRange) ?: return
        val maxScale = scaleForVisibleRange(getAxisRange(axis), minYRange) ?: return
        viewPortHandler.setMinMaxScaleY(minScale, maxScale)
    }

    /** Zoom factor at which [visibleRange] of an axis spanning [axisRange] fills the content, or null when there is none. */
    internal fun scaleForVisibleRange(axisRange: Float, visibleRange: Float): Float? {
        if (!(visibleRange > 0f)) return null
        return (axisRange / visibleRange).takeIf { it.isFinite() }
    }

    /**
     * Scrolls so the left edge of the content area sits at the given x value, keeping the vertical
     * position within the viewport limits. Runs as a viewport job and redraws (see [addViewportJob]).
     */
    public fun moveViewToX(xValue: Float) {
        if (!allFinite(xValue)) return
        val job = MoveViewJob.getInstance(viewPortHandler, xValue, 0f, getTransformer(AxisDependency.LEFT), this)
        addViewportJob(job)
    }

    /**
     * Scrolls so the left edge sits at [xValue] and [yValue] is vertically centered. Runs as a viewport
     * job and redraws (see [addViewportJob]).
     *
     * @param xValue Value shown at the left edge of the content area.
     * @param yValue Value shown at the vertical center, in values of [axis].
     * @param axis The y axis [yValue] refers to.
     */
    public fun moveViewTo(xValue: Float, yValue: Float, axis: AxisDependency) {
        if (!allFinite(xValue, yValue)) return
        val yInView = getAxisRange(axis) / viewPortHandler.scaleY

        val job = MoveViewJob.getInstance(viewPortHandler, xValue, yValue + yInView / 2f, getTransformer(axis), this)
        addViewportJob(job)
    }

    /**
     * Like [moveViewTo], but animates from the current position.
     *
     * @param xValue Value shown at the left edge of the content area when the animation ends.
     * @param yValue Value shown at the vertical center when the animation ends, in values of [axis].
     * @param axis The y axis [yValue] refers to.
     * @param duration Animation duration in milliseconds.
     */
    public fun moveViewToAnimated(xValue: Float, yValue: Float, axis: AxisDependency, duration: Long) {
        if (!allFinite(xValue, yValue)) return
        val bounds = getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentTop, axis)

        val yInView = getAxisRange(axis) / viewPortHandler.scaleY

        val job = AnimatedMoveViewJob.getInstance(viewPortHandler, xValue, yValue + yInView / 2f, getTransformer(axis), this, bounds.x.toFloat(), bounds.y.toFloat(), duration)
        addViewportJob(job)

        MPPointD.recycleInstance(bounds)
    }

    /**
     * Scrolls so [yValue] is vertically centered. The left edge is moved to x value 0 at the same time,
     * within the viewport limits. Runs as a viewport job and redraws (see [addViewportJob]).
     *
     * @param yValue Value shown at the vertical center, in values of [axis].
     * @param axis The y axis [yValue] refers to.
     */
    public fun centerViewToY(yValue: Float, axis: AxisDependency) {
        if (!allFinite(yValue)) return
        val valsInView = getAxisRange(axis) / viewPortHandler.scaleY

        val job = MoveViewJob.getInstance(viewPortHandler, 0f, yValue + valsInView / 2f, getTransformer(axis), this)
        addViewportJob(job)
    }

    /**
     * Scrolls so the given position is at the center of the content area. Runs as a viewport job and
     * redraws (see [addViewportJob]).
     *
     * @param xValue Value shown at the horizontal center.
     * @param yValue Value shown at the vertical center, in values of [axis].
     * @param axis The y axis [yValue] refers to.
     */
    public fun centerViewTo(xValue: Float, yValue: Float, axis: AxisDependency) {
        if (!allFinite(xValue, yValue)) return
        val yInView = getAxisRange(axis) / viewPortHandler.scaleY
        val xInView = xAxis.axisRange / viewPortHandler.scaleX

        val job = MoveViewJob.getInstance(viewPortHandler, xValue - xInView / 2f, yValue + yInView / 2f, getTransformer(axis), this)
        addViewportJob(job)
    }

    /**
     * Like [centerViewTo], but animates from the current position.
     *
     * @param xValue Value shown at the horizontal center when the animation ends.
     * @param yValue Value shown at the vertical center when the animation ends, in values of [axis].
     * @param axis The y axis [yValue] refers to.
     * @param duration Animation duration in milliseconds.
     */
    public fun centerViewToAnimated(xValue: Float, yValue: Float, axis: AxisDependency, duration: Long) {
        if (!allFinite(xValue, yValue)) return
        val bounds = getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentTop, axis)

        val yInView = getAxisRange(axis) / viewPortHandler.scaleY
        val xInView = xAxis.axisRange / viewPortHandler.scaleX

        val job = AnimatedMoveViewJob.getInstance(viewPortHandler, xValue - xInView / 2f, yValue + yInView / 2f, getTransformer(axis), this, bounds.x.toFloat(), bounds.y.toFloat(), duration)
        addViewportJob(job)

        MPPointD.recycleInstance(bounds)
    }

    private fun allFinite(vararg values: Float) = values.all { it.isFinite() }

    private var customViewPortEnabled = false

    /**
     * Replaces the automatic offsets with fixed ones and applies them on the next UI loop pass.
     * Legend and axis labels are no longer accounted for; prefer [setExtraOffsets] unless you need this.
     * [resetViewPortOffsets] restores automatic offsets.
     *
     * @param left Space in pixels between the left view edge and the content area.
     * @param top Space in pixels between the top view edge and the content area.
     * @param right Space in pixels between the right view edge and the content area.
     * @param bottom Space in pixels between the bottom view edge and the content area.
     */
    public fun setViewPortOffsets(left: Float, top: Float, right: Float, bottom: Float) {
        customViewPortEnabled = true
        post {
            viewPortHandler.restrainViewPort(left, top, right, bottom)
            prepareOffsetMatrix()
            prepareValuePxMatrix()
        }
    }

    /** Removes offsets set with [setViewPortOffsets] and recalculates them automatically. */
    public fun resetViewPortOffsets() {
        customViewPortEnabled = false
        calculateOffsets()
    }

    /** Range of the given y axis in values. */
    protected fun getAxisRange(axis: AxisDependency): Float {
        return if (axis == AxisDependency.LEFT) axisLeft.axisRange else axisRight.axisRange
    }

    private val getPositionBuffer = FloatArray(2)

    /**
     * Pixel position of an entry inside the view.
     *
     * @param e The entry, or null.
     * @param axis The y axis the entry's data set depends on.
     * @return A new point in pixels, or null when [e] is null.
     */
    public open fun getPosition(e: Entry<*>?, axis: AxisDependency): MPPointF? {
        if (e == null) return null

        getPositionBuffer[0] = e.x
        getPositionBuffer[1] = e.y

        getTransformer(axis).pointValuesToPixel(getPositionBuffer)

        return MPPointF(getPositionBuffer[0], getPositionBuffer[1])
    }

    /** Fill color of the content area, used while [isDrawGridBackgroundEnabled] is true. */
    public var gridBackgroundColor: Int
        get() = gridBackgroundPaint.color
        set(value) {
            gridBackgroundPaint.color = value
        }

    /**
     * True when dragging is enabled on at least one axis. Setting it sets both [isDragXEnabled] and
     * [isDragYEnabled]. Dragging does not affect scaling.
     */
    public var isDragEnabled: Boolean
        get() = isDragXEnabled || isDragYEnabled
        set(value) {
            isDragXEnabled = value
            isDragYEnabled = value
        }

    /**
     * True when scaling by gesture is enabled on at least one axis. Setting it sets both [isScaleXEnabled]
     * and [isScaleYEnabled]. Scaling does not affect dragging.
     */
    public var isScaleEnabled: Boolean
        get() = isScaleXEnabled || isScaleYEnabled
        set(value) {
            isScaleXEnabled = value
            isScaleYEnabled = value
        }

    /** Width in dp of the border drawn while [isDrawBordersEnabled] is true. Default 1. */
    public var borderWidth: Float = 1f
        set(value) {
            field = value
            borderPaint.strokeWidth = Utils.convertDpToPixel(value)
        }

    /** Color of the border drawn while [isDrawBordersEnabled] is true. */
    public var borderColor: Int
        get() = borderPaint.color
        set(value) {
            borderPaint.color = value
        }

    /**
     * Converts a pixel position inside the view to values.
     *
     * @param x Pixel x position.
     * @param y Pixel y position.
     * @param axis The y axis the y value should refer to.
     * @return A new point holding the x value and the y value.
     */
    public fun getValuesByTouchPoint(x: Float, y: Float, axis: AxisDependency): MPPointD {
        val result = MPPointD(0.0, 0.0)
        getValuesByTouchPoint(x, y, axis, result)
        return result
    }

    /** Like [getValuesByTouchPoint], but writes the values into [outputPoint] instead of allocating. */
    public fun getValuesByTouchPoint(x: Float, y: Float, axis: AxisDependency, outputPoint: MPPointD) {
        getTransformer(axis).getValuesByTouchPoint(x, y, outputPoint)
    }

    /**
     * Converts values to a pixel position inside the view.
     *
     * @param x The x value.
     * @param y The y value on [axis].
     * @param axis The y axis [y] refers to.
     * @return The pixel position.
     */
    public fun getPixelForValues(x: Float, y: Float, axis: AxisDependency): MPPointD {
        return getTransformer(axis).getPixelForValues(x, y)
    }

    /**
     * Entry nearest to a pixel position, or null when there is no data or no entry within
     * [maxHighlightDistance].
     */
    public fun getEntryByTouchPoint(x: Float, y: Float): Entry<*>? {
        val h = getHighlightByTouchPoint(x, y) ?: return null
        return data?.getEntryForHighlight(h)
    }

    /**
     * Data set of the entry nearest to a pixel position, or null when there is no data or no entry
     * within [maxHighlightDistance].
     */
    public fun getDataSetByTouchPoint(x: Float, y: Float): IBarLineScatterCandleBubbleDataSet<out Entry<*>>? {
        val h = getHighlightByTouchPoint(x, y) ?: return null
        return data?.getDataSetByIndex(h.dataSetIndex)
    }

    /** Scratch point reused by [lowestVisibleX]. */
    protected val posForGetLowestVisibleX: MPPointD = MPPointD.getInstance(0.0, 0.0)

    override val lowestVisibleX: Float
        get() {
            getTransformer(AxisDependency.LEFT).getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentBottom, posForGetLowestVisibleX)
            return max(xAxis.axisMinimum.toDouble(), posForGetLowestVisibleX.x).toFloat()
        }

    /** Scratch point reused by [highestVisibleX]. */
    protected val posForGetHighestVisibleX: MPPointD = MPPointD.getInstance(0.0, 0.0)

    override val highestVisibleX: Float
        get() {
            getTransformer(AxisDependency.LEFT).getValuesByTouchPoint(viewPortHandler.contentRight, viewPortHandler.contentBottom, posForGetHighestVisibleX)
            return min(xAxis.axisMaximum.toDouble(), posForGetHighestVisibleX.x).toFloat()
        }

    /** Span of x values currently visible in the content area. */
    public val visibleXRange: Float
        get() = abs(highestVisibleX - lowestVisibleX)

    /** Current horizontal zoom factor of the viewport, 1 when fully zoomed out. `View.scaleX` is untouched. */
    public val zoomX: Float
        get() = viewPortHandler.scaleX

    /** Current vertical zoom factor of the viewport, 1 when fully zoomed out. `View.scaleY` is untouched. */
    public val zoomY: Float
        get() = viewPortHandler.scaleY

    /** True when both zoom factors are at their minimum. */
    public val isFullyZoomedOut: Boolean
        get() = viewPortHandler.isFullyZoomedOut

    /** The [YAxis] for the given side. */
    public fun getAxis(axis: AxisDependency): YAxis {
        return if (axis == AxisDependency.LEFT) axisLeft else axisRight
    }

    override fun isInverted(axis: AxisDependency): Boolean = getAxis(axis).isInverted

    /** Extra distance in dp the content may be dragged past its left and right bounds. Default 0. */
    public var dragOffsetX: Float
        get() = viewPortHandler.dragOffsetX
        set(value) {
            viewPortHandler.dragOffsetX = value
        }

    /** Extra distance in dp the content may be dragged past its top and bottom bounds. Default 0. */
    public var dragOffsetY: Float
        get() = viewPortHandler.dragOffsetY
        set(value) {
            viewPortHandler.dragOffsetY = value
        }

    /** True when neither [dragOffsetX] nor [dragOffsetY] is above 0. */
    public val hasNoDragOffset: Boolean
        get() = viewPortHandler.hasNoDragOffset

    /** Larger of the two y axis maximums. */
    override val yChartMax: Float
        get() = max(axisLeft.axisMaximum, axisRight.axisMaximum)

    /** Smaller of the two y axis minimums. */
    override val yChartMin: Float
        get() = min(axisLeft.axisMinimum, axisRight.axisMinimum)

    /** True when the left or the right y axis is inverted. */
    public val isAnyAxisInverted: Boolean
        get() = axisLeft.isInverted || axisRight.isInverted


    private val onSizeChangedBuffer = FloatArray(2)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        onSizeChangedBuffer[0] = 0f
        onSizeChangedBuffer[1] = 0f

        if (isKeepPositionOnRotation) {
            onSizeChangedBuffer[0] = viewPortHandler.contentLeft
            onSizeChangedBuffer[1] = viewPortHandler.contentTop
            getTransformer(AxisDependency.LEFT).pixelsToValue(onSizeChangedBuffer)
        }

        super.onSizeChanged(w, h, oldw, oldh)

        if (isKeepPositionOnRotation) {
            getTransformer(AxisDependency.LEFT).pointValuesToPixel(onSizeChangedBuffer)
            viewPortHandler.centerViewPort(onSizeChangedBuffer, this)
        } else {
            viewPortHandler.refresh(viewPortHandler.matrixTouch, this, true)
        }
    }
}
