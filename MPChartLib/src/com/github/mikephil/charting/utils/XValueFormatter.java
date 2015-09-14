package com.github.mikephil.charting.utils;

/**
 * Created by Philipp Jahoda on 14/09/15.
 * An interface for providing custom x-axis Strings.
 *
 * @author Philipp Jahoda
 */
public interface XValueFormatter {

    String getXValue(String original, int index, ViewPortHandler viewPortHandler);
}
