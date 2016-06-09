package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 09/06/16.
 */
public abstract class BarLineScatterCandleBubbleRenderer extends DataRenderer {


    public BarLineScatterCandleBubbleRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    /**
     * Calculates and returns the x-bounds for the given DataSet in terms of index in their values array. This
     * includes minimum and maximum visible x, as well as range.
     *
     * @param dataSet
     * @return
     */
    protected XBounds getXBounds(BarLineScatterCandleBubbleDataProvider chart, IBarLineScatterCandleBubbleDataSet
            dataSet) {
        return new XBounds(chart, dataSet);
    }

    /**
     * Class representing the bounds of the current viewport in terms of indices in the values array of a DataSet.
     */
    protected class XBounds {

        /** minimum visible entry index */
        public final int min;

        /** maximum visible entry index */
        public final int max;

        /** range of visible entry indices */
        public final int range;

        public XBounds(BarLineScatterCandleBubbleDataProvider chart, IBarLineScatterCandleBubbleDataSet dataSet) {

            float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));

            float low = chart.getLowestVisibleX();
            float high = chart.getHighestVisibleX();

            Entry entryFrom = dataSet.getEntryForXPos(low, DataSet.Rounding.DOWN);
            Entry entryTo = dataSet.getEntryForXPos(high, DataSet.Rounding.UP);

            min = dataSet.getEntryIndex(entryFrom);
            max = dataSet.getEntryIndex(entryTo);
            range = (int) ((max - min) * phaseX);
        }
    }
}
