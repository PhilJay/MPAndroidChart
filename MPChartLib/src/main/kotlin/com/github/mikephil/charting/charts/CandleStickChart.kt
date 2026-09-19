package com.github.mikephil.charting.charts

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Keep
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider
import com.github.mikephil.charting.renderer.CandleStickChartRenderer

/**
 * Chart that draws [CandleData] as candlesticks with open, high, low and close values, as used for
 * prices. It implements [CandleDataProvider] for the candle renderer.
 */
@Keep
open class CandleStickChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BarLineChartBase<CandleData>(context, attrs, defStyle), CandleDataProvider {

    override fun init() {
        super.init()

        renderer = CandleStickChartRenderer(this, animator, viewPortHandler)

        xAxis.spaceMin = 0.5f
        xAxis.spaceMax = 0.5f
    }

    override val candleData: CandleData?
        get() = data
}
