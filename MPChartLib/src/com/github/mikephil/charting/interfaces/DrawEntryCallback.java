package com.github.mikephil.charting.interfaces;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface DrawEntryCallback {
    void drawEntry(Canvas canvas, float posX, float posY, Paint paint);
}
