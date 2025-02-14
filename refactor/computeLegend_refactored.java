public void computeLegend(ChartData<?> data) {
    if (!mLegend.isLegendCustom()) {
        computedEntries.clear();
        for (int i = 0; i < data.getDataSetCount(); i++) {
            IDataSet dataSet = data.getDataSetByIndex(i);
            if (dataSet == null) continue;
            processDataSet(dataSet);
        }
        if (mLegend.getExtraEntries() != null) {
            Collections.addAll(computedEntries, mLegend.getExtraEntries());
        }
        mLegend.setEntries(computedEntries);
    }

    Typeface tf = mLegend.getTypeface();
    if (tf != null) {
        mLegendLabelPaint.setTypeface(tf);
    }
    mLegendLabelPaint.setTextSize(mLegend.getTextSize());
    mLegendLabelPaint.setColor(mLegend.getTextColor());

    mLegend.calculateDimensions(mLegendLabelPaint, mViewPortHandler);
}

private void processDataSet(IDataSet dataSet) {
    List<Integer> clrs = dataSet.getColors();
    int entryCount = dataSet.getEntryCount();
    if (dataSet instanceof IBarDataSet && ((IBarDataSet) dataSet).isStacked()) {
        processBarDataSet((IBarDataSet) dataSet, clrs);
    } else if (dataSet instanceof IPieDataSet) {
        processPieDataSet((IPieDataSet) dataSet, clrs, entryCount);
    } else if (dataSet instanceof ICandleDataSet && ((ICandleDataSet) dataSet).getDecreasingColor() != ColorTemplate.COLOR_NONE) {
        processCandleDataSet((ICandleDataSet) dataSet);
    } else {
        processOtherDataSet(dataSet, clrs, entryCount);
    }
}

private void processBarDataSet(IBarDataSet dataSet, List<Integer> clrs) {
    String[] sLabels = dataSet.getStackLabels();
    int minEntries = Math.min(clrs.size(), dataSet.getStackSize());
    for (int j = 0; j < minEntries; j++) {
        String label = (sLabels.length > 0) ? sLabels[j % minEntries] : null;
        computedEntries.add(new LegendEntry(label, dataSet.getForm(), dataSet.getFormSize(), dataSet.getFormLineWidth(), dataSet.getFormLineDashEffect(), clrs.get(j)));
    }
    if (dataSet.getLabel() != null) {
        computedEntries.add(new LegendEntry(dataSet.getLabel(), Legend.LegendForm.NONE, Float.NaN, Float.NaN, null, ColorTemplate.COLOR_NONE));
    }
}

private void processPieDataSet(IPieDataSet dataSet, List<Integer> clrs, int entryCount) {
    for (int j = 0; j < clrs.size() && j < entryCount; j++) {
        computedEntries.add(new LegendEntry(dataSet.getEntryForIndex(j).getLabel(), dataSet.getForm(), dataSet.getFormSize(), dataSet.getFormLineWidth(), dataSet.getFormLineDashEffect(), clrs.get(j)));
    }
    if (dataSet.getLabel() != null) {
        computedEntries.add(new LegendEntry(dataSet.getLabel(), Legend.LegendForm.NONE, Float.NaN, Float.NaN, null, ColorTemplate.COLOR_NONE));
    }
}

private void processCandleDataSet(ICandleDataSet dataSet) {
    int decreasingColor = dataSet.getDecreasingColor();
    int increasingColor = dataSet.getIncreasingColor();
    computedEntries.add(new LegendEntry(null, dataSet.getForm(), dataSet.getFormSize(), dataSet.getFormLineWidth(), dataSet.getFormLineDashEffect(), decreasingColor));
    computedEntries.add(new LegendEntry(dataSet.getLabel(), dataSet.getForm(), dataSet.getFormSize(), dataSet.getFormLineWidth(), dataSet.getFormLineDashEffect(), increasingColor));
}

private void processOtherDataSet(IDataSet dataSet, List<Integer> clrs, int entryCount) {
    for (int j = 0; j < clrs.size() && j < entryCount; j++) {
        String label = (j < clrs.size() - 1 && j < entryCount - 1) ? null : dataSet.getLabel();
        computedEntries.add(new LegendEntry(label, dataSet.getForm(), dataSet.getFormSize(), dataSet.getFormLineWidth(), dataSet.getFormLineDashEffect(), clrs.get(j)));
    }
}