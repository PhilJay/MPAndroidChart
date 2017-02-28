package com.github.mikephil.charting.components;


import android.graphics.DashPathEffect;

import com.github.mikephil.charting.utils.ColorTemplate;

public class LegendEntry {
    public LegendEntry() {

    }

    /**
     * @param label              The legend entry text. A `null` label will start a group.
     * @param form               The form to draw for this entry.
     * @param formSize           Set to NaN to use the legend's default.
     * @param formLineWidth      Set to NaN to use the legend's default.
     * @param formLineDashEffect Set to nil to use the legend's default.
     * @param formColor          The color for drawing the form.
     */
    public LegendEntry(String label,
                       Legend.LegendForm form,
                       float formSize,
                       float formLineWidth,
                       DashPathEffect formLineDashEffect,
                       int formColor) {
        this.label = label;
        this.form = form;
        this.formSize = formSize;
        this.formLineWidth = formLineWidth;
        this.formLineDashEffect = formLineDashEffect;
        this.formColor = formColor;
    }

    /**
     * @param label     The legend entry text. A `null` label will start a group.
     * @param formColor The color for drawing the form.
     */
    public LegendEntry(String label, int formColor) {
        this.label = label;
        this.formColor = formColor;
    }

    /**
     * The legend entry text.
     * A `null` label will start a group.
     */
    public String label;

    /**
     * The form to draw for this entry.
     * <p>
     * `NONE` will avoid drawing a form, and any related space.
     * `EMPTY` will avoid drawing a form, but keep its space.
     * `DEFAULT` will use the Legend's default.
     */
    public Legend.LegendForm form = Legend.LegendForm.DEFAULT;

    /**
     * Form size will be considered except for when .None is used
     * <p>
     * Set as NaN to use the legend's default
     */
    public float formSize = Float.NaN;

    /**
     * Line width used for shapes that consist of lines.
     * <p>
     * Set as NaN to use the legend's default
     */
    public float formLineWidth = Float.NaN;

    /**
     * Line dash path effect used for shapes that consist of lines.
     * <p>
     * Set to null to use the legend's default
     */
    public DashPathEffect formLineDashEffect = null;

    /**
     * The color for drawing the form
     */
    public int formColor = ColorTemplate.COLOR_NONE;

}