package com.github.mikephil.charting.renderer

import com.github.mikephil.charting.utils.ViewPortHandler

abstract class Renderer(
    /**
     * the component that handles the drawing area of the chart and it's offsets
     */
    @JvmField
    protected var viewPortHandler: ViewPortHandler
)
