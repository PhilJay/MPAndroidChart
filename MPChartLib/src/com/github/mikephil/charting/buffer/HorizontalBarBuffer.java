
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class HorizontalBarBuffer extends BarBuffer {

    public HorizontalBarBuffer(int size, float groupspace, int dataSetCount, boolean containsStacks) {
        super(size, groupspace, dataSetCount, containsStacks);
    }

    @Override
    public void feed(ArrayList<BarEntry> entries) {

        float size = entries.size() * phaseX;

        int dataSetOffset = (mDataSetCount - 1);
        float barSpaceHalf = mBarSpace / 2f;
        float groupSpaceHalf = mGroupSpace / 2f;
        float barWidth = 0.5f;

        for (int i = 0; i < size; i++) {

            BarEntry e = entries.get(i);

            // calculate the x-position, depending on datasetcount
            float x = e.getXIndex() + i * dataSetOffset + mDataSetIndex
                    + mGroupSpace * i + groupSpaceHalf;
            float y = e.getVal();
            float[] vals = e.getVals();

            if (!mContainsStacks || vals == null) {

                float bottom = x - barWidth + barSpaceHalf;
                float top = x + barWidth - barSpaceHalf;
                float right = y >= 0 ? y : 0;
                float left = y <= 0 ? y : 0;

                // multiply the height of the rect with the phase
                if (right > 0)
                    right *= phaseY;
                else
                    left *= phaseY;

                addBar(left, top, right, bottom);

            } else {

                float all = e.getVal();

                // fill the stack
                for (int k = 0; k < vals.length; k++) {

                    all -= vals[k];
                    y = vals[k] + all;

                    float bottom = x - barWidth + barSpaceHalf;
                    float top = x + barWidth - barSpaceHalf;
                    float right = y >= 0 ? y : 0;
                    float left = y <= 0 ? y : 0;

                    // multiply the height of the rect with the phase
                    if (right > 0)
                        right *= phaseY;
                    else
                        left *= phaseY;

                    addBar(left, top, right, bottom);
                }
            }
        }

        reset();
    }
}
