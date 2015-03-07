
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class BarBuffer extends AbstractBuffer<BarEntry> {

    protected float mBarSpace = 0f;
    protected float mGroupSpace = 0f;
    protected int mDataSetIndex = 0;
    protected int mDataSetCount = 1;
    protected boolean mContainsStacks = false;

    public BarBuffer(int size, float groupspace, int dataSetCount, boolean containsStacks) {
        super(size);
        this.mGroupSpace = groupspace;
        this.mDataSetCount = dataSetCount;
        this.mContainsStacks = containsStacks;
    }

    public void setBarSpace(float barspace) {
        this.mBarSpace = barspace;
    }

    public void setDataSet(int index) {
        this.mDataSetIndex = index;
    }

    protected void addBar(float left, float top, float right, float bottom) {

        buffer[index++] = left;
        buffer[index++] = top;
        buffer[index++] = right;
        buffer[index++] = bottom;
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
            float [] vals = e.getVals();
            
            if(!mContainsStacks || vals == null) {
                
                float left = x - barWidth + barSpaceHalf;
                float right = x + barWidth - barSpaceHalf;
                float top = y >= 0 ? y : 0;
                float bottom = y <= 0 ? y : 0;

                // multiply the height of the rect with the phase
                if (top >= 0)
                    top *= phaseY;
                else
                    bottom *= phaseY;

                addBar(left, top, right, bottom);
                
            } else {
                
                float all = e.getVal();

                // fill the stack
                for (int k = 0; k < vals.length; k++) {

                    all -= vals[k];
                    y = vals[k] + all;

                    float left = x - barWidth + barSpaceHalf;
                    float right = x + barWidth - barSpaceHalf;
                    float top = y >= 0 ? y : 0;
                    float bottom = y <= 0 ? y : 0;

                    // multiply the height of the rect with the phase
                    if (top >= 0)
                        top *= phaseY;
                    else
                        bottom *= phaseY;

                    addBar(left, top, right, bottom);
                }
            }           
        }

        reset();
    }
}
