package com.github.mikephil.charting.agiplan;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;

import com.github.mikephil.charting.data.PieEntry;

/**
 * Created by vsossella on 07/02/17.
 */

public class InvestmentPieEntry extends PieEntry {

    private SpannableString detail;
    private Integer color;
    private Drawable circle;

    public InvestmentPieEntry(float value, SpannableString detail, Integer color, Drawable circle) {
        super(value, "");
        this.detail = detail;
        this.color = color;
        this.circle = circle;
    }

    public SpannableString getDetail() {
        return detail;
    }

    public void setDetail(SpannableString detail) {
        this.detail = detail;
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