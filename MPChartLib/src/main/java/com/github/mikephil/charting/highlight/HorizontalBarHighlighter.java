package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.SelectionDetail;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

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

		PointD pos = getValsForTouch(y, x);

		SelectionDetail selectionDetail = getSelectionDetail((float) pos.y, y, x);
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

	@Override
	protected SelectionDetail getDetails(IDataSet set, int dataSetIndex, float xVal, DataSet.Rounding rounding) {

		final Entry e = set.getEntryForXPos(xVal, rounding);

		PointD pixels = mChart.getTransformer(set.getAxisDependency()).getPixelsForValues(e.getY(), e.getX());

		return new SelectionDetail((float) pixels.x, (float) pixels.y, e.getX(), e.getY(), dataSetIndex, set);
	}

	@Override
	protected float getDistance(float x, float y, float selX, float selY) {
		return Math.abs(y - selY);
	}
}
