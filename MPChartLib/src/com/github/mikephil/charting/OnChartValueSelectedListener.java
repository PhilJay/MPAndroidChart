package com.github.mikephil.charting;

public interface OnChartValueSelectedListener {

    public void onValuesSelected(Series[] values, Highlight[] highlights);
    public void onNothingSelected();
}
