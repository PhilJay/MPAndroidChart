
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class LineBuffer extends AbstractBuffer<Entry> {

    private boolean mSteppedEnabled = false;

    public void setSteppedEnabled(boolean enabled) {
        mSteppedEnabled = enabled;
    }

    public boolean isSteppedEnabled() {
        return mSteppedEnabled;
    }

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
        moveTo(entries.get(0).getXIndex(), entries.get(0).getVal());

        float size = entries.size() * phaseX;

        if(mSteppedEnabled == true) {
            lineTo(entries.get(1).getXIndex(), entries.get(0).getVal());
            for (int i = 1; i < size; i++) {

                Entry e = entries.get(i);
                lineTo(e.getXIndex(), e.getVal() * phaseY);

                if(i < size - 1) {
                    Entry next = entries.get(i + 1);
                    lineTo(next.getXIndex(), e.getVal() * phaseY);
                }
            }
        } else {
            for (int i = 1; i < size; i++) {

                Entry e = entries.get(i);
                lineTo(e.getXIndex(), e.getVal() * phaseY);
            }
        }

        reset();
    }
}
