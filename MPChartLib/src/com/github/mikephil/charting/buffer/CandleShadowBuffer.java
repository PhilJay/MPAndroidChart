
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.CandleEntry;

import java.util.List;

public class CandleShadowBuffer extends AbstractBuffer<CandleEntry> {

    public CandleShadowBuffer(int size) {
        super(size);
    }

    private void addShadow(float x1, float y1, float x2, float y2) {

        buffer[index++] = x1;
        buffer[index++] = y1;
        buffer[index++] = x2;
        buffer[index++] = y2;
    }

    @Override
    public void feed(List<CandleEntry> entries) {
        
        float size = entries.size() * phaseX;

        for (int i = 0; i < size; i++) {

            CandleEntry e = entries.get(i);
            addShadow(e.getXIndex(), e.getHigh() * phaseY, e.getXIndex(), e.getLow() * phaseY);
        }

        reset();
    }
}
