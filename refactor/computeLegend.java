/**
 * Prepares the legend and calculates all needed forms, labels and colors.
 *
 * @param data
 */
public void computeLegend(ChartData<?> data) {

    if (!mLegend.isLegendCustom()) {

        computedEntries.clear();

        // loop for building up the colors and labels used in the legend
        for (int i = 0; i < data.getDataSetCount(); i++) {

            IDataSet dataSet = data.getDataSetByIndex(i);
            if (dataSet == null) continue;

            List<Integer> clrs = dataSet.getColors();
            int entryCount = dataSet.getEntryCount();

            // if we have a barchart with stacked bars
            if (dataSet instanceof IBarDataSet && ((IBarDataSet) dataSet).isStacked()) {

                IBarDataSet bds = (IBarDataSet) dataSet;
                String[] sLabels = bds.getStackLabels();

                int minEntries = Math.min(clrs.size(), bds.getStackSize());

                for (int j = 0; j < minEntries; j++) {
                    String label;
                    if (sLabels.length > 0) {
                        int labelIndex = j % minEntries;
                        label = labelIndex < sLabels.length ? sLabels[labelIndex] : null;
                    } else {
                        label = null;
                    }

                    computedEntries.add(new LegendEntry(
                            label,
                            dataSet.getForm(),
                            dataSet.getFormSize(),
                            dataSet.getFormLineWidth(),
                            dataSet.getFormLineDashEffect(),
                            clrs.get(j)
                    ));
                }

                if (bds.getLabel() != null) {
                    // add the legend description label
                    computedEntries.add(new LegendEntry(
                            dataSet.getLabel(),
                            Legend.LegendForm.NONE,
                            Float.NaN,
                            Float.NaN,
                            null,
                            ColorTemplate.COLOR_NONE
                    ));
                }

            } else if (dataSet instanceof IPieDataSet) {

                IPieDataSet pds = (IPieDataSet) dataSet;

                for (int j = 0; j < clrs.size() && j < entryCount; j++) {

                    computedEntries.add(new LegendEntry(
                            pds.getEntryForIndex(j).getLabel(),
                            dataSet.getForm(),
                            dataSet.getFormSize(),
                            dataSet.getFormLineWidth(),
                            dataSet.getFormLineDashEffect(),
                            clrs.get(j)
                    ));
                }

                if (pds.getLabel() != null) {
                    // add the legend description label
                    computedEntries.add(new LegendEntry(
                            dataSet.getLabel(),
                            Legend.LegendForm.NONE,
                            Float.NaN,
                            Float.NaN,
                            null,
                            ColorTemplate.COLOR_NONE
                    ));
                }

            } else if (dataSet instanceof ICandleDataSet && ((ICandleDataSet) dataSet).getDecreasingColor() !=
                    ColorTemplate.COLOR_NONE) {

                int decreasingColor = ((ICandleDataSet) dataSet).getDecreasingColor();
                int increasingColor = ((ICandleDataSet) dataSet).getIncreasingColor();

                computedEntries.add(new LegendEntry(
                        null,
                        dataSet.getForm(),
                        dataSet.getFormSize(),
                        dataSet.getFormLineWidth(),
                        dataSet.getFormLineDashEffect(),
                        decreasingColor
                ));

                computedEntries.add(new LegendEntry(
                        dataSet.getLabel(),
                        dataSet.getForm(),
                        dataSet.getFormSize(),
                        dataSet.getFormLineWidth(),
                        dataSet.getFormLineDashEffect(),
                        increasingColor
                ));

            } else { // all others

                for (int j = 0; j < clrs.size() && j < entryCount; j++) {

                    String label;

                    // if multiple colors are set for a DataSet, group them
                    if (j < clrs.size() - 1 && j < entryCount - 1) {
                        label = null;
                    } else { // add label to the last entry
                        label = data.getDataSetByIndex(i).getLabel();
                    }

                    computedEntries.add(new LegendEntry(
                            label,
                            dataSet.getForm(),
                            dataSet.getFormSize(),
                            dataSet.getFormLineWidth(),
                            dataSet.getFormLineDashEffect(),
                            clrs.get(j)
                    ));
                }
            }
        }

        if (mLegend.getExtraEntries() != null) {
            Collections.addAll(computedEntries, mLegend.getExtraEntries());
        }

        mLegend.setEntries(computedEntries);
    }

    Typeface tf = mLegend.getTypeface();

    if (tf != null)
        mLegendLabelPaint.setTypeface(tf);

    mLegendLabelPaint.setTextSize(mLegend.getTextSize());
    mLegendLabelPaint.setColor(mLegend.getTextColor());

    // calculate all dimensions of the mLegend
    mLegend.calculateDimensions(mLegendLabelPaint, mViewPortHandler);
}