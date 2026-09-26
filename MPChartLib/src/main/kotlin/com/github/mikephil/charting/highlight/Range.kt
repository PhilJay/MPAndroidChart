package com.github.mikephil.charting.highlight

/**
 * The value span one value of a stacked bar entry covers, from where the previous value ended to where
 * this one ends. Bar entries build one range per stack value; [BarHighlighter] uses them to find the
 * touched stack value.
 *
 * @property from value where the span starts, excluded from it.
 * @property to value where the span ends, included in it.
 */
public class Range(public var from: Float, public var to: Float) {

    /** True if [value] lies inside the range, excluding [from] and including [to]. */
    public fun contains(value: Float): Boolean = value > from && value <= to

    /** True if [value] lies beyond [to]. */
    public fun isLarger(value: Float): Boolean = value > to

    /** True if [value] lies before [from]. */
    public fun isSmaller(value: Float): Boolean = value < from
}
