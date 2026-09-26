package com.github.mikephil.charting.renderer

import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * Base class of everything that draws onto a chart canvas.
 *
 * @property viewPortHandler Holds the content rectangle and the current zoom and translation of the chart, in pixels.
 */
public abstract class Renderer(protected val viewPortHandler: ViewPortHandler)
