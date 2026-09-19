package com.github.mikephil.charting.charts

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Keep
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider
import com.github.mikephil.charting.renderer.BubbleChartRenderer

/**
 * Chart that draws [BubbleData] as circles whose area, not radius, represents the entry size.
 * It implements [BubbleDataProvider] for the bubble renderer.
 */
@Keep
open class BubbleChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BarLineChartBase<BubbleData>(context, attrs, defStyle), BubbleDataProvider {

    override fun init() {
        super.init()

        renderer = BubbleChartRenderer(this, animator, viewPortHandler)
    }

    override val bubbleData: BubbleData?
        get() = data
}
