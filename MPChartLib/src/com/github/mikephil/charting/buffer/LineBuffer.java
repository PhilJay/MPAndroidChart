
package com.github.mikephil.charting.buffer;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class LineBuffer extends AbstractBuffer<Entry> {

    public LineBuffer(int size) {
        super((size < 4) ? 4 : size);
    }

    public void moveTo(float x, float y) {

        if (index != 0)
            return;

        buffer[index++] = x;
        buffer[index++] = y;

        // in case just one entry, this is overwritten when lineTo is called
        buffer[index] = x;
        buffer[index + 1] = y;
    }

    public void lineTo(float x, float y) {

        if (index == 2) {
            buffer[index++] = x;
            buffer[index++] = y;
        } else {

            float prevX = buffer[index - 2];
            float prevY = buffer[index - 1];
            buffer[index++] = prevX;
            buffer[index++] = prevY;
            buffer[index++] = x;
            buffer[index++] = y;
        }
    }

    @Override
    public void feed(List<Entry> entries) {
        moveTo(entries.get(mFrom).getXIndex(), entries.get(mFrom).getVal() * phaseY);

        int size = (int) Math.ceil((mTo - mFrom) * phaseX + mFrom);

        Log.i("BUFFER", "from: " + mFrom + ", to: " + mTo + ", size: " + size);

        for (int i = mFrom + 1; i < size; i++) {

            Entry e = entries.get(i);
            lineTo(e.getXIndex(), e.getVal() * phaseY);
        }

        reset();
    }
}
