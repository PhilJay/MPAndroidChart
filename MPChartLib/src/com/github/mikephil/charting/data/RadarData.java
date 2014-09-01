
package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * Data container for the RadarChart.
 * 
 * @author Philipp Jahoda
 */
public class RadarData extends BarLineScatterCandleRadarData {

    public RadarData(ArrayList<String> xVals, ArrayList<RadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(String[] xVals, ArrayList<RadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(ArrayList<String> xVals, RadarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public RadarData(String[] xVals, RadarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
}
