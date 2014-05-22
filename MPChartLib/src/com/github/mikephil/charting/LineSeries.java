package com.github.mikephil.charting;

public class LineSeries extends Series {
    
    private int mLineIndex = 0;
    private int mXIndex = 0;

    public LineSeries(float val, int lineIndex, int xIndex) {
        super(val);
        mLineIndex = lineIndex;
        mXIndex = xIndex;
    }

    public int getLineIndex() {
        return mLineIndex;
    }
    
    public int getXIndex() {
        return mXIndex;
    }
}
