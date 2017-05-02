package com.xxmassdeveloper.mpchartexample.agiplan;

import com.github.mikephil.charting.data.PieEntry;

/**
 * Created by vsossella on 07/02/17.
 */

public class InvestmentPieEntry extends PieEntry {

    private String description;
    private float percentage;
    private Integer color;

    public InvestmentPieEntry(float value, String description, float percentage, Integer color) {
        super(value, "");
        this.description = description;
        this.percentage = percentage;
        this.color = color;
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

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}