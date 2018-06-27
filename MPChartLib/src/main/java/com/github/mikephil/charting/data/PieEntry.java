package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author Philipp Jahoda
 */
@SuppressLint("ParcelCreator")
public class PieEntry extends Entry {

    private String label;

    public PieEntry(double value) {
        super(0f, value);
    }

    public PieEntry(double value, Object data) {
        super(0, value, data);
    }

    public PieEntry(double value, Drawable icon) {
        super(0, value, icon);
    }

    public PieEntry(double value, Drawable icon, Object data) {
        super(0, value, icon, data);
    }

    public PieEntry(double value, String label) {
        super(0f, value);
        this.label = label;
    }

    public PieEntry(double value, String label, Object data) {
        super(0f, value, data);
        this.label = label;
    }

    public PieEntry(double value, String label, Drawable icon) {
        super(0f, value, icon);
        this.label = label;
    }

    public PieEntry(double value, String label, Drawable icon, Object data) {
        super(0f, value, icon, data);
        this.label = label;
    }

    /**
     * This is the same as getY(). Returns the value of the PieEntry.
     *
     * @return
     */
    public double getValue() {
        return getY();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Deprecated
    @Override
    public void setX(double x) {
        super.setX(x);
        Log.i("DEPRECATED", "Pie entries do not have x values");
    }

    @Deprecated
    @Override
    public double getX() {
        Log.i("DEPRECATED", "Pie entries do not have x values");
        return super.getX();
    }

    public PieEntry copy() {
        PieEntry e = new PieEntry(getY(), label, getData());
        return e;
    }
}
