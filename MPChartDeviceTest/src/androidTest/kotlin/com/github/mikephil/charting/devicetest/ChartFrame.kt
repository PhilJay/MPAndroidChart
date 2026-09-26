package com.github.mikephil.charting.devicetest

import android.content.Context
import android.widget.FrameLayout

/** Holds the charts under test and what they threw while drawing, so one bad case does not end the run. */
class ChartFrame(context: Context) : FrameLayout(context) {

    val errors = mutableListOf<Throwable>()
}
