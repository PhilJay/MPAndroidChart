package com.github.mikephil.charting.data

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

/** Data for a bar chart: one or more [IBarDataSet]s plus the shared bar width and grouping. */
public open class BarData : BarLineScatterCandleBubbleData<IBarDataSet<*>> {

    /** Creates an empty data object. */
    public constructor() : super()

    /** Creates a data object holding the given sets. */
    public constructor(vararg dataSets: IBarDataSet<*>) : super(*dataSets)

    /** Creates a data object holding [dataSets]; an `ArrayList` is kept by reference, other lists are copied. */
    public constructor(dataSets: List<IBarDataSet<*>>) : super(dataSets)

    /** Width of every bar in x value units, not pixels. Default 0.85, which leaves a gap between bars 1 apart. */
    public var barWidth: Float = 0.85f

    /**
     * Places the bars of all sets side by side in groups by rewriting the x of their entries in place.
     * Entry i of every set forms group i; groups are laid out for the set with the most entries, starting at
     * [fromX], each group taking [getGroupWidth]. Previously set x values are lost. Recomputes the cached
     * ranges here; the chart still needs its own notify call. Set the x axis range to [fromX] plus the
     * group count times [getGroupWidth] to show all groups.
     *
     * @param fromX x value where the first group starts.
     * @param groupSpace space between groups in x value units, for example 0.8 for a bar width of 1.
     * @param barSpace space between the bars of a group in x value units, for example 0.1 for a bar width of 1.
     * @throws IllegalStateException when this data holds fewer than 2 sets.
     */
    public fun groupBars(fromX: Float, groupSpace: Float, barSpace: Float) {
        val setCount = dataSets.size
        if (setCount <= 1) {
            throw IllegalStateException("BarData needs to hold at least 2 BarDataSets to allow grouping.")
        }

        val max = maxEntryCountSet ?: return
        val maxEntryCount = max.entryCount

        val groupSpaceWidthHalf = groupSpace / 2f
        val barSpaceHalf = barSpace / 2f
        val barWidthHalf = barWidth / 2f

        val interval = getGroupWidth(groupSpace, barSpace)

        var x = fromX
        for (i in 0 until maxEntryCount) {
            val start = x
            x += groupSpaceWidthHalf

            for (set in dataSets) {
                x += barSpaceHalf
                x += barWidthHalf

                if (i < set.entryCount) {
                    set.getEntryForIndex(i).x = x
                }

                x += barWidthHalf
                x += barSpaceHalf
            }

            x += groupSpaceWidthHalf
            val end = x
            val innerInterval = end - start
            val diff = interval - innerInterval

            if (diff > 0 || diff < 0) {
                x += diff
            }
        }

        notifyDataChanged()
    }

    /**
     * Returns the x range one group of bars takes after [groupBars]: one [barWidth] plus [barSpace] per set,
     * plus [groupSpace].
     *
     * @param groupSpace space between groups in x value units.
     * @param barSpace space between the bars of a group in x value units.
     */
    public fun getGroupWidth(groupSpace: Float, barSpace: Float): Float {
        return dataSets.size * (barWidth + barSpace) + groupSpace
    }
}
