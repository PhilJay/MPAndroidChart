package com.xxmassdeveloper.mpchartexample.agiplan;

import com.github.mikephil.charting.data.PieEntry;

/**
 * Created by vsossella on 07/02/17.
 */

public class InvestmentPieEntry extends PieEntry {

    private String description;
    private float percentage;

    public InvestmentPieEntry(float value, String description, float percentage) {
        super(value, "");
        this.description = description;
        this.percentage = percentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }
}