package com.github.mikephil.charting.listener;

import com.github.mikephil.charting.highlight.Highlight;

public interface OnAxisSelectedListener {

    /**
     * Called when an axis has been selected outside the chart.
     *
     * @param h highlight object that contains information
     *          about the highlighted position.
     */
    void onAxisSelected(Highlight h);

    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    void onNothingSelected();
}