package com.github.mikephil.charting.data;

/**
 * Created by Philipp Jahoda on 03/04/16.
 */
public class XAxisValue {

    /**
     * the label that describes this value
     */
    private String mLabel = "";

    /**
     * the position of this value on the x-axis
     */
    private double mPosition;

    /**
     * Constructor only with label. This is relevant for pie and radarchart.
     *
     * @param label the x-axis label of this value
     */
    public XAxisValue(String label) {
        this.mLabel = label;
    }

    /**
     * Constructor.
     *
     * @param xPosition the position of this value on the x-axis
     * @param label     the x-axis label of this value
     */
    public XAxisValue(double xPosition, String label) {
        this.mLabel = label;
        this.mPosition = xPosition;
    }

    /**
     * Sets both x-position and label.
     *
     * @param xPosition
     * @param label
     */
    public void set(double xPosition, String label) {
        this.mLabel = label;
        this.mPosition = xPosition;
    }

    /**
     * Returns the position (x) of the value on the x-axis.
     *
     * @return
     */
    public double getPosition() {
        return mPosition;
    }

    /**
     * Returns the x-axis label of this value.
     *
     * @return
     */
    public String getLabel() {
        return mLabel;
    }
}
