package com.github.mikephil.charting.interfaces.datasets;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;

/**
 * Created by Philipp Jahoda on 03/11/15.
 */
public interface IPieDataSet extends IDataSet<Entry> {

    /**
     * Returns the space that is set to be between the piechart-slices of this
     * DataSet, in pixels.
     *
     * @return
     */
    float getSliceSpace();

    /**
     * Returns the distance a highlighted piechart slice is "shifted" away from
     * the chart-center in dp.
     *
     * @return
     */
    float getSelectionShift();

    PieDataSet.ValuePosition getXValuePosition();
    PieDataSet.ValuePosition getYValuePosition();

    /**
     * When valuePosition is OutsideSlice, indicates line color
     * */
    int getValueLineColor();

    /**
     *  When valuePosition is OutsideSlice, indicates line width
     *  */
    float getValueLineWidth();

    /**
     * When valuePosition is OutsideSlice, indicates offset as percentage out of the slice size
     * */
    float getValueLinePart1OffsetPercentage();

    /**
     * When valuePosition is OutsideSlice, indicates length of first half of the line
     * */
    float getValueLinePart1Length();

    /**
     * When valuePosition is OutsideSlice, indicates length of second half of the line
     * */
    float getValueLinePart2Length();

    /**
     * When valuePosition is OutsideSlice, this allows variable line length
     * */
    boolean isValueLineVariableLength();

}

