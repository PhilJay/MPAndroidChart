package com.github.mikephil.charting.components

import android.graphics.DashPathEffect
import android.graphics.Paint
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.FSize
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.max
import kotlin.math.min

/**
 * The legend of a chart. By default it shows one entry per data set, or one per color for data sets with several
 * colors, recomputed whenever the chart data changes. Assign [entries] to show your own entries instead. Sizes and
 * spacings are in dp and converted to px in [calculateDimensions].
 */
open class Legend() : ComponentBase() {

    /** Shape drawn in front of a legend label. */
    enum class LegendForm {
        /** No form, and no space kept for one. */
        NONE,
        /** No form, but the space for one is kept. */
        EMPTY,
        /** On an entry: the legend's [form]. On the legend itself: drawn as a circle. */
        DEFAULT,
        /** A filled square. */
        SQUARE,
        /** A filled circle. */
        CIRCLE,
        /** A horizontal line using [formLineWidth] and [formLineDashEffect]. */
        LINE
    }

    /** Horizontal placement of the legend: at the left edge, centered, or at the right edge of the chart. */
    enum class LegendHorizontalAlignment {
        LEFT, CENTER, RIGHT
    }

    /** Vertical placement of the legend: at the top edge, centered, or at the bottom edge of the chart. */
    enum class LegendVerticalAlignment {
        TOP, CENTER, BOTTOM
    }

    /** Layout of the entries: HORIZONTAL in rows, wrapping when [isWordWrapEnabled], or VERTICAL in one column. */
    enum class LegendOrientation {
        HORIZONTAL, VERTICAL
    }

    /**
     * Order of form and label: LEFT_TO_RIGHT draws the form before the label and lays entries out to the right,
     * RIGHT_TO_LEFT draws the label before the form and lays entries out to the left.
     */
    enum class LegendDirection {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    /**
     * Entries to draw. Assigning them makes the legend custom, so it stops mirroring the data sets until
     * [resetCustom]. An entry with a null label draws only its form, stacked with the next entry.
     */
    var entries: List<LegendEntry> = emptyList()
        set(value) {
            field = value
            isLegendCustom = true
        }

    internal fun setComputedEntries(computed: List<LegendEntry>) {
        val custom = isLegendCustom
        entries = computed
        isLegendCustom = custom
    }

    /** Entries appended after the computed ones the next time the chart data changes. Ignored by a custom legend. See [setExtra]. */
    var extraEntries: List<LegendEntry> = emptyList()

    /** Whether [entries] were assigned rather than computed from the data sets. Default false. */
    var isLegendCustom = false
        private set

    /** Horizontal placement of the legend. Default [LegendHorizontalAlignment.LEFT]. */
    var horizontalAlignment = LegendHorizontalAlignment.LEFT

    /** Vertical placement of the legend. Default [LegendVerticalAlignment.BOTTOM]. */
    var verticalAlignment = LegendVerticalAlignment.BOTTOM

    /** Whether the entries are laid out in rows or in one column. Default [LegendOrientation.HORIZONTAL]. */
    var orientation = LegendOrientation.HORIZONTAL

    /** Whether the legend is drawn over the content area instead of the chart reserving space for it. Default false. */
    var isDrawInsideEnabled = false

    /** Order of form and label within an entry. Default [LegendDirection.LEFT_TO_RIGHT]. */
    var direction = LegendDirection.LEFT_TO_RIGHT

    /** Form drawn for entries whose own form is [LegendForm.DEFAULT]. Default [LegendForm.SQUARE]. */
    var form = LegendForm.SQUARE

    /** Size of the forms in dp, used by entries whose own size is NaN. Default 8. */
    var formSize = 8f

    /** Stroke width in dp of [LegendForm.LINE] forms, used by entries whose own width is NaN. Default 3. */
    var formLineWidth = 3f

    /** Dash pattern of [LegendForm.LINE] forms, used by entries without their own; null draws solid lines. */
    var formLineDashEffect: DashPathEffect? = null

    /** Horizontal space in dp between two entries in a row. Default 6. */
    var xEntrySpace = 6f

    /** Vertical space in dp between two entries in a column, or between wrapped rows. Default 0. */
    var yEntrySpace = 0f

    /** Space in dp between a form and its label. Default 5. */
    var formToTextSpace = 5f

    /** Space in dp between stacked forms, that is entries without a label. Default 3. */
    var stackSpace = 3f

    /**
     * Largest share of the chart the legend may take, 0..1: of the width for a vertical legend at the side, of the
     * height for a horizontal one. A wrapping horizontal legend breaks its rows at this share of the content width.
     * Default 0.95.
     */
    var maxSizePercent = 0.95f

    /** Whether a horizontal legend breaks into several rows instead of being cut off. Costs performance. Default false. */
    var isWordWrapEnabled = false

    /** Total width in px the legend needs, including [xOffset]. Set by [calculateDimensions]. */
    var neededWidth = 0f
        internal set

    /** Total height in px the legend needs, including [yOffset]. Set by [calculateDimensions]. */
    var neededHeight = 0f
        internal set

    /** Height in px of the tallest label. Set by [calculateDimensions]. */
    var textHeightMax = 0f
        internal set

    /** Width in px of the widest entry, form and spacing included. Set by [calculateDimensions]. */
    var textWidthMax = 0f
        internal set

    /** Label size in px per entry, in entry order. Filled by [calculateDimensions] for a horizontal legend. */
    val calculatedLabelSizes: MutableList<FSize> = ArrayList(16)

    /** Per entry, whether a new row starts with it. Filled by [calculateDimensions] for a horizontal legend. */
    val calculatedLabelBreakPoints: MutableList<Boolean> = ArrayList(16)

    /** Size in px of each row. Filled by [calculateDimensions] for a horizontal legend. */
    val calculatedLineSizes: MutableList<FSize> = ArrayList(16)

    init {
        textSize = 10f
        xOffset = 5f
        yOffset = 3f
    }

    /** Creates a custom legend that shows [entries]. */
    constructor(entries: List<LegendEntry>) : this() {
        this.entries = entries
    }

    /**
     * Width in px of the widest entry: the longest label plus the largest form size and [formToTextSpace].
     * @param p paint used to measure the labels
     */
    fun getMaximumEntryWidth(p: Paint): Float {
        var max = 0f
        var maxFormSize = 0f
        val formToTextSpace = Utils.convertDpToPixel(formToTextSpace)

        for (entry in entries) {
            val formSize = Utils.convertDpToPixel(if (entry.formSize.isNaN()) formSize else entry.formSize)
            if (formSize > maxFormSize) maxFormSize = formSize

            val label = entry.label ?: continue
            val length = Utils.calcTextWidth(p, label).toFloat()
            if (length > max) max = length
        }

        return max + maxFormSize + formToTextSpace
    }

    /**
     * Height in px of the tallest label.
     * @param p paint used to measure the labels
     */
    fun getMaximumEntryHeight(p: Paint): Float {
        var max = 0f
        for (entry in entries) {
            val label = entry.label ?: continue
            val length = Utils.calcTextHeight(p, label).toFloat()
            if (length > max) max = length
        }
        return max
    }

    /**
     * Builds [extraEntries] from parallel lists of colors and labels; surplus items of the longer list are dropped.
     * A color of [ColorTemplate.COLOR_SKIP] or 0 gives the entry [LegendForm.NONE], [ColorTemplate.COLOR_NONE]
     * gives it [LegendForm.EMPTY].
     * @param colors form color per entry
     * @param labels label text per entry
     */
    fun setExtra(colors: List<Int>, labels: List<String>) {
        val result = mutableListOf<LegendEntry>()
        for (i in 0 until min(colors.size, labels.size)) {
            val entry = LegendEntry()
            entry.formColor = colors[i]
            entry.label = labels[i]

            if (entry.formColor == ColorTemplate.COLOR_SKIP || entry.formColor == 0) {
                entry.form = LegendForm.NONE
            } else if (entry.formColor == ColorTemplate.COLOR_NONE) {
                entry.form = LegendForm.EMPTY
            }

            result.add(entry)
        }
        extraEntries = result
    }

    /** Makes the legend mirror the data sets again after [entries] were assigned. Applied when the chart data changes next. */
    fun resetCustom() {
        isLegendCustom = false
    }

    /**
     * Measures the legend: the largest entry, [neededWidth] and [neededHeight], and for a horizontal legend the row
     * layout. Converts the dp sizes to px. The chart calls this before it lays out its content area.
     * @param labelPaint paint used to measure the labels
     * @param viewPortHandler supplies the content width that rows wrap at
     */
    fun calculateDimensions(labelPaint: Paint, viewPortHandler: ViewPortHandler) {
        val defaultFormSize = Utils.convertDpToPixel(formSize)
        val stackSpace = Utils.convertDpToPixel(stackSpace)
        val formToTextSpace = Utils.convertDpToPixel(formToTextSpace)
        val xEntrySpace = Utils.convertDpToPixel(xEntrySpace)
        val yEntrySpace = Utils.convertDpToPixel(yEntrySpace)
        val wordWrapEnabled = isWordWrapEnabled
        val entries = entries
        val entryCount = entries.size

        textWidthMax = getMaximumEntryWidth(labelPaint)
        textHeightMax = getMaximumEntryHeight(labelPaint)

        when (orientation) {
            LegendOrientation.VERTICAL -> {
                var maxWidth = 0f
                var maxHeight = 0f
                var width = 0f
                val labelLineHeight = Utils.getLineHeight(labelPaint)
                var wasStacked = false

                for (i in 0 until entryCount) {
                    val e = entries[i]
                    val drawingForm = e.form != LegendForm.NONE
                    val formSize = if (e.formSize.isNaN()) defaultFormSize else Utils.convertDpToPixel(e.formSize)
                    val label = e.label

                    if (!wasStacked) width = 0f

                    if (drawingForm) {
                        if (wasStacked) width += stackSpace
                        width += formSize
                    }

                    if (label != null) {
                        if (drawingForm && !wasStacked) {
                            width += formToTextSpace
                        } else if (wasStacked) {
                            maxWidth = max(maxWidth, width)
                            maxHeight += labelLineHeight + yEntrySpace
                            width = 0f
                            wasStacked = false
                        }

                        width += Utils.calcTextWidth(labelPaint, label)
                        maxHeight += labelLineHeight + yEntrySpace
                    } else {
                        wasStacked = true
                        width += formSize
                        if (i < entryCount - 1) width += stackSpace
                    }

                    maxWidth = max(maxWidth, width)
                }

                neededWidth = maxWidth
                neededHeight = maxHeight
            }
            LegendOrientation.HORIZONTAL -> {
                val labelLineHeight = Utils.getLineHeight(labelPaint)
                val labelLineSpacing = Utils.getLineSpacing(labelPaint) + yEntrySpace
                val contentWidth = viewPortHandler.contentWidth * maxSizePercent

                var maxLineWidth = 0f
                var currentLineWidth = 0f
                var requiredWidth = 0f
                var stackedStartIndex = -1

                calculatedLabelBreakPoints.clear()
                calculatedLabelSizes.clear()
                calculatedLineSizes.clear()

                for (i in 0 until entryCount) {
                    val e = entries[i]
                    val drawingForm = e.form != LegendForm.NONE
                    val formSize = if (e.formSize.isNaN()) defaultFormSize else Utils.convertDpToPixel(e.formSize)
                    val label = e.label

                    calculatedLabelBreakPoints.add(false)

                    if (stackedStartIndex == -1) {
                        requiredWidth = 0f
                    } else {
                        requiredWidth += stackSpace
                    }

                    if (label != null) {
                        calculatedLabelSizes.add(Utils.calcTextSize(labelPaint, label))
                        requiredWidth += if (drawingForm) formToTextSpace + formSize else 0f
                        requiredWidth += calculatedLabelSizes[i].width
                    } else {
                        calculatedLabelSizes.add(FSize.getInstance(0f, 0f))
                        requiredWidth += if (drawingForm) formSize else 0f
                        if (stackedStartIndex == -1) stackedStartIndex = i
                    }

                    if (label != null || i == entryCount - 1) {
                        val requiredSpacing = if (currentLineWidth == 0f) 0f else xEntrySpace

                        if (!wordWrapEnabled || currentLineWidth == 0f || contentWidth - currentLineWidth >= requiredSpacing + requiredWidth) {
                            currentLineWidth += requiredSpacing + requiredWidth
                        } else {
                            calculatedLineSizes.add(FSize.getInstance(currentLineWidth, labelLineHeight))
                            maxLineWidth = max(maxLineWidth, currentLineWidth)
                            calculatedLabelBreakPoints[if (stackedStartIndex > -1) stackedStartIndex else i] = true
                            currentLineWidth = requiredWidth
                        }

                        if (i == entryCount - 1) {
                            calculatedLineSizes.add(FSize.getInstance(currentLineWidth, labelLineHeight))
                            maxLineWidth = max(maxLineWidth, currentLineWidth)
                        }
                    }

                    stackedStartIndex = if (label != null) -1 else stackedStartIndex
                }

                neededWidth = maxLineWidth
                neededHeight = labelLineHeight + (labelLineHeight + labelLineSpacing) * (if (calculatedLineSizes.isEmpty()) 0 else calculatedLineSizes.size - 1)
            }
        }

        neededHeight += Utils.convertDpToPixel(yOffset)
        neededWidth += Utils.convertDpToPixel(xOffset)
    }
}
