package com.xxmassdeveloper.mpchartexample.custom.formatter;

/**
 * Interface that denotes that a ValueFormatter encodes and decodes its values.
 */
public interface ICodecFormatter {

    /**
     * Converts a data value to its axis value.
     *
     * @param dataValue data value
     * @return axis value
     */
    float encode(float dataValue);

    /**
     * Converts an axis value to its data value
     *
     * @param axisValue axis value
     * @return data value
     */
    float decode(float axisValue);

    /**
     * Converts an array data value to its axis value array.
     *
     * @param dataValues data value
     * @return axis value
     */
    float[] encode(float[] dataValues);

    /**
     * Converts an array of axis value to its data value array.
     *
     * @param axisValues axis value
     * @return data value
     */
    float[] decode(float[] axisValues);
}
