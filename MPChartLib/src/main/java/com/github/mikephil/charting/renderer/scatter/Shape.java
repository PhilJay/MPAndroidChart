package com.github.mikephil.charting.renderer.scatter;

import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.Utils;

public class Shape {
    private float shapeSize;
    private float shapeHalf;
    private float shapeHoleSizeHalf;
    private float shapeHoleSize;
    private float shapeStrokeSize;
    private float shapeStrokeSizeHalf;
    private int shapeHoleColor;

    public Shape(IScatterDataSet dataSet) {
        this.shapeSize = dataSet.getScatterShapeSize();
        this.shapeHalf = shapeSize / 2f;
        this.shapeHoleSizeHalf = Utils.convertDpToPixel(dataSet.getScatterShapeHoleRadius());
        this.shapeHoleSize = shapeHoleSizeHalf * 2.f;
        this.shapeStrokeSize = (shapeSize - shapeHoleSize) / 2.f;
        this.shapeStrokeSizeHalf = shapeStrokeSize / 2.f;
        this.shapeHoleColor = dataSet.getScatterShapeHoleColor();
    }

    public float getShapeSize() {
        return shapeSize;
    }

    public float getShapeHalf() {
        return shapeHalf;
    }

    public float getShapeHoleSizeHalf() {
        return shapeHoleSizeHalf;
    }

    public float getShapeHoleSize() {
        return shapeHoleSize;
    }

    public float getShapeStrokeSize() {
        return shapeStrokeSize;
    }

    public float getShapeStrokeSizeHalf() {
        return shapeStrokeSizeHalf;
    }

    public int getShapeHoleColor() {
        return shapeHoleColor;
    }
}
