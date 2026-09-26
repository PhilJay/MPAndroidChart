package com.github.mikephil.charting.listener

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight

/**
 * Receives the value the user selects in a chart by tapping or, when enabled, dragging.
 * Register it as `chart.onChartValueSelectedListener`, add it to `chart.valueSelectedListeners` or use the
 * `chart.onValueSelected { ... }` shortcut.
 *
 * Programmatic calls to `chart.highlightValue(...)` also report here unless their `callListener` parameter is false.
 */
public interface OnChartValueSelectedListener {

    /**
     * Called when a value is selected.
     *
     * @param e the selected entry.
     * @param h where the selection is: data set index, x and y value, pixel position and stack index.
     */
    public fun onValueSelected(e: Entry<*>, h: Highlight)

    /**
     * Called when the selection is cleared: the user tapped empty space, tapped the selected value again,
     * or the highlighted entry no longer exists in the data.
     */
    public fun onNothingSelected()
}
