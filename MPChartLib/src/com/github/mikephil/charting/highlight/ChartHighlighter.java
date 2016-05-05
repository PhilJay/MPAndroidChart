package com.github.mikephil.charting.highlight;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.utils.SelectionDetail;
import com.github.mikephil.charting.utils.Utils;

/**
 * Created by Philipp Jahoda on 21/07/15.
 */
public class ChartHighlighter<T extends BarLineScatterCandleBubbleDataProvider> {

	/** instance of the data-provider */
	protected T mChart;

	public ChartHighlighter(T chart) {
		this.mChart = chart;
	}

	/**
	 * Returns a Highlight object corresponding to the given x- and y- touch positions in pixels.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Highlight getHighlight(float x, float y) {

		int xIndex = getXIndex(x);

		SelectionDetail selectionDetail = getSelectionDetail(xIndex, y, -1);
		if (selectionDetail == null)
			return null;

		return new Highlight(xIndex,
				selectionDetail.value,
				selectionDetail.dataIndex,
				selectionDetail.dataSetIndex);
	}

	/**
	 * Returns the corresponding x-index for a given touch-position in pixels.
	 * 
	 * @param x
	 * @return
	 */
	protected int getXIndex(float x) {

		// create an array of the touch-point
		float[] pts = new float[2];
		pts[0] = x;

		// take any transformer to determine the x-axis value
		mChart.getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);

		return (int) Math.round(pts[0]);
	}

	/**
	 * Returns the corresponding SelectionDetail for a given xIndex and y-touch position in pixels.
	 * 
	 * @param xIndex
	 * @param y
	 * @param dataSetIndex
	 * @return
	 */
	protected SelectionDetail getSelectionDetail(int xIndex, float y, int dataSetIndex) {

		List<SelectionDetail> valsAtIndex = getSelectionDetailsAtIndex(xIndex, dataSetIndex);

		float leftdist = Utils.getMinimumDistance(valsAtIndex, y, YAxis.AxisDependency.LEFT);
		float rightdist = Utils.getMinimumDistance(valsAtIndex, y, YAxis.AxisDependency.RIGHT);

		YAxis.AxisDependency axis = leftdist < rightdist ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT;

		SelectionDetail detail = Utils.getClosestSelectionDetailByPixelY(valsAtIndex, y, axis);

		return detail;
	}

	/**
	 * Returns a list of SelectionDetail object corresponding to the given xIndex.
	 *
	 * @param xIndex
	 * @param dataSetIndex dataSet index to look at. -1 if unspecified.
	 * @return
	 */
	protected List<SelectionDetail> getSelectionDetailsAtIndex(int xIndex, int dataSetIndex) {

		List<SelectionDetail> vals = new ArrayList<SelectionDetail>();

		if (mChart.getData() == null) return vals;

		float[] pts = new float[2];

		for (int i = 0, dataSetCount = mChart.getData().getDataSetCount();
			 i < dataSetCount;
			 i++) {

			if (dataSetIndex > -1 && dataSetIndex != i)
				continue;

			IDataSet dataSet = mChart.getData().getDataSetByIndex(i);

			// dont include datasets that cannot be highlighted
			if (!dataSet.isHighlightEnabled())
				continue;

			// extract all y-values from all DataSets at the given x-index
			final float[] yVals = dataSet.getYValsForXIndex(xIndex);
			for (float yVal : yVals) {
				if (Float.isNaN(yVal))
					continue;

				pts[1] = yVal;

				mChart.getTransformer(dataSet.getAxisDependency()).pointValuesToPixel(pts);

				if (!Float.isNaN(pts[1]))
				{
					vals.add(new SelectionDetail(pts[1], yVal, i, dataSet));
				}
			}
		}

		return vals;
	}
}
