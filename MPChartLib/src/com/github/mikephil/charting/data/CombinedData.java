
package com.github.mikephil.charting.data;

import java.util.List;

/**
 * Data object that allows the combination of Line-, Bar-, Scatter-, Bubble- and
 * CandleData. Used in the CombinedChart class.
 * 
 * @author Philipp Jahoda
 */
public class CombinedData extends BarLineScatterCandleData<BarLineScatterCandleDataSet<?>> {

    private LineData mLineData;
    private BarData mBarData;
    private ScatterData mScatterData;
    private CandleData mCandleData;
    private BubbleData mBubbleData;

    public CombinedData() {
        super();
    }

    public CombinedData(List<String> xVals) {
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

    public void setData(BubbleData data) {
        mBubbleData = data;
        mDataSets.addAll(data.getDataSets());
        init(data.getDataSets());
    }

    public BubbleData getBubbleData() {
        return mBubbleData;
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
        if (mLineData != null)
            mLineData.notifyDataChanged();
        if (mBarData != null)
            mBarData.notifyDataChanged();
        if (mCandleData != null)
            mCandleData.notifyDataChanged();
        if (mScatterData != null)
            mScatterData.notifyDataChanged();
        if (mBubbleData != null)
            mBubbleData.notifyDataChanged();
    }
}
