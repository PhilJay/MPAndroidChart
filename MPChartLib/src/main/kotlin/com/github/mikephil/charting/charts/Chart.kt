package com.github.mikephil.charting.charts

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
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
abstract class Chart<T : ChartData<out IDataSet<out Entry<*>>>> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle), ChartInterface {

    /** Writes chart internals to logcat while true. Slows down drawing, so keep it off in production. */
    var isLogEnabled = false

    /**
     * The data this chart displays, or null when nothing has been set.
     *
     * Setting a non-null value sizes the default value formatter to the data's y range, assigns that
     * formatter to every data set that has none, and calls [notifyDataSetChanged], which recalculates
     * and redraws the chart. Setting null removes the data and redraws; [clear] does the same and also
     * clears highlights.
     */
    override var data: T? = null
        set(value) {
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
    var isHighlightPerTapEnabled = true

    /** Whether the chart keeps scrolling after the finger is lifted. */
    var isDragDecelerationEnabled = true

    /**
     * How slowly a deceleration scroll loses speed, between 0 (stops at once) and 0.999.
     * Values outside that range are clamped. Default 0.9.
     */
    var dragDecelerationFrictionCoef = 0.9f
        set(value) {
            field = value.coerceIn(0f, 0.999f)
        }

    /** Formatter used for data sets without their own. Its digit count follows the y range of the data. */
    protected val defaultValueFormatterInternal = DefaultValueFormatter(0)

    override val defaultValueFormatter: IValueFormatter
        get() = defaultValueFormatterInternal

    /** Paint used for the description text. */
    lateinit var descriptionPaint: Paint

    /** Paint used for the no-data text. */
    lateinit var infoPaint: Paint

    /** The x axis component: its labels, range, grid lines and position. */
    lateinit var xAxis: XAxis
        protected set

    /** Whether the chart reacts to touch at all. False disables tapping, dragging, scaling and rotating. */
    var isTouchEnabled = true

    /** The description text drawn in the bottom right corner by default. */
    lateinit var description: Description

    /** The legend component. It is filled from the data sets unless a custom legend is set on it. */
    lateinit var legend: Legend
        protected set

    /** Single listener told when a value is selected or the selection is cleared. */
    var onChartValueSelectedListener: OnChartValueSelectedListener? = null

    /**
     * Additional listeners, notified after [onChartValueSelectedListener] in list order.
     * Meant for observers that must not replace the app's own listener, such as Compose state holders.
     */
    val valueSelectedListeners: MutableList<OnChartValueSelectedListener> = mutableListOf()

    /** Additional gesture listeners, notified after [onChartGestureListener] in list order. */
    val gestureListeners: MutableList<OnChartGestureListener> = mutableListOf()

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
    fun onValueSelected(onNothingSelected: () -> Unit = {}, onValueSelected: (entry: Entry<*>, highlight: Highlight) -> Unit) {
        onChartValueSelectedListener = object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry<*>, h: Highlight) = onValueSelected(e, h)
            override fun onNothingSelected() = onNothingSelected()
        }
    }

    /** Handles touch events for this chart. Replace it to customize gestures; [isTouchEnabled] must stay true. */
    lateinit var chartTouchListener: ChartTouchListener<*>

    /** Text drawn in the center while [data] is null. An empty string draws nothing. */
    var noDataText = "No chart data available."

    /** Single listener told about gestures such as tap, double tap, drag, scale and fling. */
    var onChartGestureListener: OnChartGestureListener? = null

    /** Draws the [legend]. */
    lateinit var legendRenderer: LegendRenderer
        protected set

    /** Paint used for legend labels. */
    var legendLabelPaint: Paint
        get() = legendRenderer.labelPaint
        set(value) {
            legendRenderer.labelPaint = value
        }

    /** Draws the data. Each chart type installs its own renderer; replace it for custom drawing. */
    lateinit var renderer: DataRenderer

    /** Turns a touch position into the [Highlight] of the nearest value. */
    lateinit var highlighter: IHighlighter

    /** Holds the chart size, the content rectangle and the zoom and drag matrix. */
    var viewPortHandler = ViewPortHandler()
        protected set

    /** Drives the x and y animation phases used by [animateX], [animateY] and [animateXY]. */
    lateinit var animator: ChartAnimator
        protected set

    /** Extra space in dp added above the content area on top of the calculated offsets. */
    var extraTopOffset = 0f

    /** Extra space in dp added right of the content area on top of the calculated offsets. */
    var extraRightOffset = 0f

    /** Extra space in dp added below the content area on top of the calculated offsets. */
    var extraBottomOffset = 0f

    /** Extra space in dp added left of the content area on top of the calculated offsets. */
    var extraLeftOffset = 0f

    private var offsetsCalculated = false

    /** The values currently highlighted. Empty, never null, when nothing is highlighted. */
    var highlighted: List<Highlight> = emptyList()
        protected set

    /** Farthest distance in dp a touch may be from a value and still highlight it. Default 500. */
    override var maxHighlightDistance = 500f

    /** Whether the [marker] is drawn at highlighted values. */
    var isDrawMarkersEnabled = true

    /**
     * Marker drawn at each highlighted value, or null for none.
     * A [MarkerView] or [MarkerImage] is attached to this chart when set.
     */
    var marker: IMarker? = null
        set(value) {
            field = value
            (value as? MarkerView)?.chartView = this
            (value as? MarkerImage)?.chartView = this
        }

    /** Viewport jobs waiting for the chart to get a size. They run once in `onSizeChanged`. */
    val jobs: MutableList<Runnable> = mutableListOf()

    /**
     * Whether background drawables and child views are released when the chart leaves the window. Leave it off for
     * a chart that is reattached later, such as a list row or a pager page, because it also removes the view a
     * Compose marker keeps as a child.
     */
    var isUnbindEnabled = false

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
            color = Color.rgb(247, 189, 51)
            textAlign = Paint.Align.CENTER
            textSize = Utils.convertDpToPixel(12f)
        }

        if (isLogEnabled) Log.i(LOG_TAG, "Chart.init()")
    }

    /** Sets [data] to null, clears the highlight and redraws, which shows [noDataText]. */
    fun clear() {
        data = null
        offsetsCalculated = false
        highlighted = emptyList()
        chartTouchListener.lastHighlighted = null
        invalidate()
    }

    /** Removes all data sets from [data] but keeps the data object, then redraws. Does nothing without data. */
    fun clearValues() {
        data?.clearValues()
        invalidate()
    }

    /** True when there is no data or the data holds no entries. */
    val isEmpty: Boolean
        get() = (data?.entryCount ?: 0) <= 0

    /**
     * Refreshes the data, recalculates axis ranges, legend and offsets and redraws.
     * Call it after changing entries or data sets in place. Does nothing while [data] is null.
     */
    abstract fun notifyDataSetChanged()

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
        if (data == null) {
            if (noDataText.isNotEmpty()) {
                val pt = center
                when (infoPaint.textAlign) {
                    Paint.Align.LEFT -> {
                        pt.x = 0f
                        canvas.drawText(noDataText, pt.x, pt.y, infoPaint)
                    }
                    Paint.Align.RIGHT -> {
                        pt.x *= 2.0f
                        canvas.drawText(noDataText, pt.x, pt.y, infoPaint)
                    }
                    else -> canvas.drawText(noDataText, pt.x, pt.y, infoPaint)
                }
            }
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
    fun valuesToHighlight(): Boolean {
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
    fun highlightValues(highs: List<Highlight>) {
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
    fun highlightValue(x: Float, dataSetIndex: Int, dataIndex: Int = -1, stackIndex: Int = -1, callListener: Boolean = true) {
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
    fun highlightValue(x: Float, y: Float, dataSetIndex: Int, dataIndex: Int = -1, stackIndex: Int = -1, callListener: Boolean = true) {
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
    fun highlightValue(highlight: Highlight?, callListener: Boolean = true) {
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
    open fun getHighlightByTouchPoint(x: Float, y: Float): Highlight? {
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
     * Animates the drawing of the data along both axes. The chart redraws itself while animating.
     *
     * @param durationMillisX Duration of the x animation in milliseconds.
     * @param durationMillisY Duration of the y animation in milliseconds.
     * @param easingX Easing of the x animation.
     * @param easingY Easing of the y animation. Defaults to [easingX].
     */
    fun animateXY(durationMillisX: Int, durationMillisY: Int, easingX: EasingFunction = Easing.Linear, easingY: EasingFunction = easingX) {
        animator.animateXY(durationMillisX, durationMillisY, easingX, easingY)
    }

    /**
     * Animates the drawing of the data along the x axis. The chart redraws itself while animating.
     *
     * @param durationMillis Duration in milliseconds.
     * @param easing Easing of the animation.
     */
    fun animateX(durationMillis: Int, easing: EasingFunction = Easing.Linear) {
        animator.animateX(durationMillis, easing)
    }

    /**
     * Animates the drawing of the data along the y axis. The chart redraws itself while animating.
     *
     * @param durationMillis Duration in milliseconds.
     * @param easing Easing of the animation.
     */
    fun animateY(durationMillis: Int, easing: EasingFunction = Easing.Linear) {
        animator.animateY(durationMillis, easing)
    }

    /** Largest y value across all data sets, or 0 without data. */
    val yMax: Float
        get() = data?.yMax ?: 0f

    /** Smallest y value across all data sets, or 0 without data. */
    val yMin: Float
        get() = data?.yMin ?: 0f

    override val xChartMax: Float
        get() = xAxis.axisMaximum

    override val xChartMin: Float
        get() = xAxis.axisMinimum

    override val xRange: Float
        get() = xAxis.axisRange

    /** Center of the whole view in pixels, ignoring offsets. See [centerOffsets] for the content center. */
    val center: MPPointF
        get() = MPPointF(width / 2f, height / 2f)

    override val centerOffsets: MPPointF
        get() = viewPortHandler.contentCenter

    override val centerOfView: MPPointF
        get() = center

    override val contentRect: RectF
        get() = viewPortHandler.contentRect

    /** Sets all four extra offsets in dp at once. They apply on the next offset calculation. */
    fun setExtraOffsets(left: Float, top: Float, right: Float, bottom: Float) {
        extraLeftOffset = left
        extraTopOffset = top
        extraRightOffset = right
        extraBottomOffset = bottom
    }

    /** Color of [noDataText]. */
    var noDataTextColor: Int
        get() = infoPaint.color
        set(value) {
            infoPaint.color = value
        }

    /** Typeface of [noDataText], or null for the default. */
    var noDataTextTypeface: Typeface?
        get() = infoPaint.typeface
        set(value) {
            infoPaint.typeface = value
        }

    /** Horizontal alignment of [noDataText]: left edge, center or right edge of the view. */
    var noDataTextAlignment: Paint.Align
        get() = infoPaint.textAlign
        set(value) {
            infoPaint.textAlign = value
        }

    /** Stops the parent view from intercepting touch events, so a gesture stays with the chart. */
    fun disableScroll() {
        parent?.requestDisallowInterceptTouchEvent(true)
    }

    /** Lets the parent view intercept touch events again. */
    fun enableScroll() {
        parent?.requestDisallowInterceptTouchEvent(false)
    }


    /** Draws the chart into a new ARGB_8888 bitmap of the view size, over its background or white. */
    fun toBitmap(): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        draw(canvas)
        return returnedBitmap
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
     * @return True when the image was written, false when the folder could not be created, the
     * MediaStore entry could not be inserted or writing failed. A failed write leaves no entry behind.
     */
    fun saveToGallery(
        fileName: String,
        subFolderPath: String = "",
        fileDescription: String = "MPAndroidChart-Library Save",
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 40
    ): Boolean {
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
    fun removeViewportJob(job: Runnable) {
        jobs.remove(job)
    }

    /** Drops all jobs waiting in [jobs]. */
    fun clearAllViewportJobs() {
        jobs.clear()
    }

    /**
     * Runs a viewport job once the chart has a size: posted right away when it already has one, otherwise stored
     * in [jobs] until `onSizeChanged`. The zoom and move functions use this. At most [MAX_WAITING_JOBS] wait,
     * because only the last position matters and a chart that never gets a size would otherwise collect them
     * forever.
     */
    fun addViewportJob(job: Runnable) {
        if (viewPortHandler.hasChartDimens()) {
            post(job)
        } else {
            while (jobs.size >= MAX_WAITING_JOBS) jobs.removeAt(0)
            jobs.add(job)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            getChildAt(i).layout(0, 0, right - left, bottom - top)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = Utils.convertDpToPixel(50f).toInt()
        setMeasuredDimension(
            max(suggestedMinimumWidth, resolveSize(size, widthMeasureSpec)),
            max(suggestedMinimumHeight, resolveSize(size, heightMeasureSpec))
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
    var isHardwareAccelerationEnabled: Boolean
        get() = layerType == LAYER_TYPE_HARDWARE
        set(value) {
            setLayerType(if (value) LAYER_TYPE_HARDWARE else LAYER_TYPE_SOFTWARE, null)
        }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
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

    companion object {

        /** Logcat tag used by all chart classes. */
        const val LOG_TAG = "MPAndroidChart"

        /** How many viewport jobs wait for the chart to get a size before the oldest are dropped. */
        private const val MAX_WAITING_JOBS = 64

    }
}
