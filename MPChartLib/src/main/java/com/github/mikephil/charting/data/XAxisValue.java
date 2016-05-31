package com.github.mikephil.charting.data;

/**
 * Created by Philipp Jahoda on 03/04/16.
 */
public class XAxisValue {

    /**
     * the label that describes this yValue
     */
    private String mLabel = "";

    /**
     * the position of this yValue on the xPx-axis
     */
    private double mPosition;

    /**
     * Constructor only with label. This is relevant for pie and radarchart.
     *
     * @param label the xPx-axis label of this yValue
     */
    public XAxisValue(String label) {
        this.mLabel = label;
    }

    /**
     * Constructor.
     *
     * @param xPosition the position of this yValue on the xPx-axis
     * @param label     the xPx-axis label of this yValue
     */
    public XAxisValue(double xPosition, String label) {
        this.mLabel = label;
        this.mPosition = xPosition;
    }

    /**
     * Sets both xPx-position and label.
     *
     * @param xPosition
     * @param label
     */
    public void set(double xPosition, String label) {
        this.mLabel = label;
        this.mPosition = xPosition;
    }

    /**
     * Returns the position (xPx) of the yValue on the xPx-axis.
     *
     * @return
     */
    public double getPosition() {
        return mPosition;
    }

    /**
     * Returns the xPx-axis label of this yValue.
     *
     * @return
     */
    public String getLabel() {
        return mLabel;
    }
}
