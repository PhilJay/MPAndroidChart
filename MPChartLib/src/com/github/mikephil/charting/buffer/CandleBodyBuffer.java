
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;

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

    @Override
    public void feed(ArrayList<CandleEntry> entries) {
        
        float size = entries.size() * phaseX;

        for (int i = 0; i < size; i++) {

            CandleEntry e = entries.get(i);
            addBody(e.getXIndex() - 0.5f + mBodySpace, e.getClose() * phaseY, e.getXIndex() + 0.5f - mBodySpace, e.getOpen() * phaseY);
        }

        reset();
    }
}
