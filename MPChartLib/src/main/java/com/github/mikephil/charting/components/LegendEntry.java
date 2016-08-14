package com.github.mikephil.charting.components;


import com.github.mikephil.charting.utils.ColorTemplate;

public class LegendEntry {
    public LegendEntry() {

    }

    /**
     *
     * @param label The legend entry text. A `null` label will start a group.
     * @param form The form to draw for this entry.
     * @param formSize Set as NaN to use the legend's default
     * @param formLineWidth Set as NaN to use the legend's default
     * @param formColor The color for drawing the form
     */
    public LegendEntry(String label,
                       Legend.LegendForm form,
                       float formSize,
                       float formLineWidth,
                       int formColor)
    {
        this.label = label;
        this.form = form;
        this.formSize = formSize;
        this.formLineWidth = formLineWidth;
        this.formColor = formColor;
    }

    /**
     * The legend entry text.
     * A `null` label will start a group.
     */
    public String label;

    /**
     * The form to draw for this entry.
     *
     * `NONE` will avoid drawing a form, and any related space.
     * `EMPTY` will avoid drawing a form, but keep its space.
     * `DEFAULT` will use the Legend's default.
     */
    public Legend.LegendForm form = Legend.LegendForm.DEFAULT;

    /**
     * Form size will be considered except for when .None is used
     *
     * Set as NaN to use the legend's default
     */
    public float formSize = Float.NaN;

    /**
     * Line width used for shapes that consist of lines.
     *
     * Set as NaN to use the legend's default
     */
    public float formLineWidth = Float.NaN;

    /**
     * The color for drawing the form
     */
    public int formColor = ColorTemplate.COLOR_NONE;

}