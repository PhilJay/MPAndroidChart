
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * DataSet for the CandleStickChart.
 * 
 * @author Philipp Jahoda
 */
public class CandleDataSet extends BarLineScatterCandleDataSet {

    /** the width of the shadow of the candle */
    private float mShadowWidth = 3f;

    /** the space between the candle entries, default 0.2f (20%) */
    private float mBodySpace = 0.1f;

    public CandleDataSet(ArrayList<CandleEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public DataSet copy() {

        ArrayList<CandleEntry> yVals = new ArrayList<CandleEntry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(((CandleEntry) mYVals.get(i)).copy());
        }

        CandleDataSet copied = new CandleDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mShadowWidth = mShadowWidth;
        copied.mBodySpace = mBodySpace;
        copied.mHighLightColor = mHighLightColor;
        
        return copied;
    }
    
    @Override
    protected void calcMinMax() {
//        super.calcMinMax();
        
        if (mYVals.size() == 0) {
            return;
        }
        
        ArrayList<CandleEntry> entries = (ArrayList<CandleEntry>) mYVals;

        mYMin = entries.get(0).getLow();
        mYMax = entries.get(0).getHigh();

        for (int i = 0; i < entries.size(); i++) {

            CandleEntry e = entries.get(i);

            if (e.getLow() < mYMin)
                mYMin = e.getLow();

            if (e.getHigh() > mYMax)
                mYMax = e.getHigh();
        }
    }

    /**
     * Sets the space that is left out on the left and right side of each
     * candle, default 0.1f (10%), max 0.45f, min 0f
     * 
     * @param space
     */
    public void setBodySpace(float space) {

        if (space < 0f)
            space = 0f;
        if (space > 0.45f)
            space = 0.45f;

        mBodySpace = space;
    }

    /**
     * Returns the space that is left out on the left and right side of each
     * candle.
     * 
     * @return
     */
    public float getBodySpace() {
        return mBodySpace;
    }

    /**
     * Sets the width of the candle-shadow-line in pixels. Default 3f.
     * 
     * @param width
     */
    public void setShadowWidth(float width) {
        mShadowWidth = Utils.convertDpToPixel(width);
    }

    /**
     * Returns the width of the candle-shadow-line in pixels.
     * 
     * @return
     */
    public float getShadowWidth() {
        return mShadowWidth;
    }
}
