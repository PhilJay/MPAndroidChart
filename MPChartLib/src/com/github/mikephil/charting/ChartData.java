package com.github.mikephil.charting;

import java.util.ArrayList;

/**
 * Class that holds all relevant data that represents the chart
 * 
 * @author Philipp
 * 
 */
public class ChartData {

	/** maximum y-value in the y-value array */
	private float mYMax = 0.0f;

	/** the minimum y-value in the y-value array */
	private float mYMin = 0.0f;

	/** the total sum of all y-values */
	private float mYValueSum = 0f;

	private ArrayList<String> mXVals;
	private ArrayList<Series> mYVals;

	/** array that holds all the different type ids that are in the series array */
	private ArrayList<Integer> mDiffTypes;

	/** field that holds all the series split into their different types */
	private ArrayList<ArrayList<Series>> typeSeries;

	/**
	 * constructor for chart data
	 * 
	 * @param xVals
	 *            must be at least as long as the longest series array of one type
	 * @param yVals
	 *            all y series values
	 */
	public ChartData(ArrayList<String> xVals, ArrayList<Series> yVals) {
		this.mXVals = xVals;
		this.mYVals = yVals;

		calcTypes();
		calcMinMax();
		calcYValueSum();
		calcTypeSeries();

		for (int i = 0; i < typeSeries.size(); i++) {
			if (typeSeries.get(i).size() > xVals.size()) {
				throw new IllegalArgumentException("x values are smaller than the largest y series array of one type");
			}
		}
	}

	/**
	 * calculates all different types that occur in the series and stores them for fast access
	 */
	private void calcTypes() {
		mDiffTypes = new ArrayList<Integer>();

		for (int i = 0; i < mYVals.size(); i++) {

			int type = mYVals.get(i).getType();

			if (!alreadyCounted(mDiffTypes, type)) {
				mDiffTypes.add(type);
			}
		}
	}

	/**
	 * calc minimum and maximum y value
	 */
	private void calcMinMax() {

		mYMin = mYVals.get(0).getVal();
		mYMax = mYVals.get(0).getVal();

		for (int i = 0; i < mYVals.size(); i++) {
			if (mYVals.get(i).getVal() < mYMin)
				mYMin = mYVals.get(i).getVal();

			if (mYVals.get(i).getVal() > mYMax)
				mYMax = mYVals.get(i).getVal();
		}
	}

	/**
	 * calculates the sum of all y-values
	 */
	private void calcYValueSum() {

		mYValueSum = 0;

		for (int i = 0; i < getYValSize(); i++) {
			mYValueSum += Math.abs(getYVals().get(i).getVal());
		}
	}

	/**
	 * extract all different types of series and store them in seperate arrays
	 */
	private void calcTypeSeries() {
		typeSeries = new ArrayList<ArrayList<Series>>();

		for (int i = 0; i < getTypeCount(); i++) {
			ArrayList<Series> series = new ArrayList<Series>();

			for (int j = 0; j < mYVals.size(); j++) {
				Series s = mYVals.get(j);
				if (s.getType() == mDiffTypes.get(i)) {
					series.add(s);
				}
			}

			typeSeries.add(series);
		}
	}

	private boolean alreadyCounted(ArrayList<Integer> countedTypes, int type) {
		for (int i = 0; i < countedTypes.size(); i++) {
			if (countedTypes.get(i) == type)
				return true;
		}

		return false;
	}

	public int getTypeCount() {
		return mDiffTypes.size();
	}

	public float getYMin() {
		return mYMin;
	}

	public float getYMax() {
		return mYMax;
	}

	public float getYValueSum() {
		return mYValueSum;
	}

	/**
	 * Checks if the ChartData object contains valid data
	 * 
	 * @return
	 */
	public boolean isValid() {

		if (mXVals == null || mXVals.size() <= 1 || mYVals == null || mYVals.size() <= 1)
			return false;
		else
			return true;
	}

	public ArrayList<String> getXVals() {
		return mXVals;
	}

	public ArrayList<Series> getYVals() {
		return mYVals;
	}

	public ArrayList<ArrayList<Series>> getTypeSeries() {
		return typeSeries;
	}

	public int getXValSize() {
		return mXVals.size();
	}

	public int getYValSize() {
		return mYVals.size();
	}
}
