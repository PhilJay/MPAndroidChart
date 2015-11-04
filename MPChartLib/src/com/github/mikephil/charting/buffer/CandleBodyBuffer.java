
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;

public class CandleBodyBuffer extends AbstractBuffer<ICandleDataSet> {
    
    private float mBodySpace = 0f;

    public CandleBodyBuffer(int size) {
        super(size);
    }
    
    public void setBodySpace(float bodySpace) {
        this.mBodySpace = bodySpace;
    }

    private void addBody(float left, float top, float right, float bottom) {

        buffer[index++] = left;
        buffer[index++] = top;
        buffer[index++] = right;
        buffer[index++] = bottom;
    }

    @Override
    public void feed(ICandleDataSet data) {

        int size = (int)Math.ceil((mTo - mFrom) * phaseX + mFrom);

        for (int i = mFrom; i < size; i++) {

            CandleEntry e = data.getEntryForIndex(i);
            addBody(e.getXIndex() - 0.5f + mBodySpace, e.getClose() * phaseY, e.getXIndex() + 0.5f - mBodySpace, e.getOpen() * phaseY);
        }

        reset();
    }
}
