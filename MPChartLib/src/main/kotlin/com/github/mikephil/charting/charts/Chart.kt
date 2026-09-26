package com.github.mikephil.charting.charts

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.animation.Easing.EasingFunction
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.components.MarkerImage
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.animation.ValueTransition
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.highlight.IHighlighter
import com.github.mikephil.charting.interfaces.dataprovider.ChartInterface
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.renderer.DataRenderer
import com.github.mikephil.charting.renderer.LegendRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.io.File
import java.io.IOException
import kotlin.math.abs
import kotlin.math.max

/**
 * Base class of every chart view. It owns the data, the axis, legend and description components,
 * the paints, highlighting, markers, animation and the viewport jobs that all chart types share.
 *
 * @param T The [ChartData] type this chart displays. Every concrete chart fixes it, for example
 * [LineChart] shows `LineData` and [PieChart] shows `PieData`.
 */
public abstract class Chart<T : ChartData<out IDataSet<out Entry<*>>>> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle), ChartInterface {

    /** Writes chart internals to logcat while true. Slows down drawing, so keep it off in production. */
    public var isLogEnabled: Boolean = false

    /**
     * The data this chart displays, or null when nothing has been set.
     *
     * Setting a non-null value sizes the default value formatter to the data's y range, assigns that
     * formatter to every data set that has none, and calls [notifyDataSetChanged], which recalculates
     * and redraws the chart. Setting null removes the data and redraws; [clear] does the same and also
     * clears highlights.
     *
     * Setting it ends running value animations from [animateValue] and [animateDataChange] first, so their entries
     * hold their target values.
     */
    override var data: T? = null
        set(value) {
            animator.endValueAnimations()
            field = value
            offsetsCalculated = false

            if (value == null) {
                invalidate()
                return
            }

            setupDefaultFormatter(value.yMin, value.yMax)

            for (set in value.dataSets) {
                if (set.needsFormatter || set.valueFormatter === defaultValueFormatterInternal) {
                    set.valueFormatter = defaultValueFormatterInternal
                }
            }

            notifyDataSetChanged()

            if (isLogEnabled) Log.i(LOG_TAG, "Data is set.")
        }

    /** Whether a tap highlights the nearest value. Values can still be highlighted by drag or in code. */
    public var isHighlightPerTapEnabled: Boolean = true

    /** Whether the chart keeps scrolling after the finger is lifted. */
    public var isDragDecelerationEnabled: Boolean = true

    /**
     * How slowly a deceleration scroll loses speed, between 0 (stops at once) and 0.999.
     * Values outside that range are clamped. Default 0.9.
     */
    public var dragDecelerationFrictionCoef: Float = 0.9f
        set(value) {
            field = value.coerceIn(0f, 0.999f)
        }

    /** Formatter used for data sets without their own. Its digit count follows the y range of the data. */
    protected val defaultValueFormatterInternal: DefaultValueFormatter = DefaultValueFormatter(0)

    override val defaultValueFormatter: IValueFormatter
        get() = defaultValueFormatterInternal

    /** Paint used for the description text. */
    public lateinit var descriptionPaint: Paint

    /** Paint used for the no-data text. */
    public lateinit var infoPaint: Paint

    /** The x axis component: its labels, range, grid lines and position. */
    public lateinit var xAxis: XAxis
        protected set

    /** Whether the chart reacts to touch at all. False disables tapping, dragging, scaling and rotating. */
    public var isTouchEnabled: Boolean = true

    /** The description text drawn in the bottom right corner by default. */
    public lateinit var description: Description

    /** The legend component. It is filled from the data sets unless a custom legend is set on it. */
    public lateinit var legend: Legend
        protected set

    /** Single listener told when a value is selected or the selection is cleared. */
    public var onChartValueSelectedListener: OnChartValueSelectedListener? = null

    /**
     * Additional listeners, notified after [onChartValueSelectedListener] in list order.
     * Meant for observers that must not replace the app's own listener, such as Compose state holders.
     */
    public val valueSelectedListeners: MutableList<OnChartValueSelectedListener> = mutableListOf()

    /** Additional gesture listeners, notified after [onChartGestureListener] in list order. */
    public val gestureListeners: MutableList<OnChartGestureListener> = mutableListOf()

    internal fun notifyGesture(block: (OnChartGestureListener) -> Unit) {
        onChartGestureListener?.let(block)
        for (listener in gestureListeners.toList()) block(listener)
    }

    /**
     * Replaces [onChartValueSelectedListener] with lambdas.
     *
     * @param onNothingSelected Called when the selection is cleared. Does nothing by default.
     * @param onValueSelected Called with the selected entry and its [Highlight].
     */
    public fun onValueSelected(onNothingSelected: () -> Unit = {}, onValueSelected: (entry: Entry<*>, highlight: Highlight) -> Unit) {
        onChartValueSelectedListener = object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry<*>, h: Highlight) = onValueSelected(e, h)
            override fun onNothingSelected() = onNothingSelected()
        }
    }

    /** Handles touch events for this chart. Replace it to customize gestures; [isTouchEnabled] must stay true. */
    public lateinit var chartTouchListener: ChartTouchListener<*>

    /**
     * Text of the empty state, drawn while the chart has no data or data without entries. An empty string draws
     * only the icon.
     */
    public var noDataText: String = "No data yet"
        set(value) {
            field = value
            invalidate()
        }

    /** Text of the empty state while [isLoading] is true. */
    public var loadingText: String = "Loading…"
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Whether the data is on its way. While true and the chart has nothing to draw, the empty state shows
     * [loadingText] and its icon pulses. Data that arrives is drawn as usual, so this can stay true until then.
     */
    public var isLoading: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    /** Whether the empty state draws an icon above its text: [noDataIcon], or a small outline of this chart type. */
    public var isNoDataIconEnabled: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    /** Icon drawn above the empty state text instead of the outline of this chart type, or null for the outline. */
    public var noDataIcon: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    /** Color of the chart type outline in the empty state. Default a faint slate that reads on light and dark backgrounds. */
    public var noDataIconColor: Int
        get() = noDataIconPaint.color
        set(value) {
            noDataIconPaint.color = value
            invalidate()
        }

    private val noDataIconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(84, 138, 148, 166) }
    private val noDataIconBounds = RectF()

    /** Single listener told about gestures such as tap, double tap, drag, scale and fling. */
    public var onChartGestureListener: OnChartGestureListener? = null

    /** Draws the [legend]. */
    public lateinit var legendRenderer: LegendRenderer
        protected set

    /** Paint used for legend labels. */
    public var legendLabelPaint: Paint
        get() = legendRenderer.labelPaint
        set(value) {
            legendRenderer.labelPaint = value
        }

    /** Draws the data. Each chart type installs its own renderer; replace it for custom drawing. */
    public lateinit var renderer: DataRenderer

    /** Turns a touch position into the [Highlight] of the nearest value. */
    public lateinit var highlighter: IHighlighter

    /** Holds the chart size, the content rectangle and the zoom and drag matrix. */
    public var viewPortHandler: ViewPortHandler = ViewPortHandler()
        protected set

    /** Runs the animations: the x and y phases of [animateX], [animateY] and [animateXY], and the value animations. */
    public lateinit var animator: ChartAnimator
        protected set

    /** Extra space in dp added above the content area on top of the calculated offsets. */
    public var extraTopOffset: Float = 0f

    /** Extra space in dp added right of the content area on top of the calculated offsets. */
    public var extraRightOffset: Float = 0f

    /** Extra space in dp added below the content area on top of the calculated offsets. */
    public var extraBottomOffset: Float = 0f

    /** Extra space in dp added left of the content area on top of the calculated offsets. */
    public var extraLeftOffset: Float = 0f

    private var offsetsCalculated = false

    /** The values currently highlighted. Empty, never null, when nothing is highlighted. */
    public var highlighted: List<Highlight> = emptyList()
        protected set

    /** Farthest distance in dp a touch may be from a value and still highlight it. Default 500. */
    override var maxHighlightDistance: Float = 500f

    /** Whether the [marker] is drawn at highlighted values. */
    public var isDrawMarkersEnabled: Boolean = true

    /**
     * Marker drawn at each highlighted value, or null for none.
     * A [MarkerView] or [MarkerImage] is attached to this chart when set.
     */
    public var marker: IMarker? = null
        set(value) {
            field = value
            (value as? MarkerView)?.chartView = this
            (value as? MarkerImage)?.chartView = this
        }

    /** Viewport jobs waiting for the chart to get a size. They run once in `onSizeChanged`. */
    public val jobs: MutableList<Runnable> = mutableListOf()

    /**
     * Whether background drawables and child views are released when the chart leaves the window. Leave it off for
     * a chart that is reattached later, such as a list row or a pager page, because it also removes the view a
     * Compose marker keeps as a child.
     */
    public var isUnbindEnabled: Boolean = false

    init {
        init()
    }

    /** Creates the components, paints and animator. Subclasses add their renderer, highlighter and touch listener. */
    protected open fun init() {
        setWillNotDraw(false)

        animator = ChartAnimator { postInvalidate() }

        Utils.init(context)

        description = Description()
        legend = Legend()
        legendRenderer = LegendRenderer(viewPortHandler, legend)
        xAxis = XAxis()

        descriptionPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        infoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(138, 148, 166)
            textAlign = Paint.Align.CENTER
            textSize = Utils.convertDpToPixel(13f)
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        }

        if (isLogEnabled) Log.i(LOG_TAG, "Chart.init()")
    }

    /** Sets [data] to null, clears the highlight and redraws, which shows [noDataText]. */
    public fun clear() {
        data = null
        offsetsCalculated = false
        highlighted = emptyList()
        chartTouchListener.lastHighlighted = null
        invalidate()
    }

    /** Removes all data sets from [data] but keeps the data object, then redraws. Does nothing without data. */
    public fun clearValues() {
        data?.clearValues()
        invalidate()
    }

    /** True when there is no data or the data holds no entries. */
    public val isEmpty: Boolean
        get() = (data?.entryCount ?: 0) <= 0

    /**
     * Refreshes the data, recalculates axis ranges, legend and offsets and redraws.
     * Call it after changing entries or data sets in place. Does nothing while [data] is null.
     */
    public abstract fun notifyDataSetChanged()

    /** Computes the content rectangle from the legend, axis labels and extra offsets. */
    protected abstract fun calculateOffsets()

    /** Computes the axis ranges from the data. */
    protected abstract fun calcMinMax()

    /**
     * Sets the digit count of the default value formatter from the value range.
     *
     * @param min Smallest y value of the data.
     * @param max Largest y value of the data.
     */
    protected fun setupDefaultFormatter(min: Float, max: Float) {
        val data = data
        val reference = if (data == null || data.entryCount < 2) {
            max(abs(min), abs(max))
        } else {
            abs(max - min)
        }

        val digits = Utils.getDecimals(reference)
        defaultValueFormatterInternal.setup(digits)
    }

    override fun onDraw(canvas: Canvas) {
        if (showsEmptyState) {
            drawEmptyState(canvas)
            return
        }

        if (!offsetsCalculated) {
            calculateOffsets()
            offsetsCalculated = true
        }
    }

    /** Draws the [description] text at its position, or in the bottom right corner when it has none. */
    protected fun drawDescription(c: Canvas) {
        val description = description
        if (!description.isEnabled) return

        val position = description.position

        descriptionPaint.typeface = description.typeface
        descriptionPaint.textSize = Utils.convertDpToPixel(description.textSize)
        descriptionPaint.color = description.textColor
        descriptionPaint.textAlign = description.textAlign

        val x: Float
        val y: Float

        if (position == null) {
            x = width - viewPortHandler.offsetRight - Utils.convertDpToPixel(description.xOffset)
            y = height - viewPortHandler.offsetBottom - Utils.convertDpToPixel(description.yOffset)
        } else {
            x = position.x
            y = position.y
        }

        c.drawText(description.text, x, y, descriptionPaint)
    }

    /** True when at least one value is highlighted. */
    public fun valuesToHighlight(): Boolean {
        return highlighted.isNotEmpty()
    }

    /** Tells the touch listener which highlight a tap must toggle off next. */
    protected fun setLastHighlighted(highs: List<Highlight>?) {
        chartTouchListener.lastHighlighted = highs?.firstOrNull()
    }

    /**
     * Highlights all the given values at once and redraws. The list is taken as is, without checking that
     * the entries exist, and no listener is called. An empty list clears the highlight.
     */
    public fun highlightValues(highs: List<Highlight>) {
        highlighted = highs
        setLastHighlighted(highs)
        invalidate()
    }

    /**
     * Highlights the entry closest to the given x value in one data set, whatever its y value, and redraws.
     *
     * @param x The x value to search for.
     * @param dataSetIndex Index of the data set to search. An index outside the data clears the highlight.
     * @param dataIndex For [CombinedChart] only: index of the data object inside the combined data.
     * @param stackIndex Index inside a stacked bar, or -1 for the whole bar.
     * @param callListener Whether the value selected listeners are notified.
     */
    public fun highlightValue(x: Float, dataSetIndex: Int, dataIndex: Int = -1, stackIndex: Int = -1, callListener: Boolean = true) {
        highlightValue(x, Float.NaN, dataSetIndex, dataIndex, stackIndex, callListener)
    }

    /**
     * Highlights the entry closest to the given x and y value in one data set and redraws.
     *
     * @param x The x value to search for.
     * @param y The y value to search for, or `Float.NaN` to accept any y value.
     * @param dataSetIndex Index of the data set to search. An index outside the data clears the highlight.
     * @param dataIndex For [CombinedChart] only: index of the data object inside the combined data.
     * @param stackIndex Index inside a stacked bar, or -1 for the whole bar.
     * @param callListener Whether the value selected listeners are notified.
     */
    public fun highlightValue(x: Float, y: Float, dataSetIndex: Int, dataIndex: Int = -1, stackIndex: Int = -1, callListener: Boolean = true) {
        val dataSetCount = data?.dataSetCount ?: 0
        if (dataSetIndex < 0 || dataSetIndex >= dataSetCount) {
            highlightValue(null, callListener)
        } else {
            highlightValue(Highlight(x, y, dataSetIndex, dataIndex).also { it.stackIndex = stackIndex }, callListener)
        }
    }

    /**
     * Highlights the value described by [highlight] and redraws. This is what a tap calls.
     * Null, or a highlight that matches no entry, clears the highlight instead.
     *
     * @param highlight Which data set and x value to highlight, or null to clear.
     * @param callListener Whether [onChartValueSelectedListener] and [valueSelectedListeners] are notified
     * with `onValueSelected` or, when nothing ends up highlighted, `onNothingSelected`.
     */
    public fun highlightValue(highlight: Highlight?, callListener: Boolean = true) {
        var high = highlight
        var e: Entry<*>? = null

        if (high == null) {
            highlighted = emptyList()
        } else {
            if (isLogEnabled) Log.i(LOG_TAG, "Highlighted: $high")

            e = data?.getEntryForHighlight(high)
            if (e == null) {
                highlighted = emptyList()
                high = null
            } else {
                highlighted = listOf(high)
            }
        }

        setLastHighlighted(highlighted)

        if (callListener) {
            val listeners = listOfNotNull(onChartValueSelectedListener) + valueSelectedListeners
            for (listener in listeners) {
                if (!valuesToHighlight() || e == null || high == null) {
                    listener.onNothingSelected()
                } else {
                    listener.onValueSelected(e, high)
                }
            }
        }

        invalidate()
    }

    /**
     * Finds the value nearest to a touch position.
     *
     * @param x Touch x position in pixels.
     * @param y Touch y position in pixels.
     * @return The [Highlight] of the nearest value, or null when there is no data or no value within
     * [maxHighlightDistance].
     */
    public open fun getHighlightByTouchPoint(x: Float, y: Float): Highlight? {
        if (data == null) {
            Log.e(LOG_TAG, "Can't select by touch. No data set.")
            return null
        }
        return highlighter.getHighlight(x, y)
    }

    /** Draws the [marker] at every highlighted value that lies inside the content area. */
    protected open fun drawMarkers(canvas: Canvas) {
        val marker = marker ?: return
        if (!isDrawMarkersEnabled || !valuesToHighlight()) return

        val data = data ?: return
        val highlighted = highlighted

        for (highlight in highlighted) {
            val set = data.getDataSetByIndex(highlight.dataSetIndex) ?: continue

            val e = data.getEntryForHighlight(highlight) ?: continue
            val entryIndex = set.getEntryIndex(e)

            if (entryIndex > set.entryCount * animator.phaseX) continue

            val pos = getMarkerPosition(highlight)

            if (!viewPortHandler.isInBounds(pos[0], pos[1])) continue

            marker.refreshContent(e, highlight)
            marker.draw(canvas, pos[0], pos[1])
        }
    }

    /** Pixel position where the marker for [high] is drawn, as `[x, y]`. */
    protected open fun getMarkerPosition(high: Highlight): FloatArray = floatArrayOf(high.drawX, high.drawY)

    /**
     * Animates the drawing of the data along both axes. The chart redraws itself while animating. Running x and y
     * animations are cancelled first.
     *
     * @param durationMillisX Duration of the x animation in milliseconds.
     * @param durationMillisY Duration of the y animation in milliseconds.
     * @param easingX Easing of the x animation.
     * @param easingY Easing of the y animation. Defaults to [easingX].
     * @param onEnd Called once after both animations have ended, see [animateX].
     */
    @JvmOverloads
    public fun animateXY(
        durationMillisX: Int,
        durationMillisY: Int,
        easingX: EasingFunction = Easing.Linear,
        easingY: EasingFunction = easingX,
        onEnd: () -> Unit = {}
    ) {
        animator.animateXY(durationMillisX, durationMillisY, easingX, easingY, onEnd)
    }

    /**
     * Animates the drawing of the data along the x axis. The chart redraws itself while animating. A running x
     * animation is cancelled first.
     *
     * @param durationMillis Duration in milliseconds.
     * @param easing Easing of the animation.
     * @param onEnd Called once when the animation ends: when it finishes, when [stopAnimations] or leaving the
     * window ends it, and when a new x animation cancels it.
     */
    @JvmOverloads
    public fun animateX(durationMillis: Int, easing: EasingFunction = Easing.Linear, onEnd: () -> Unit = {}) {
        animator.animateX(durationMillis, easing, onEnd)
    }

    /**
     * Animates the drawing of the data along the y axis. The chart redraws itself while animating. A running y
     * animation is cancelled first.
     *
     * @param durationMillis Duration in milliseconds.
     * @param easing Easing of the animation.
     * @param onEnd Called once when the animation ends, see [animateX].
     */
    @JvmOverloads
    public fun animateY(durationMillis: Int, easing: EasingFunction = Easing.Linear, onEnd: () -> Unit = {}) {
        animator.animateY(durationMillis, easing, onEnd)
    }

    /**
     * Animates the y value of one entry to [toY]. The axis ranges follow the value on every frame. Works for line,
     * bar, scatter, bubble, pie and radar entries. A running animation of the same entry is cancelled first and the
     * new one starts from the current value. Setting [data] ends the animation with the entry at [toY].
     *
     * @param entry An entry of the current [data].
     * @param toY The y value the entry ends with.
     * @param durationMillis Duration in milliseconds.
     * @param easing Easing of the animation.
     * @param onEnd Called once when the animation ends: when it finishes, when it is ended early, and when a new
     * animation of the same entry cancels it.
     * @throws IllegalArgumentException when [entry] is not part of [data], is a stacked [BarEntry] (animate a stack
     * with [animateDataChange]) or is a [CandleEntry], which does not draw its y.
     */
    public fun animateValue(entry: Entry<*>, toY: Float, durationMillis: Int, easing: EasingFunction = Easing.Linear, onEnd: () -> Unit = {}) {
        require(!(entry is BarEntry<*> && entry.isStacked)) { "animateValue does not animate stacked bars, use animateDataChange." }
        require(entry !is CandleEntry<*>) { "animateValue does not animate candle entries, use animateDataChange." }
        val dataSet = data?.dataSets?.firstOrNull { it.getEntryIndex(entry) >= 0 }
        requireNotNull(dataSet) { "The entry is not part of the chart data." }

        dataChange?.release(entry)
        val transition = ValueTransition.ofY(entry, toY, dataSet)
        animator.start(
            key = entry,
            from = 0f,
            to = 1f,
            durationMillis = durationMillis,
            easing = easing,
            onUpdate = { fraction ->
                transition.apply(fraction)
                refreshAfterValueChange(transition.dataSets)
            },
            onEnd = { cancelled ->
                // A cancelling animation of the same entry continues from the current value.
                if (!cancelled) {
                    transition.finish()
                    refreshAfterValueChange(transition.dataSets)
                }
                onEnd()
            }
        )
    }

    /**
     * Sets [newData] and animates every entry from the value it replaces to its own value. Entries are paired by
     * data set index and entry index; entries past the old end grow from 0, entries past the new end are gone at
     * once. Stacked bars animate each stack value, candles open, high, low and close, bubbles their size and pie
     * slices their value. The axis ranges follow on every frame and highlights stay on their x value.
     *
     * [newData] is set without animation, as if [data] were set, when the chart has no data, when [newData] is the
     * current data, or when the shapes differ: another data class, another number of data sets or another data set
     * class at the same index. A running data change or value animation ends first; the new one starts from the
     * values shown at that moment.
     *
     * The entries of [newData] are moved on every frame and hold exactly their own values again when the animation
     * ends, also when it is ended early by setting [data], by [stopAnimations] or by leaving the window.
     *
     * @param newData The data to show. Pass a new instance, not the current data changed in place.
     * @param durationMillis Duration in milliseconds.
     * @param easing Easing of the animation.
     * @param onEnd Called once when the animation ends, right away when nothing is animated.
     */
    public fun animateDataChange(newData: T, durationMillis: Int, easing: EasingFunction = Easing.Linear, onEnd: () -> Unit = {}) {
        val oldData = data
        val transition = if (oldData == null || oldData === newData) null else ValueTransition.between(oldData, newData)
        data = newData
        if (transition == null) {
            onEnd()
            return
        }

        dataChange = transition
        transition.apply(0f)
        refreshAfterValueChange(transition.dataSets)
        animator.start(
            key = ChartAnimator.DATA_CHANGE,
            from = 0f,
            to = 1f,
            durationMillis = durationMillis,
            easing = easing,
            onUpdate = { fraction ->
                transition.apply(fraction)
                refreshAfterValueChange(transition.dataSets)
            },
            onEnd = {
                if (dataChange === transition) dataChange = null
                transition.finish()
                refreshAfterValueChange(transition.dataSets)
                onEnd()
            }
        )
    }

    private var dataChange: ValueTransition? = null

    /** True while any animation runs: [animateX], [animateY], [animateXY], a spin, [animateValue] or [animateDataChange]. */
    public val isAnimating: Boolean
        get() = animator.isAnimating

    /**
     * Ends every running animation at once at its final state: the drawing is complete, a spin stands at its end
     * angle and animated values hold their targets. Each `onEnd` runs. Leaving the window does the same.
     */
    public fun stopAnimations() {
        animator.stop()
    }

    /**
     * Recomputes ranges, axes and offsets and redraws after the values of entries in [changedSets] changed in place.
     * Only the changed data sets are walked, and legend and buffers are kept. Highlights follow the values they
     * point at and are dropped when their entry is gone.
     */
    internal fun refreshAfterValueChange(changedSets: List<IDataSet<*>>) {
        val data = data ?: return
        for (set in changedSets) set.calcMinMax()
        data.notifyDataChanged()
        calcMinMax()
        computeAxes()
        calculateOffsets()
        followHighlights(data)
        invalidate()
    }

    /** Computes the axis labels from the axis ranges. Charts with axes override it. */
    internal open fun computeAxes() {}

    private fun followHighlights(data: T) {
        if (highlighted.isEmpty()) return
        highlighted = highlighted.mapNotNull { high ->
            val entry = data.getEntryForHighlight(high) ?: data.getEntryForHighlight(high.withY(Float.NaN)) ?: return@mapNotNull null
            if (high.y.isNaN() || high.y == entry.y) high else high.withY(entry.y)
        }
        setLastHighlighted(highlighted)
    }

    private fun Highlight.withY(y: Float): Highlight =
        Highlight(x, y, xPx, yPx, dataSetIndex, stackIndex, axis).also {
            it.dataIndex = dataIndex
            it.setDraw(drawX, drawY)
        }

    /** Largest y value across all data sets, or 0 without data. */
    public val yMax: Float
        get() = data?.yMax ?: 0f

    /** Smallest y value across all data sets, or 0 without data. */
    public val yMin: Float
        get() = data?.yMin ?: 0f

    override val xChartMax: Float
        get() = xAxis.axisMaximum

    override val xChartMin: Float
        get() = xAxis.axisMinimum

    override val xRange: Float
        get() = xAxis.axisRange

    /** Center of the whole view in pixels, ignoring offsets. See [centerOffsets] for the content center. */
    public val center: MPPointF
        get() = MPPointF(width / 2f, height / 2f)

    override val centerOffsets: MPPointF
        get() = viewPortHandler.contentCenter

    override val centerOfView: MPPointF
        get() = center

    override val contentRect: RectF
        get() = viewPortHandler.contentRect

    /** Sets all four extra offsets in dp at once. They apply on the next offset calculation. */
    public fun setExtraOffsets(left: Float, top: Float, right: Float, bottom: Float) {
        extraLeftOffset = left
        extraTopOffset = top
        extraRightOffset = right
        extraBottomOffset = bottom
    }

    /** Whether the chart has no data or no entries and draws its empty state instead of the data. */
    protected val showsEmptyState: Boolean
        get() = isEmpty

    /**
     * Draws the empty state: the icon and [noDataText], or [loadingText] while [isLoading], as one block centered
     * vertically and placed horizontally by [noDataTextAlignment]. Override it to draw an empty state of your own.
     */
    protected open fun drawEmptyState(canvas: Canvas) {
        val text = if (isLoading) loadingText else noDataText
        val iconHeight = if (isNoDataIconEnabled) Utils.convertDpToPixel(24f) else 0f
        val icon = noDataIcon
        val iconWidth = when {
            iconHeight == 0f -> 0f
            icon != null && icon.intrinsicHeight > 0 -> iconHeight * icon.intrinsicWidth / icon.intrinsicHeight
            else -> Utils.convertDpToPixel(30f)
        }
        val textHeight = if (text.isEmpty()) 0f else infoPaint.descent() - infoPaint.ascent()
        val gap = if (iconHeight > 0f && text.isNotEmpty()) Utils.convertDpToPixel(10f) else 0f
        if (iconHeight == 0f && textHeight == 0f) return

        val textWidth = if (text.isEmpty()) 0f else infoPaint.measureText(text)
        val layout = EmptyStateLayout(width.toFloat(), height.toFloat(), iconWidth, iconHeight, gap, textWidth, textHeight, infoPaint.textAlign)

        if (iconHeight > 0f) {
            val pulse = if (isLoading) loadingPulse(System.currentTimeMillis()) else 1f
            noDataIconBounds.set(layout.centerX - iconWidth / 2f, layout.top, layout.centerX + iconWidth / 2f, layout.top + iconHeight)
            if (icon != null) {
                icon.setBounds(noDataIconBounds.left.toInt(), noDataIconBounds.top.toInt(), noDataIconBounds.right.toInt(), noDataIconBounds.bottom.toInt())
                icon.alpha = (255 * pulse).toInt()
                icon.draw(canvas)
            } else {
                val color = noDataIconPaint.color
                noDataIconPaint.alpha = (Color.alpha(color) * pulse).toInt()
                drawNoDataIcon(canvas, noDataIconBounds, noDataIconPaint)
                noDataIconPaint.color = color
            }
            if (isLoading) postInvalidateOnAnimation()
        }

        if (text.isNotEmpty()) {
            canvas.drawText(text, layout.textX, layout.textTop - infoPaint.ascent(), infoPaint)
        }
    }

    /**
     * Draws the outline of this chart type that the empty state shows above its text, inside [bounds], with
     * [paint] in [noDataIconColor]. The default draws three bars; line, pie, radar, scatter and bubble charts draw
     * their own shape.
     */
    protected open fun drawNoDataIcon(canvas: Canvas, bounds: RectF, paint: Paint) {
        paint.style = Paint.Style.FILL
        val barWidth = bounds.width() / 4.5f
        val gap = (bounds.width() - 3 * barWidth) / 2f
        val heights = floatArrayOf(0.55f, 1f, 0.75f)
        val radius = barWidth / 3.5f
        for (i in 0..2) {
            val left = bounds.left + i * (barWidth + gap)
            canvas.drawRoundRect(left, bounds.bottom - bounds.height() * heights[i], left + barWidth, bounds.bottom, radius, radius, paint)
        }
    }

    /** Color of [noDataText] and [loadingText]. */
    public var noDataTextColor: Int
        get() = infoPaint.color
        set(value) {
            infoPaint.color = value
            invalidate()
        }

    /** Size of [noDataText] and [loadingText] in dp. Default 13. */
    public var noDataTextSize: Float
        get() = Utils.convertPixelsToDp(infoPaint.textSize)
        set(value) {
            infoPaint.textSize = Utils.convertDpToPixel(value)
            invalidate()
        }

    /** Typeface of [noDataText] and [loadingText], or null for the default. Default sans serif medium. */
    public var noDataTextTypeface: Typeface?
        get() = infoPaint.typeface
        set(value) {
            infoPaint.typeface = value
        }

    /** Horizontal alignment of the empty state: at the left edge, in the center or at the right edge of the view. */
    public var noDataTextAlignment: Paint.Align
        get() = infoPaint.textAlign
        set(value) {
            infoPaint.textAlign = value
        }

    /** Stops the parent view from intercepting touch events, so a gesture stays with the chart. */
    public fun disableScroll() {
        parent?.requestDisallowInterceptTouchEvent(true)
    }

    /** Lets the parent view intercept touch events again. */
    public fun enableScroll() {
        parent?.requestDisallowInterceptTouchEvent(false)
    }


    /**
     * Draws the chart into a new ARGB_8888 bitmap of [width] x [height] pixels, over its background or white.
     *
     * The size defaults to the chart's current size. When another size is asked for, the chart is laid out at that
     * size for the drawing and put back to its previous size afterwards, so a chart that was never shown, for example
     * one built in code only to export an image, can be drawn with `chart.toBitmap(1200, 800)`. A chart without a size
     * and without an asked for size gives a 1x1 bitmap.
     */
    @JvmOverloads
    public fun toBitmap(width: Int = this.width, height: Int = this.height): Bitmap {
        val bitmapWidth = width.coerceAtLeast(1)
        val bitmapHeight = height.coerceAtLeast(1)
        val previousWidth = this.width
        val previousHeight = this.height
        val resize = bitmapWidth != previousWidth || bitmapHeight != previousHeight
        if (resize) layoutAt(bitmapWidth, bitmapHeight)

        val returnedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        draw(canvas)

        if (resize && previousWidth > 0 && previousHeight > 0) layoutAt(previousWidth, previousHeight)
        return returnedBitmap
    }

    private fun layoutAt(width: Int, height: Int) {
        measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
        layout(left, top, left + width, top + height)
    }

    /**
     * Saves the chart as an image into the device gallery through the MediaStore.
     * Below Android 10 the app needs the write external storage permission.
     *
     * @param fileName File name. The extension for [format] is appended when missing.
     * @param subFolderPath Folder inside DCIM the image is stored in, or empty for DCIM itself.
     * @param fileDescription Description stored with the image.
     * @param format Image format. PNG ignores [quality].
     * @param quality Compression quality from 0 to 100. Values outside that range fall back to 40.
     * @return True when the image was written, false when the view has no size yet, the folder could not be created, the
     * MediaStore entry could not be inserted or writing failed. A failed write leaves no entry behind.
     */
    public fun saveToGallery(
        fileName: String,
        subFolderPath: String = "",
        fileDescription: String = "MPAndroidChart-Library Save",
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 40
    ): Boolean {
        if (width <= 0 || height <= 0) return false

        val safeQuality = if (quality in 0..100) quality else 40

        val (mimeType, extension) = when {
            format == Bitmap.CompressFormat.PNG -> "image/png" to ".png"
            format.name.startsWith("WEBP") -> "image/webp" to ".webp"
            else -> "image/jpeg" to ".jpg"
        }
        val name = if (fileName.endsWith(extension) || (extension == ".jpg" && fileName.endsWith(".jpeg"))) fileName else fileName + extension

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.DESCRIPTION, fileDescription)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/$subFolderPath".trimEnd('/'))
                put(MediaStore.Images.Media.IS_PENDING, 1)
            } else {
                @Suppress("DEPRECATION")
                val dir = File(Environment.getExternalStorageDirectory(), "DCIM/$subFolderPath")
                if (!dir.exists() && !dir.mkdirs()) return false
                @Suppress("DEPRECATION")
                put(MediaStore.Images.Media.DATA, File(dir, name).absolutePath)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false

        return try {
            val stream = resolver.openOutputStream(uri) ?: throw IOException("Could not open $uri for writing")
            stream.use { toBitmap().compress(format, safeQuality, it) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
            true
        } catch (e: IOException) {
            resolver.delete(uri, null, null)
            false
        }
    }

    /** Removes a job from [jobs] before it has run. */
    public fun removeViewportJob(job: Runnable) {
        jobs.remove(job)
    }

    /** Drops all jobs waiting in [jobs]. */
    public fun clearAllViewportJobs() {
        jobs.clear()
    }

    /**
     * Runs a viewport job once the chart has a size: posted right away when it already has one, otherwise stored
     * in [jobs] until `onSizeChanged`. The zoom and move functions use this. At most [MAX_WAITING_JOBS] wait,
     * because only the last position matters and a chart that never gets a size would otherwise collect them
     * forever.
     */
    public fun addViewportJob(job: Runnable) {
        if (viewPortHandler.hasChartDimens()) {
            post(job)
        } else {
            while (jobs.size >= MAX_WAITING_JOBS) jobs.removeAt(0)
            jobs.add(job)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(0, 0, child.measuredWidth, child.measuredHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Utils.convertDpToPixel(50f).toInt()
        setMeasuredDimension(
            max(suggestedMinimumWidth, resolveSize(size, widthMeasureSpec)),
            max(suggestedMinimumHeight, resolveSize(size, heightMeasureSpec))
        )
        measureChildren(
            MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.AT_MOST)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (isLogEnabled) Log.i(LOG_TAG, "OnSizeChanged()")

        if (w > 0 && h > 0 && w < 10000 && h < 10000) {
            if (isLogEnabled) Log.i(LOG_TAG, "Setting chart dimens, width: $w, height: $h")
            viewPortHandler.setChartDimens(w.toFloat(), h.toFloat())
        } else {
            if (isLogEnabled) Log.w(LOG_TAG, "*Avoiding* setting chart dimens! width: $w, height: $h")
        }

        notifyDataSetChanged()

        for (r in jobs) post(r)
        jobs.clear()

        super.onSizeChanged(w, h, oldw, oldh)
    }

    /** Whether the view uses a hardware layer. Setting it switches between hardware and software layer type. */
    public var isHardwareAccelerationEnabled: Boolean
        get() = layerType == LAYER_TYPE_HARDWARE
        set(value) {
            setLayerType(if (value) LAYER_TYPE_HARDWARE else LAYER_TYPE_SOFTWARE, null)
        }

    /** Ends every running animation at its final state, see [stopAnimations], so no animation keeps the chart alive. */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimations()
        if (isUnbindEnabled) unbindDrawables(this)
    }

    private fun unbindDrawables(view: View) {
        view.background?.callback = null
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                unbindDrawables(view.getChildAt(i))
            }
            view.removeAllViews()
        }
    }

    public companion object {

        /** Logcat tag used by all chart classes. */
        public const val LOG_TAG: String = "MPAndroidChart"

        /** How many viewport jobs wait for the chart to get a size before the oldest are dropped. */
        private const val MAX_WAITING_JOBS = 64

    }
}
