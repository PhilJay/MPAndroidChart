package com.github.mikephil.charting.utils

/**
 * Transformer for the horizontal bar chart, where an inverted axis mirrors the values along the
 * x axis instead of the y axis.
 */
public class TransformerHorizontalBarChart(viewPortHandler: ViewPortHandler) : Transformer(viewPortHandler) {

    override fun prepareMatrixOffset(inverted: Boolean) {
        matrixOffset.reset()
        if (!inverted) {
            matrixOffset.postTranslate(viewPortHandler.offsetLeft, viewPortHandler.chartHeight - viewPortHandler.offsetBottom)
        } else {
            matrixOffset.setTranslate(
                -(viewPortHandler.chartWidth - viewPortHandler.offsetRight),
                viewPortHandler.chartHeight - viewPortHandler.offsetBottom
            )
            matrixOffset.postScale(-1.0f, 1.0f)
        }
    }
}
