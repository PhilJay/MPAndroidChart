
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class HorizontalBarBuffer extends BarBuffer {

    public HorizontalBarBuffer(int size, float groupspace, int dataSetCount, boolean containsStacks) {
        super(size, groupspace, dataSetCount, containsStacks);
    }

    @Override
    public void feed(List<BarEntry> entries) {

        float size = entries.size() * phaseX;

        int dataSetOffset = (mDataSetCount - 1);
        float barSpaceHalf = mBarSpace / 2f;
        float groupSpaceHalf = mGroupSpace / 2f;
        float barWidth = 0.5f;

        for (int i = 0; i < size; i++) {

            BarEntry e = entries.get(i);

            // calculate the x-position, depending on datasetcount
            float x = e.getXIndex() + e.getXIndex() * dataSetOffset + mDataSetIndex
                    + mGroupSpace * e.getXIndex() + groupSpaceHalf;
            float y = e.getVal();
            float[] vals = e.getVals();

            if (!mContainsStacks || vals == null) {

                float bottom = x - barWidth + barSpaceHalf;
                float top = x + barWidth - barSpaceHalf;
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

                    if(value >= 0f) {
                        y = posY;
                        yStart = posY + value;
                        posY = yStart;
                    } else {
                        y = negY;
                        yStart = negY + Math.abs(value);
                        negY += Math.abs(value);
                    }

                    float bottom = x - barWidth + barSpaceHalf;
                    float top = x + barWidth - barSpaceHalf;
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
