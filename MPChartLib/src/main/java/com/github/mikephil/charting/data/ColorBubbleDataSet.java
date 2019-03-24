package com.github.mikephil.charting.data;

import java.util.ArrayList;
import java.util.List;

public class ColorBubbleDataSet extends BubbleDataSet {
    public ColorBubbleDataSet(List<ColorBubbleEntry> yVals, String label) {
        super( (List<BubbleEntry>) (List<?>) yVals, label);
    }

    @Override
    protected void copy(BarLineScatterCandleBubbleDataSet barLineScatterCandleBubbleDataSet) {
        super.copy(barLineScatterCandleBubbleDataSet);
        copyFieldsFrom((BubbleDataSet) barLineScatterCandleBubbleDataSet);
    }

    @Override
    public DataSet<BubbleEntry> copy() {
        List<ColorBubbleEntry> entries = new ArrayList<>();
        for (int i = 0; i < mValues.size(); i++) {
            entries.add((ColorBubbleEntry) mValues.get(i).copy());
        }
        BubbleDataSet copy = new ColorBubbleDataSet(entries, getLabel());
        copy.copyFieldsFrom(this);
        return copy;
    }
}

