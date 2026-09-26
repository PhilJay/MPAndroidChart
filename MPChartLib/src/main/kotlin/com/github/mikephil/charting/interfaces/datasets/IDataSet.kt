package com.github.mikephil.charting.interfaces.datasets

import android.graphics.DashPathEffect
import android.graphics.Typeface
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.MPPointF

/**
 * A set of entries with shared styling, the unit a chart draws as one line, one group of bars, one pie
 * and so on. Renderers and highlighters only read this interface; the concrete data set classes in
 * `com.github.mikephil.charting.data` implement it and inherit these docs.
 *
 * Entries are expected in ascending x order. Lookups by x value use binary search and cost time at runtime,
 * so avoid them in tight loops.
 *
 * @param T the entry type this data set holds.
 */
public interface IDataSet<T : Entry<*>> {

    /** Smallest y value in this data set, [Float.MAX_VALUE] when empty. */
    public val yMin: Float

    /** Largest y value in this data set, -[Float.MAX_VALUE] when empty. */
    public val yMax: Float

    /** Smallest x value in this data set, [Float.MAX_VALUE] when empty. */
    public val xMin: Float

    /** Largest x value in this data set, -[Float.MAX_VALUE] when empty. */
    public val xMax: Float

    /** Number of entries in this data set. */
    public val entryCount: Int

    /**
     * True when an entry's y may not be a finite number, so a renderer has to look for gaps before drawing a
     * line. The default is true, which is always safe; answer false only when every y is known to be finite.
     */
    public val hasNonFiniteY: Boolean
        get() = true

    /** Recomputes [xMin], [xMax], [yMin] and [yMax] from all entries. */
    public fun calcMinMax()

    /**
     * Recomputes [yMin] and [yMax] from the entries between the x values [fromX] and [toX] only.
     * Used when the chart auto scales its y axis to the visible range.
     */
    public fun calcMinMaxY(fromX: Float, toX: Float)

    /**
     * Returns the entry at [xValue], or the entry at the nearest x value according to [rounding] if there is none.
     *
     * @param closestToY when several entries share the x value, the one whose y value is nearest to this wins;
     *   NaN picks the first.
     * @param rounding whether to round up, down or to the closest x value when [xValue] has no entry.
     * @return null if the data set is empty or rounding leaves the entry range.
     */
    public fun getEntryForXValue(xValue: Float, closestToY: Float = Float.NaN, rounding: DataSet.Rounding = DataSet.Rounding.CLOSEST): T?

    /** Returns all entries whose x value equals [xValue], or an empty list if there is none. */
    public fun getEntriesForXValue(xValue: Float): List<T>

    /**
     * Returns the entry at position [index] in the entry list.
     *
     * @throws IndexOutOfBoundsException if [index] is outside 0 until [entryCount].
     */
    public fun getEntryForIndex(index: Int): T

    /**
     * Returns the index of the entry at [xValue], or of the entry at the nearest x value according to [rounding].
     *
     * @param closestToY when several entries share the x value, the one whose y value is nearest to this wins;
     *   NaN picks the first.
     * @param rounding whether to round up, down or to the closest x value when [xValue] has no entry.
     * @return -1 if the data set is empty or rounding leaves the entry range.
     */
    public fun getEntryIndex(xValue: Float, closestToY: Float = Float.NaN, rounding: DataSet.Rounding = DataSet.Rounding.CLOSEST): Int

    /** Returns the index of [e] in the entry list, or -1 if it is not contained. */
    public fun getEntryIndex(e: Entry<*>): Int

    /**
     * Returns the index of the first entry whose x value equals [xIndex] by scanning all entries,
     * or -1 if there is none.
     */
    public fun getIndexInEntries(xIndex: Int): Int

    /**
     * Appends [e] at the end of the entry list and updates the min and max values.
     *
     * @return true if the entry was added.
     */
    public fun addEntry(e: T): Boolean

    /** Inserts [e] at the position matching its x value and updates the min and max values. */
    public fun addEntryOrdered(e: T)

    /** Removes the entry at index 0. Returns false if the data set is empty. */
    public fun removeFirst(): Boolean

    /** Removes the last entry. Returns false if the data set is empty. */
    public fun removeLast(): Boolean

    /** Removes [e] and updates the min and max values. Returns false if [e] is not contained. */
    public fun removeEntry(e: T): Boolean

    /** Removes the entry at or closest to [xValue]. Returns false if the data set is empty. */
    public fun removeEntryByXValue(xValue: Float): Boolean

    /** Removes the entry at [index] and returns true. Throws when [index] is out of range. */
    public fun removeEntry(index: Int): Boolean

    /** True if [entry] is in this data set. Scans all entries. */
    public fun contains(entry: T): Boolean

    /** Removes all entries and resets the min and max values. */
    public fun clear()

    /** Name shown in the legend for this data set. */
    public var label: String

    /** The y axis this data set is plotted against. Default [YAxis.AxisDependency.LEFT]. */
    public var axisDependency: YAxis.AxisDependency

    /** Colors used to draw the entries, cycled by [getColor] when there are fewer colors than entries. */
    public val colors: List<Int>

    /** The first of [colors], the color used when only one is set. */
    public val color: Int

    /** Returns the color for entry [index], wrapping around [colors] with modulo. */
    public fun getColor(index: Int): Int

    /** True if entries of this data set can be highlighted by touch or `chart.highlightValue(...)`. Default true. */
    public var isHighlightEnabled: Boolean

    /**
     * Formatter for the value labels drawn at the entries. Until one is set, the chart's default formatter is
     * returned and [needsFormatter] is true.
     */
    public var valueFormatter: IValueFormatter

    /** True if no formatter was set, so the chart should supply its default formatter. */
    public val needsFormatter: Boolean

    /** Color of the value labels. Reading it returns the first of [valueTextColors]; writing replaces the whole list. */
    public var valueTextColor: Int

    /** Colors of the value labels, cycled by [getValueTextColor] when there are fewer colors than entries. */
    public var valueTextColors: List<Int>

    /** Returns the value label color for entry [index], wrapping around [valueTextColors] with modulo. */
    public fun getValueTextColor(index: Int): Int

    /** Typeface of the value labels, null for the default. */
    public var valueTypeface: Typeface?

    /** Text size of the value labels in dp. Default 7 dp. */
    public var valueTextSize: Float

    /** Shape drawn for this data set in the legend. [Legend.LegendForm.DEFAULT] uses the legend's form. */
    public val form: Legend.LegendForm

    /** Size of the legend form in dp. NaN uses the legend's form size. */
    public val formSize: Float

    /** Line width in dp for legend forms drawn as lines. NaN uses the legend's line width. */
    public val formLineWidth: Float

    /** Dash effect for legend forms drawn as lines. Null uses the legend's dash effect. */
    public val formLineDashEffect: DashPathEffect?

    /**
     * True to draw the value labels at the entries. Default true. Not drawn when more entries are visible
     * than the chart's `maxVisibleCount` allows.
     */
    public var isDrawValuesEnabled: Boolean

    /**
     * True to draw the entry icons. Default true. Not drawn when more entries are visible than the chart's
     * `maxVisibleCount` allows.
     */
    public var isDrawIconsEnabled: Boolean

    /**
     * Offset of the icons from their entry position in dp. For pie and radar charts x moves the icon along
     * the value direction and y moves it towards or away from the center.
     */
    public var iconsOffset: MPPointF

    /** True if this data set is drawn. Default true. Hidden data sets still count for the axis ranges. */
    public var isVisible: Boolean
}
