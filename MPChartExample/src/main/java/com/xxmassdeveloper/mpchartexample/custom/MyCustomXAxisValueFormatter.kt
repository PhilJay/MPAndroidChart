package com.xxmassdeveloper.mpchartexample.custom

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

/**
 * Created by Philipp Jahoda on 14/09/15.
 *
 */
@Deprecated("The {@link MyAxisValueFormatter} does exactly the same thing and is more functional.")
class MyCustomXAxisValueFormatter(private val mViewPortHandler: ViewPortHandler) :
    IAxisValueFormatter {
    private val mFormat: DecimalFormat
    override fun getFormattedValue(value: Float, axis: AxisBase): String {

        //Log.i("TRANS", "x: " + viewPortHandler.getTransX() + ", y: " + viewPortHandler.getTransY());

        // e.g. adjust the x-axis values depending on scale / zoom level
        val xScale = mViewPortHandler.scaleX
        return if (xScale > 5) "4" else if (xScale > 3) "3" else if (xScale > 1) "2" else mFormat.format(
            value.toDouble()
        )
    }

    init {
        // maybe do something here or provide parameters in constructor
        mFormat = DecimalFormat("###,###,###,##0.0")
    }
}