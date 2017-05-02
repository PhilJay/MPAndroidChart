package com.github.mikephil.charting.agiplan;

import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.data.PieEntry;

/**
 * Created by vsossella on 07/02/17.
 */

public class InvestmentPieEntry extends PieEntry {

    private String description;
    private float percentage;
    private Integer color;
    private Drawable circle;

    public InvestmentPieEntry(float value, String description, float percentage, Integer color, Drawable circle) {
        super(value, "");
        this.description = description;
        this.percentage = percentage;
        this.color = color;
        this.circle = circle;
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

    public Drawable getCircle() {
        return circle;
    }

    public void setCircle(Drawable circle) {
        this.circle = circle;
    }
}