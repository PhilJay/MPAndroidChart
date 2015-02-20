
package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * Data object that allows the combination of Line-, Bar-, Scatter- and
 * CandleData. Used in the CombinedChart class.
 * 
 * @author Philipp Jahoda
 */
public class CombinedData extends BarLineScatterCandleData<BarLineScatterCandleDataSet<?>> {

    private LineData mLineData;
    private BarData mBarData;
    private ScatterData mScatterData;
    private CandleData mCandleData;

    public CombinedData() {
        super();
    }

    public CombinedData(ArrayList<String> xVals) {
        super(xVals);
    }

    public CombinedData(String[] xVals) {
        super(xVals);
    }

    public void setData(LineData data) {
        mLineData = data;
        mDataSets.addAll(data.getDataSets());
        init(data.getDataSets());
    }

    public void setData(BarData data) {
        mBarData = data;
        mDataSets.addAll(data.getDataSets());
        init(data.getDataSets());
    }

    public void setData(ScatterData data) {
        mScatterData = data;
        mDataSets.addAll(data.getDataSets());
        init(data.getDataSets());
    }

    public void setData(CandleData data) {
        mCandleData = data;
        mDataSets.addAll(data.getDataSets());
        init(data.getDataSets());
    }

    public LineData getLineData() {
        return mLineData;
    }

    public BarData getBarData() {
        return mBarData;
    }

    public ScatterData getScatterData() {
        return mScatterData;
    }

    public CandleData getCandleData() {
        return mCandleData;
    }

    @Override
    public void notifyDataChanged() {
        mLineData.notifyDataChanged();
        mBarData.notifyDataChanged();
        mCandleData.notifyDataChanged();
        mScatterData.notifyDataChanged();
    }
}
