
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class ScatterBuffer extends AbstractBuffer<Entry> {
    
    public ScatterBuffer(int size) {
        super(size);
    }

    protected void addTwo(float x, float y) {
        buffer[index++] = x;
        buffer[index++] = y;
    }

    @Override
    public void feed(ArrayList<Entry> entries) {
        
        float size = entries.size() * phaseX;
        
        for (int i = 0; i < size; i++) {

            Entry e = entries.get(i);
            addTwo(e.getXIndex(), e.getVal() * phaseY);
        }
        
        reset();
    }
}
