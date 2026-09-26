package com.github.mikephil.charting.data

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils

/**
 * Styling and bookkeeping shared by every data set: colors, value labels, legend form, axis and visibility.
 * Entry storage is left to [DataSet]; the entry related overrides here work through [getEntryForIndex].
 *
 * @param T the entry type this set holds.
 * @param label name of the set, shown in the legend and usable for lookup via [ChartData.getDataSetByLabel].
 */
public abstract class BaseDataSet<T : Entry<*>>(label: String = "DataSet") : IDataSet<T> {

    /** Colors used to draw the entries, reused from the start when there are more entries than colors. */
    override var colors: List<Int> = listOf(Color.rgb(140, 234, 255))

    /** Colors used to draw the value labels, reused from the start like [colors]. Default black. */
    override var valueTextColors: List<Int> = mutableListOf(Color.BLACK)

    override var label: String = label

    override var axisDependency: YAxis.AxisDependency = YAxis.AxisDependency.LEFT

    override var isHighlightEnabled: Boolean = true

    private var formatter: IValueFormatter? = null

    /** Formatter for the value labels. Returns [Utils.defaultValueFormatter] until a custom one is assigned. */
    override var valueFormatter: IValueFormatter
        get() = formatter ?: Utils.defaultValueFormatter
        set(value) {
            formatter = value
        }

    /** True while no custom [valueFormatter] is assigned, so the chart may apply its own default formatter. */
    override val needsFormatter: Boolean
        get() = formatter == null

    override var valueTypeface: Typeface? = null

    /** Shape of this set's legend entry. [Legend.LegendForm.DEFAULT] uses the legend's form. */
    override var form: Legend.LegendForm = Legend.LegendForm.DEFAULT

    /** Size of this set's legend form in dp. NaN (the default) uses the legend's form size. */
    override var formSize: Float = Float.NaN

    /** Line width of this set's legend form in dp when the form is a line. NaN uses the legend's width. */
    override var formLineWidth: Float = Float.NaN

    /** Dash effect for this set's legend form when the form is a line. Null uses the legend's dash effect. */
    override var formLineDashEffect: DashPathEffect? = null

    override var isDrawValuesEnabled: Boolean = true

    override var isDrawIconsEnabled: Boolean = true

    /** Offset in dp applied to every entry icon. Assigning copies x and y into the existing point. */
    override var iconsOffset: MPPointF = MPPointF()
        set(value) {
            field.x = value.x
            field.y = value.y
        }

    /** Text size of the value labels in dp. Default 7. */
    override var valueTextSize: Float = 7f

    override var isVisible: Boolean = true

    /** Recomputes the cached x and y ranges from the entries. Call after changing entries in place. */
    public fun notifyDataSetChanged() {
        calcMinMax()
    }

    /**
     * The first entry color, or black when [colors] is empty. Assigning replaces the whole [colors] list with this one color.
     */
    override var color: Int
        get() = colors.firstOrNull() ?: Color.BLACK
        set(value) {
            colors = listOf(value)
        }

    /**
     * Returns the color for the entry at [index], wrapping around [colors] when there are fewer colors. Black when
     * [colors] is empty.
     */
    override fun getColor(index: Int): Int = colorAt(colors, index)

    /** Replaces [colors] with the given ARGB colors. */
    public fun setColors(vararg colors: Int) {
        this.colors = colors.toList()
    }

    /**
     * Replaces [colors] with colors resolved from resource ids.
     *
     * @param colorResIds color resource ids, for example `R.color.red`.
     * @param context used to resolve the ids.
     */
    public fun setColors(colorResIds: List<Int>, context: Context) {
        colors = colorResIds.map { context.getColor(it) }
    }

    /** Appends one color to [colors]. */
    public fun addColor(color: Int) {
        colors = colors + color
    }

    /**
     * Replaces [colors] with this one color after applying [alpha].
     *
     * @param color the ARGB color; its own alpha is ignored.
     * @param alpha opacity from 0 (transparent) to 255 (opaque).
     */
    public fun setColor(color: Int, alpha: Int) {
        this.color = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

    /**
     * Replaces [colors] with the given colors after applying [alpha] to each of them.
     *
     * @param colors the ARGB colors; their own alpha is ignored.
     * @param alpha opacity from 0 (transparent) to 255 (opaque).
     */
    public fun setColors(colors: List<Int>, alpha: Int) {
        this.colors = colors.map { Color.argb(alpha, Color.red(it), Color.green(it), Color.blue(it)) }
    }

    /** Removes all colors. Add at least one color before the set is drawn, [getColor] fails on an empty list. */
    public fun resetColors() {
        colors = emptyList()
    }

    /** The first value label color, or black when the list is empty. Assigning replaces the whole [valueTextColors] list with this one color. */
    override var valueTextColor: Int
        get() = valueTextColors.firstOrNull() ?: Color.BLACK
        set(value) {
            valueTextColors = mutableListOf(value)
        }

    /**
     * Returns the value label color for the entry at [index], wrapping around [valueTextColors]. Black when the list
     * is empty.
     */
    override fun getValueTextColor(index: Int): Int = colorAt(valueTextColors, index)

    /**
     * Finds the entry whose x equals [xIndex] exactly by scanning all entries.
     *
     * @return the index in the entries, or -1 if no entry has that x.
     */
    override fun getIndexInEntries(xIndex: Int): Int {
        for (i in 0 until entryCount) {
            if (xIndex.toFloat() == getEntryForIndex(i).x) return i
        }
        return -1
    }

    /** Removes the first entry. Returns false and does nothing when the set is empty. */
    override fun removeFirst(): Boolean {
        if (entryCount <= 0) return false
        return removeEntry(getEntryForIndex(0))
    }

    /** Removes the last entry. Returns false and does nothing when the set is empty. */
    override fun removeLast(): Boolean {
        if (entryCount <= 0) return false
        return removeEntry(getEntryForIndex(entryCount - 1))
    }

    /**
     * Removes the entry whose x is closest to [xValue], which need not be an exact match.
     *
     * @return true if an entry was removed, false when the set is empty.
     */
    override fun removeEntryByXValue(xValue: Float): Boolean {
        val e = getEntryForXValue(xValue, Float.NaN) ?: return false
        return removeEntry(e)
    }

    /**
     * Removes the entry at [index].
     *
     * @throws IndexOutOfBoundsException when [index] is not a valid entry index.
     */
    override fun removeEntry(index: Int): Boolean {
        return removeEntry(getEntryForIndex(index))
    }

    /** Returns true if [entry] is one of this set's entries, compared with `equals` (reference for [Entry]). */
    override fun contains(entry: T): Boolean {
        for (i in 0 until entryCount) {
            if (getEntryForIndex(i) == entry) return true
        }
        return false
    }

    /** Copies all styling from this set into [baseDataSet]. Color lists and the formatter are shared. */
    protected open fun copy(baseDataSet: BaseDataSet<*>) {
        baseDataSet.axisDependency = axisDependency
        baseDataSet.colors = colors
        baseDataSet.isDrawIconsEnabled = isDrawIconsEnabled
        baseDataSet.isDrawValuesEnabled = isDrawValuesEnabled
        baseDataSet.form = form
        baseDataSet.formLineDashEffect = formLineDashEffect
        baseDataSet.formLineWidth = formLineWidth
        baseDataSet.formSize = formSize
        baseDataSet.isHighlightEnabled = isHighlightEnabled
        baseDataSet.iconsOffset = iconsOffset
        baseDataSet.valueTextColors = valueTextColors
        baseDataSet.formatter = formatter
        baseDataSet.valueTextSize = valueTextSize
        baseDataSet.valueTypeface = valueTypeface
        baseDataSet.isVisible = isVisible
    }
}

internal fun colorAt(colors: List<Int>, index: Int): Int =
    if (colors.isEmpty()) Color.BLACK else colors[Math.floorMod(index, colors.size)]
