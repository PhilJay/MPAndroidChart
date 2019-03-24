package com.github.mikephil.charting.data;

import android.graphics.drawable.Drawable;

public class ColorBubbleEntry extends BubbleEntry {

    private int mColor;


    public ColorBubbleEntry(float x, float y, float size, Drawable icon, Object data, int color) {
        super(x, y, size, icon, data);
        this.mColor = color;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    public int getAlpha() {
        return (mColor & 0xff000000);
    }

    public void setAlpha(int alpha) {
        if (alpha < 0 || alpha > 0xff)
            alpha = 0xff;
        int was = mColor & 0xffffff;
        int to = alpha << 24;
        mColor = was | to;
    }

    @Override
    public ColorBubbleEntry copy() {
        return new ColorBubbleEntry(getX(), getY(), getSize(), getIcon(), getData(), getColor());
    }

    @Override
    public String toString() {
        return super.toString() + ", c= 0x" + Integer.toHexString(mColor);
    }
}
