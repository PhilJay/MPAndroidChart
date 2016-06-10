package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 12/09/15.
 */
public class CombinedHighlighter extends ChartHighlighter<BarLineScatterCandleBubbleDataProvider> implements Highlighter {

    /**
     * bar highlighter for supporting stacked highlighting
     */
    protected BarHighlighter barHighlighter;

    public CombinedHighlighter(BarLineScatterCandleBubbleDataProvider chart, BarDataProvider barChart) {
        super(chart);
        barHighlighter = barChart.getBarData() == null ? null : new BarHighlighter(barChart);
    }

    @Override
    public Highlight getHighlight(float x, float y) {

        Highlight h1 = super.getHighlight(x, y);
        Highlight h2 = barHighlighter == null ? null : barHighlighter.getHighlight(x, y);

        return (h2 != null && h2.isStacked()) ? h2 : h1;
    }

//    protected Highlight getClosest(float x, float y, Highlight... highs) {
//
//        Highlight closest = null;
//        float minDistance = Float.MAX_VALUE;
//
//        for (Highlight high : highs) {
//
//            float tempDistance = getDistance(x, y, high.getXPx(), high.getYPx());
//
//            if (tempDistance < minDistance) {
//                minDistance = tempDistance;
//                closest = high;
//            }
//        }
//
//        return closest;
//    }

    @Override
    protected List<Highlight> getHighlightsAtXPos(float xVal) {

        List<Highlight> vals = new ArrayList<Highlight>();

        CombinedData data = (CombinedData) mChart.getData();

        // get all chartdata objects
        List<ChartData> dataObjects = data.getAllData();

        for (int i = 0; i < dataObjects.size(); i++) {

            for (int j = 0, dataSetCount = dataObjects.get(i).getDataSetCount(); j < dataSetCount; j++) {

                IDataSet dataSet = dataObjects.get(i).getDataSetByIndex(j);

                // don't include datasets that cannot be highlighted
                if (!dataSet.isHighlightEnabled())
                    continue;

                Highlight s1 = buildHighlight(dataSet, j, xVal, DataSet.Rounding.UP);
                s1.setDataIndex(i);
                vals.add(s1);

                Highlight s2 = buildHighlight(dataSet, j, xVal, DataSet.Rounding.DOWN);
                s2.setDataIndex(i);
                vals.add(s2);
            }
        }

        return vals;
    }
}
