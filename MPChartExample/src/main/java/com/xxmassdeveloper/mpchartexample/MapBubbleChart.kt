package com.xxmassdeveloper.mpchartexample

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet

import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.components.YAxis
import com.xxmassdeveloper.mpchartexample.custom.renderer.CityRenderer
import kotlin.math.max

class MapBubbleChart : BubbleChart {
    protected val bounds = RectF()
    protected var minPopulation = 0
    protected var maxPopulation = 0

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun init() {
        super.init()
        mRenderer = CityRenderer(this, mAnimator, mViewPortHandler)
    }

    fun setBounds(b: RectF, minPopulation: Int, maxPopulation: Int) {
        bounds.set(b)
        this.minPopulation = minPopulation
        this.maxPopulation = maxPopulation
    }
}
