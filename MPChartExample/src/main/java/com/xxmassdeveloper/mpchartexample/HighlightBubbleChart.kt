package com.xxmassdeveloper.mpchartexample

import android.os.Bundle

class HighlightBubbleChart : BubbleChartActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chart.xAxis!!.isHighlightEnabled = true
        chart.leftAxis!!.isHighlightEnabled = true
    }
}