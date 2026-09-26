package com.github.mikephil.charting.components

import android.graphics.DashPathEffect
import com.github.mikephil.charting.utils.ColorTemplate

/**
 * One entry of a [Legend]: a form followed by a label. NaN sizes and a null dash effect fall back to the legend's values.
 * @property label the text, or null to draw only the form, stacked with the next entry.
 * @property form the shape drawn in front of the label; [Legend.LegendForm.DEFAULT] uses the legend's [Legend.form].
 * @property formSize size of the form in dp, or NaN for the legend's [Legend.formSize].
 * @property formLineWidth stroke width in dp of a line form, or NaN for the legend's [Legend.formLineWidth].
 * @property formLineDashEffect dash pattern of a line form, or null for the legend's [Legend.formLineDashEffect].
 * @property formColor color of the form; [ColorTemplate.COLOR_NONE], [ColorTemplate.COLOR_SKIP] and 0 draw no form.
 */
public class LegendEntry(
    public var label: String? = null,
    public var form: Legend.LegendForm = Legend.LegendForm.DEFAULT,
    public var formSize: Float = Float.NaN,
    public var formLineWidth: Float = Float.NaN,
    public var formLineDashEffect: DashPathEffect? = null,
    public var formColor: Int = ColorTemplate.COLOR_NONE
)
