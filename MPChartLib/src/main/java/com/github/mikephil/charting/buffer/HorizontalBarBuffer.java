
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

public class HorizontalBarBuffer extends BarBuffer {

    public HorizontalBarBuffer(int size, float groupspace, int dataSetCount, boolean containsStacks) {
        super(size, groupspace, dataSetCount, containsStacks);
    }

    @Override
    public void feed(IBarDataSet data) {

        float size = data.getEntryCount() * phaseX;
        float barWidthHalf = mBarWidth / 2f;

        for (int i = 0; i < size; i++) {

            BarEntry e = data.getEntryForIndex(i);

            if(e == null)
                continue;

            float x = e.getX();
            float y = e.getY();
            float[] vals = e.getYVals();

            if (!mContainsStacks || vals == null) {

                float top = x - barWidthHalf;
                float bottom = x + barWidthHalf;
                float left, right;
                if (mInverted) {
                    left = y >= 0 ? y : 0;
                    right = y <= 0 ? y : 0;
                } else {
                    right = y >= 0 ? y : 0;
                    left = y <= 0 ? y : 0;
                }

                // multiply the height of the rect with the phase
                if (right > 0)
                    right *= phaseY;
                else
                    left *= phaseY;

                addBar(left, top, right, bottom);

            } else {

                float posY = 0f;
                float negY = -e.getNegativeSum();
                float yStart = 0f;

                // fill the stack
                for (int k = 0; k < vals.length; k++) {

                    float value = vals[k];

                    if (value >= 0f) {
                        y = posY;
                        yStart = posY + value;
                        posY = yStart;
                    } else {
                        y = negY;
                        yStart = negY + Math.abs(value);
                        negY += Math.abs(value);
                    }

                    float top = x - barWidthHalf;
                    float bottom = x + barWidthHalf;
                    float left, right;
                    if (mInverted) {
                        left = y >= yStart ? y : yStart;
                        right = y <= yStart ? y : yStart;
                    } else {
                        right = y >= yStart ? y : yStart;
                        left = y <= yStart ? y : yStart;
                    }

                    // multiply the height of the rect with the phase
                    right *= phaseY;
                    left *= phaseY;

                    addBar(left, top, right, bottom);
                }
            }
        }

        reset();
    }
}
