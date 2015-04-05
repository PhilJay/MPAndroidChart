
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class CircleBuffer extends AbstractBuffer<Entry> {

    public CircleBuffer(int size) {
        super(size);
    }

    protected void addCircle(float x, float y) {
        buffer[index++] = x;
        buffer[index++] = y;
    }

    @Override
    public void feed(List<Entry> entries) {

        int size = (int)Math.ceil((mTo - mFrom) * phaseX + mFrom);

        for (int i = mFrom; i < size; i++) {

            Entry e = entries.get(i);
            addCircle(e.getXIndex(), e.getVal() * phaseY);
        }
        
        reset();
    }
}
