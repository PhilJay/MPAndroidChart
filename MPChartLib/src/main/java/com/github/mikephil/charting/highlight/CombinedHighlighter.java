package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.utils.SelectionDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 12/09/15.
 */
public class CombinedHighlighter extends ChartHighlighter<BarLineScatterCandleBubbleDataProvider> implements Highlighter {

    protected BarHighlighter barHighlighter;

    public CombinedHighlighter(BarLineScatterCandleBubbleDataProvider chart, BarDataProvider barChart) {
        super(chart);
        barHighlighter = new BarHighlighter(barChart);
    }

    @Override
    public Highlight getHighlight(float x, float y) {

        Highlight h1 = super.getHighlight(x, y);
        Highlight h2 = barHighlighter.getHighlight(x, y);

        return getClosest(x, y, h1, h2);
    }

    protected Highlight getClosest(float x, float y, Highlight... highs) {
        return null;
    }

    @Override
    protected List<SelectionDetail> getSelectionDetailsAtXPos(float xVal) {

        List<SelectionDetail> vals = new ArrayList<SelectionDetail>();

        CombinedData data = (CombinedData) mChart.getData();

        // get all chartdata objects
        List<ChartData> dataObjects = data.getAllData();

        for (int i = 0; i < dataObjects.size(); i++) {

            for (int j = 0, dataSetCount = dataObjects.get(i).getDataSetCount(); j < dataSetCount; j++) {

                IDataSet dataSet = dataObjects.get(i).getDataSetByIndex(j);

                // don't include datasets that cannot be highlighted
                if (!dataSet.isHighlightEnabled())
                    continue;

                SelectionDetail s1 = getDetail(dataSet, j, xVal, DataSet.Rounding.UP);
                s1.dataIndex = i;
                vals.add(s1);

                SelectionDetail s2 = getDetail(dataSet, j, xVal, DataSet.Rounding.DOWN);
                s2.dataIndex = i;
                vals.add(s2);
            }
        }

        return vals;
    }
}
