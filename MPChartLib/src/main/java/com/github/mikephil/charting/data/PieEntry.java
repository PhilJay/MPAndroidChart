package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Created by Philipp Jahoda on 31/05/16.
 */
@SuppressLint("ParcelCreator")
public class PieEntry extends Entry {

    private String label;

    public PieEntry(float value) {
        super(0f, value);
    }

    public PieEntry(float value, Object data) {
        super(0f, value, data);
    }

    public PieEntry(float value, String label) {
        super(0f, value);
        this.label = label;
    }

    public PieEntry(float value, String label, Object data) {
        super(0f, value, data);
        this.label = label;
    }

    /**
     * This is the same as getY(). Returns the value of the PieEntry.
     *
     * @return
     */
    public float getValue() {
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
    public void setX(float x) {
        super.setX(x);
        Log.i("DEPRECATED", "Pie entries do not have x values");
    }

    @Deprecated
    @Override
    public float getX() {
        Log.i("DEPRECATED", "Pie entries do not have x values");
        return super.getX();
    }

    public PieEntry copy() {
        PieEntry e = new PieEntry(getY(), label, getData());
        return e;
    }
}
