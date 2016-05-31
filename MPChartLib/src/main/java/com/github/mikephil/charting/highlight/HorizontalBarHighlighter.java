package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.utils.SelectionDetail;

/**
 * Created by Philipp Jahoda on 22/07/15.
 */
public class HorizontalBarHighlighter extends BarHighlighter {

	public HorizontalBarHighlighter(BarDataProvider chart) {
		super(chart);
	}

	@Override
	public Highlight getHighlight(float x, float y) {

		BarData barData = mChart.getBarData();

		final float xVal = getXForTouch(x);
		final float baseNoSpace = getBase(x);
		final int setCount = barData.getDataSetCount();
		int dataSetIndex = ((int)baseNoSpace) % setCount;

		if (dataSetIndex < 0) {
			dataSetIndex = 0;
		} else if (dataSetIndex >= setCount) {
			dataSetIndex = setCount - 1;
		}

		SelectionDetail selectionDetail = getSelectionDetail(xVal, x, y, dataSetIndex);
		if (selectionDetail == null)
			return null;

		IBarDataSet set = barData.getDataSetByIndex(dataSetIndex);
		if (set.isStacked()) {

			float[] pts = new float[2];
			pts[0] = y;

			// take any transformer to determine the xPx-axis yValue
			mChart.getTransformer(set.getAxisDependency()).pixelsToValue(pts);

			return getStackedHighlight(selectionDetail,
					set,
					xVal,
					pts[0]);
		}

		return new Highlight(
				xVal,
				selectionDetail.yValue,
				selectionDetail.dataIndex,
				selectionDetail.dataSetIndex,
				-1);
	}

//	@Override
//	protected float getXForTouch(float x) {
//
//		if (!mChart.getBarData().isGrouped()) {
//
//			// create an array of the touch-point
//			float[] pts = new float[2];
//			pts[1] = x;
//
//			// take any transformer to determine the xPx-axis yValue
//			mChart.getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);
//
//			return (int) Math.round(pts[1]);
//		} else {
//
//			float baseNoSpace = getBase(x);
//
//			int setCount = mChart.getBarData().getDataSetCount();
//			int xIndex = (int) baseNoSpace / setCount;
//
//			int valCount = mChart.getData().getXValCount();
//
//			if (xIndex < 0)
//				xIndex = 0;
//			else if (xIndex >= valCount)
//				xIndex = valCount - 1;
//
//			return xIndex;
//		}
//	}

	/**
	 * Returns the base yPx-yValue to the corresponding xPx-touch yValue in pixels.
	 * 
	 * @param y
	 * @return
	 */
	@Override
	protected float getBase(float y) {

		// create an array of the touch-point
		float[] pts = new float[2];
		pts[1] = y;

		// take any transformer to determine the xPx-axis yValue
		mChart.getTransformer(YAxis.AxisDependency.LEFT).pixelsToValue(pts);
		float yVal = pts[1];

		int setCount = mChart.getBarData().getDataSetCount();

		// calculate how often the group-space appears
		int steps = (int) ((float) yVal / ((float) setCount + mChart.getBarData().getGroupSpace()));

		float groupSpaceSum = mChart.getBarData().getGroupSpace() * (float) steps;

		float baseNoSpace = (float) yVal - groupSpaceSum;
		return baseNoSpace;
	}
}
