
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.CandleEntry;

import java.util.List;

public class CandleBodyBuffer extends AbstractBuffer<CandleEntry> {
    
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

    private int mFrom = 0;
    private int mTo = 0;

    public void limitFrom(int from)
    {
        mFrom = from;
    }

    public void limitTo(int to)
    {
        mTo = to;
    }

    @Override
    public void feed(List<CandleEntry> entries) {

        int size = (int)Math.ceil((mTo - mFrom) * phaseX + mFrom);

        for (int i = mFrom; i < size; i++) {

            CandleEntry e = entries.get(i);
            addBody(e.getXIndex() - 0.5f + mBodySpace, e.getClose() * phaseY, e.getXIndex() + 0.5f - mBodySpace, e.getOpen() * phaseY);
        }

        reset();
    }
}
