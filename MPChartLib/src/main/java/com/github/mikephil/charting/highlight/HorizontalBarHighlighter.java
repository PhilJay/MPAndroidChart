package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.PointD;
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

		PointD pos = getValsForTouch(x, y);

		SelectionDetail selectionDetail = getSelectionDetail((float) pos.y, x, y);
		if (selectionDetail == null)
			return null;

		IBarDataSet set = barData.getDataSetByIndex(selectionDetail.dataSetIndex);
		if (set.isStacked()) {

			return getStackedHighlight(selectionDetail,
					set,
					(float) pos.y,
					(float) pos.x);
		}

		return new Highlight(
				selectionDetail.xValue,
				selectionDetail.yValue,
				selectionDetail.dataIndex,
				selectionDetail.dataSetIndex,
				-1);
	}
}
