package com.github.mikephil.charting.components

import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.utils.Utils

/**
 * This class encapsulates everything both Axis, Legend and LimitLines have in common.
 */
abstract class ComponentBase {

    /**
     * flag that indicates if this axis / legend is enabled or not
     */
    var isEnabled: Boolean = true

    /**
     * the offset in pixels this component has on the x-axis
     */
    @JvmField
    protected var mXOffset: Float = 5f

    /**
     * the offset in pixels this component has on the Y-axis
     */
    @JvmField
    protected var mYOffset: Float = 5f

    /**
     * the typeface used for the labels
     */
    var typeface: Typeface? = null

    /**
     * the text size of the labels
     */
    @JvmField
    protected var mTextSize: Float = Utils.convertDpToPixel(10f)

    /**
     * the text color to use for the labels
     */
    open var textColor: Int = Color.BLACK

    /**
     * Returns the used offset on the x-axis for drawing the axis or legend
     * labels. This offset is applied before and after the label.
     */
    var xOffset: Float
        get() = mXOffset
        set(xOffset) {
            mXOffset = Utils.convertDpToPixel(xOffset)
        }

    /**
     * Returns the used offset on the x-axis for drawing the axis labels. This
     * offset is applied before and after the label.
     */
    var yOffset: Float
        get() = mYOffset
        set(yOffset) {
            mYOffset = Utils.convertDpToPixel(yOffset)
        }

    /**
     * returns the text size that is currently set for the labels, in pixels
     */
    var textSize: Float
        get() = mTextSize
        set(size) {
            var sizeLocal = size
            if (sizeLocal > 24f) sizeLocal = 24f
            if (sizeLocal < 6f) sizeLocal = 6f

            mTextSize = Utils.convertDpToPixel(sizeLocal)
        }
}
