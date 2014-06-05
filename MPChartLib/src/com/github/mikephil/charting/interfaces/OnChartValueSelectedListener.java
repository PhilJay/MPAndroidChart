package com.github.mikephil.charting.interfaces;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Highlight;

public interface OnChartValueSelectedListener {

    public void onValuesSelected(Entry[] values, Highlight[] highlights);
    public void onNothingSelected();
}
