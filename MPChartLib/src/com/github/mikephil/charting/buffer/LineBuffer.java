
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class LineBuffer extends AbstractBuffer<ILineDataSet> {

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
    public void feed(ILineDataSet data) {
        moveTo(data.getEntryForIndex(mFrom).getXIndex(), data.getEntryForIndex(mFrom).getVal() * phaseY);

        int size = (int) Math.ceil((mTo - mFrom) * phaseX + mFrom);
        int from = mFrom + 1;

        for (int i = from; i < size; i++) {

            Entry e = data.getEntryForIndex(i);
            lineTo(e.getXIndex(), e.getVal() * phaseY);
        }

        reset();
    }
}
