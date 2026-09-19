package com.xxmassdeveloper.mpchartexample.design

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.xxmassdeveloper.mpchartexample.R

/** A rounded label above the highlighted entry, filled with the theme's pill colors. */
@SuppressLint("ViewConstructor")
class PillMarkerView(
    context: Context,
    theme: Nightfall,
    private val label: (Entry<*>) -> String,
) : MarkerView(context, R.layout.marker_pill) {

    private val text: TextView = findViewById(R.id.tvPill)

    init {
        text.setTextColor(theme.pillText)
        text.background = GradientDrawable().apply {
            cornerRadius = Utils.convertDpToPixel(8f)
            setColor(theme.pillBackground)
        }
    }

    override fun refreshContent(e: Entry<*>, highlight: Highlight) {
        text.text = label(e)
        super.refreshContent(e, highlight)
    }

    override var offset: MPPointF
        get() = MPPointF(-(width / 2f), -height - Utils.convertDpToPixel(14f))
        set(value) {
            super.offset = value
        }
}
