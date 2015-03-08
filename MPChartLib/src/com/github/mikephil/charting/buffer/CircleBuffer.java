
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class CircleBuffer extends AbstractBuffer<Entry> {

    public CircleBuffer(int size) {
        super(size);
    }

    protected void addCircle(float x, float y) {
        buffer[index++] = x;
        buffer[index++] = y;
    }

    @Override
    public void feed(ArrayList<Entry> entries) {
        
        float size = entries.size() * phaseX;

        for (int i = 0; i < size; i++) {

            Entry e = entries.get(i);
            addCircle(e.getXIndex(), e.getVal() * phaseY);
        }
        
        reset();
    }
}
