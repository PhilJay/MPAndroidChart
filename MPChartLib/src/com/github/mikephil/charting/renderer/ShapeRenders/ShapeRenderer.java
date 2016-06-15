package com.github.mikephil.charting.renderer.ShapeRenders;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.buffer.ScatterBuffer;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:07
 */
public interface ShapeRenderer {

     void renderShape(
             Canvas c, IScatterDataSet dataSet,
             ViewPortHandler mViewPortHandler, ScatterBuffer buffer, Paint mRenderPaint, final int shapeHoleColor, final float shapeSize);



}
