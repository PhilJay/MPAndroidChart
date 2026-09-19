package com.github.mikephil.charting.charts

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.Keep
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.HorizontalBarHighlighter
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer
import com.github.mikephil.charting.renderer.XAxisRendererHorizontalBarChart
import com.github.mikephil.charting.renderer.YAxisRendererHorizontalBarChart
import com.github.mikephil.charting.utils.HorizontalViewPortHandler
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.TransformerHorizontalBarChart
import com.github.mikephil.charting.utils.Utils
import kotlin.math.max
import kotlin.math.min

/**
 * [BarChart] with horizontal bars. The axes swap roles on screen: the [xAxis] runs vertically along
 * the bars and the two y axes run horizontally along the values. The visible range setters and
 * touch helpers take that swap into account, so callers keep using x for entries and y for values.
 */
@Keep
open class HorizontalBarChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BarChart(context, attrs, defStyle) {

    override fun init() {
        viewPortHandler = HorizontalViewPortHandler()

        super.init()

        leftAxisTransformer = TransformerHorizontalBarChart(viewPortHandler)
        rightAxisTransformer = TransformerHorizontalBarChart(viewPortHandler)

        renderer = HorizontalBarChartRenderer(this, animator, viewPortHandler)
        highlighter = HorizontalBarHighlighter(this)

        rendererLeftYAxis = YAxisRendererHorizontalBarChart(viewPortHandler, axisLeft, leftAxisTransformer)
        rendererRightYAxis = YAxisRendererHorizontalBarChart(viewPortHandler, axisRight, rightAxisTransformer)
        rendererXAxis = XAxisRendererHorizontalBarChart(viewPortHandler, xAxis, leftAxisTransformer, this)
    }

    private val offsetsBuffer = RectF()

    override fun calculateLegendOffsets(offsets: RectF) {
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
                        if (axisLeft.isEnabled && axisLeft.isDrawLabelsEnabled) {
                            offsets.top += axisLeft.getRequiredHeightSpace(rendererLeftYAxis.paintAxisLabels)
                        }
                    }
                    Legend.LegendVerticalAlignment.BOTTOM -> {
                        offsets.bottom += min(legend.neededHeight, viewPortHandler.chartHeight * legend.maxSizePercent) + Utils.convertDpToPixel(legend.yOffset)
                        if (axisRight.isEnabled && axisRight.isDrawLabelsEnabled) {
                            offsets.bottom += axisRight.getRequiredHeightSpace(rendererRightYAxis.paintAxisLabels)
                        }
                    }
                    Legend.LegendVerticalAlignment.CENTER -> {}
                }
            }
        }
    }

    override fun calculateOffsets() {
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
            offsetTop += axisLeft.getRequiredHeightSpace(rendererLeftYAxis.paintAxisLabels)
        }

        if (axisRight.needsOffset) {
            offsetBottom += axisRight.getRequiredHeightSpace(rendererRightYAxis.paintAxisLabels)
        }

        val xlabelwidth = xAxis.labelRotatedWidth.toFloat()

        if (xAxis.isEnabled) {
            when (xAxis.position) {
                XAxisPosition.BOTTOM -> offsetLeft += xlabelwidth
                XAxisPosition.TOP -> offsetRight += xlabelwidth
                XAxisPosition.BOTH_SIDED -> {
                    offsetLeft += xlabelwidth
                    offsetRight += xlabelwidth
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

        prepareOffsetMatrix()
        prepareValuePxMatrix()
    }

    override fun prepareValuePxMatrix() {
        rightAxisTransformer.prepareMatrixValuePx(axisRight.axisMinimum, axisRight.axisRange, xAxis.axisRange, xAxis.axisMinimum)
        leftAxisTransformer.prepareMatrixValuePx(axisLeft.axisMinimum, axisLeft.axisRange, xAxis.axisRange, xAxis.axisMinimum)
    }

    override fun getMarkerPosition(high: Highlight): FloatArray = floatArrayOf(high.drawY, high.drawX)

    override fun getBarBounds(e: BarEntry<*>, outputRect: RectF) {
        val data = data
        val set = data?.getDataSetForEntry(e)

        if (data == null || set == null) {
            outputRect.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE)
            return
        }

        val y = e.y
        val x = e.x

        val barWidth = data.barWidth

        val top = x - barWidth / 2f
        val bottom = x + barWidth / 2f
        val left = if (y >= 0) y else 0f
        val right = if (y <= 0) y else 0f

        outputRect.set(left, top, right, bottom)

        getTransformer(set.axisDependency).rectValueToPixel(outputRect)
    }

    private val positionBuffer = FloatArray(2)

    override fun getPosition(e: Entry<*>?, axis: AxisDependency): MPPointF? {
        if (e == null) return null

        val vals = positionBuffer
        vals[0] = e.y
        vals[1] = e.x

        getTransformer(axis).pointValuesToPixel(vals)

        return MPPointF(vals[0], vals[1])
    }

    override fun getHighlightByTouchPoint(x: Float, y: Float): Highlight? {
        if (data == null) {
            if (isLogEnabled) Log.e(LOG_TAG, "Can't select by touch. No data set.")
            return null
        }
        val h = highlighter.getHighlight(y, x)
        if (h == null || !isHighlightFullBarEnabled) return h

        return Highlight(h.x, h.y, h.xPx, h.yPx, h.dataSetIndex, -1, h.axis)
    }

    override val lowestVisibleX: Float
        get() {
            getTransformer(AxisDependency.LEFT).getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentBottom, posForGetLowestVisibleX)
            return max(xAxis.axisMinimum.toDouble(), posForGetLowestVisibleX.y).toFloat()
        }

    override val highestVisibleX: Float
        get() {
            getTransformer(AxisDependency.LEFT).getValuesByTouchPoint(viewPortHandler.contentLeft, viewPortHandler.contentTop, posForGetHighestVisibleX)
            return min(xAxis.axisMaximum.toDouble(), posForGetHighestVisibleX.y).toFloat()
        }

    override fun setVisibleXRangeMaximum(maxXRange: Float) {
        val xScale = xAxis.axisRange / maxXRange
        viewPortHandler.setMinimumScaleY(xScale)
    }

    override fun setVisibleXRangeMinimum(minXRange: Float) {
        val xScale = xAxis.axisRange / minXRange
        viewPortHandler.setMaximumScaleY(xScale)
    }

    override fun setVisibleXRange(minXRange: Float, maxXRange: Float) {
        val minScale = xAxis.axisRange / minXRange
        val maxScale = xAxis.axisRange / maxXRange
        viewPortHandler.setMinMaxScaleY(minScale, maxScale)
    }

    override fun setVisibleYRangeMaximum(maxYRange: Float, axis: AxisDependency) {
        val yScale = getAxisRange(axis) / maxYRange
        viewPortHandler.setMinimumScaleX(yScale)
    }

    override fun setVisibleYRangeMinimum(minYRange: Float, axis: AxisDependency) {
        val yScale = getAxisRange(axis) / minYRange
        viewPortHandler.setMaximumScaleX(yScale)
    }

    override fun setVisibleYRange(minYRange: Float, maxYRange: Float, axis: AxisDependency) {
        val minScale = getAxisRange(axis) / minYRange
        val maxScale = getAxisRange(axis) / maxYRange
        viewPortHandler.setMinMaxScaleX(minScale, maxScale)
    }
}
