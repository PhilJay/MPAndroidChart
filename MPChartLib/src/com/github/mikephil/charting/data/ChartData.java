package com.github.mikephil.charting.data;

import java.util.ArrayList;

import com.github.mikephil.charting.utils.Highlight;

/**
 * Class that holds all relevant data that represents the chart. That involves at least one (or more) DataSets, and an
 * array of x-values.
 * 
 * @author Philipp Jahoda
 */
public class ChartData {

	/** maximum y-value in the y-value array */
	private float mYMax = 0.0f;

	/** the minimum y-value in the y-value array */
	private float mYMin = 0.0f;

	/** the total sum of all y-values */
	private float mYValueSum = 0f;

	/** holds all x-values the chart represents */
	private ArrayList<String> mXVals;

	/** holds all the datasets (e.g. different lines) the chart represents */
	private ArrayList<DataSet> mDataSets;

	/** array that holds all the different type ids that are in the series array */
	private ArrayList<Integer> mDiffTypes;

	/**
	 * constructor for chart data
	 * 
	 * @param xVals
	 *            The values describing the x-axis. Must be at least as long as the highest xIndex in the Entry objects
	 *            across all DataSets.
	 * @param dataSets
	 *            all DataSet objects the chart needs to represent
	 */
	public ChartData(ArrayList<String> xVals, ArrayList<DataSet> dataSets) {
		init(xVals, dataSets);
	}

	/**
	 * constructor that takes string array instead of arraylist string
	 * 
	 * @param xVals
	 *            The values describing the x-axis. Must be at least as long as the highest xIndex in the Entry objects
	 *            across all DataSets.
	 * @param dataSets
	 *            all DataSet objects the chart needs to represent
	 */
	public ChartData(String[] xVals, ArrayList<DataSet> dataSets) {
		ArrayList<String> newXVals = new ArrayList<String>();
		for (int i = 0; i < xVals.length; i++) {
			newXVals.add(xVals[i]);
		}
		init(newXVals, dataSets);
	}

	/**
	 * Constructor that takes only one DataSet
	 * 
	 * @param xVals
	 * @param data
	 */
	public ChartData(ArrayList<String> xVals, DataSet data) {

		ArrayList<DataSet> sets = new ArrayList<DataSet>();
		sets.add(data);
		init(xVals, sets);
	}

	private void init(ArrayList<String> xVals, ArrayList<DataSet> dataSets) {
		this.mXVals = xVals;
		this.mDataSets = dataSets;

		calcTypes();
		calcMinMax();
		calcYValueSum();

		for (int i = 0; i < mDataSets.size(); i++) {
			if (mDataSets.get(i).getYVals().size() > xVals.size()) {
				throw new IllegalArgumentException("x values are smaller than the largest y series array of one type");
			}
		}
	}

	/**
	 * Call this method to let the CartData know that the underlying data has changed.
	 */
	public void notifyDataChanged() {
		doCalculations();
	}

	/**
	 * Does all necessary calculations, if the underlying data has changed
	 */
	private void doCalculations() {
		calcTypes();
		calcMinMax();
		calcYValueSum();
	}

	/**
	 * calculates all different types that occur in the datasets and stores them for fast access
	 */
	private void calcTypes() {
		mDiffTypes = new ArrayList<Integer>();

		// check which dataset to use
		ArrayList<DataSet> dataSets = mDataSets;

		for (int i = 0; i < dataSets.size(); i++) {

			int type = dataSets.get(i).getType();

			if (!alreadyCounted(mDiffTypes, type)) {
				mDiffTypes.add(type);
			}
		}
	}

	/**
	 * calc minimum and maximum y value over all datasets
	 */
	private void calcMinMax() {
		// check which dataset to use
		ArrayList<DataSet> dataSets = mDataSets;

		mYMin = dataSets.get(0).getYMin();
		mYMax = dataSets.get(0).getYMax();

		for (int i = 0; i < dataSets.size(); i++) {
			if (dataSets.get(i).getYMin() < mYMin)
				mYMin = dataSets.get(i).getYMin();

			if (dataSets.get(i).getYMax() > mYMax)
				mYMax = dataSets.get(i).getYMax();
		}
	}

	/**
	 * calculates the sum of all y-values in all datasets
	 */
	private void calcYValueSum() {

		mYValueSum = 0;

		// check which dataset to use
		ArrayList<DataSet> dataSets = mDataSets;
		for (int i = 0; i < dataSets.size(); i++) {
			mYValueSum += Math.abs(dataSets.get(i).getYValueSum());
		}
	}

	private boolean alreadyCounted(ArrayList<Integer> countedTypes, int type) {
		for (int i = 0; i < countedTypes.size(); i++) {
			if (countedTypes.get(i) == type)
				return true;
		}

		return false;
	}

	/**
	 * Corrects all values that are kept as member variables after a new entry was added. This saves recalculating all
	 * values.
	 * 
	 * @param entry
	 *            the new entry
	 */
	public void notifyDataForNewEntry(Entry entry) {
		mYValueSum += Math.abs(entry.getVal());
		if (mYMin > entry.getVal()) {
			mYMin = entry.getVal();
		}
		if (mYMax < entry.getVal()) {
			mYMax = entry.getVal();
		}
	}

	public int getDataSetCount() {
		return mDataSets.size();
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
		if (mXVals == null || mXVals.size() <= 1)
			return false;

		if (mDataSets == null || mDataSets.size() < 1)
			return false;

		return true;
	}

	/**
	 * returns the x-values the chart represents
	 * 
	 * @return
	 */
	public ArrayList<String> getXVals() {
		return mXVals;
	}

	/**
	 * returns the Entries array from the DataSet at the given index. If a filter is set, the filtered Entries are
	 * returned
	 * 
	 * @param index
	 * @return
	 */
	public ArrayList<Entry> getYVals(int index) {
		return mDataSets.get(index).getYVals();
	}

	/**
	 * Get the entry for a corresponding highlight object
	 * 
	 * @param highlight
	 * @return the entry that is highlighted
	 */
	public Entry getEntryForHighlight(Highlight highlight) {
		return getDataSetByIndex(highlight.getDataSetIndex()).getEntryForXIndex(highlight.getXIndex());
	}

	/**
	 * returns the dataset at the given index. If a filter is set, the filtered DataSet is returned.
	 * 
	 * @param index
	 * @return
	 */
	public DataSet getDataSetByIndex(int index) {
		return mDataSets.get(index);
	}

	/**
	 * retrieve a dataset with a specific type from the chartdata. If a filter is set, the filtered DataSet is returned.
	 * 
	 * @param type
	 * @return
	 */
	public DataSet getDataSetByType(int type) {
		// check which dataset to use
		ArrayList<DataSet> dataSets = mDataSets;

		for (int i = 0; i < dataSets.size(); i++)
			if (type == dataSets.get(i).getType())
				return dataSets.get(i);

		return null;
	}

	/**
	 * returns all DataSet objects the ChartData represents. If a filter is set, the filtered DataSets are returned
	 * 
	 * @return
	 */
	public ArrayList<DataSet> getDataSets() {
		return mDataSets;
	}

	/**
	 * This returns the original data set, regardless of any filter options.
	 * 
	 * @return
	 */
	public ArrayList<DataSet> getOriginalDataSets() {
		return mDataSets;
	}

	/**
	 * returns all the different DataSet types the chartdata represents
	 * 
	 * @return
	 */
	public ArrayList<Integer> getTypes() {
		return mDiffTypes;
	}

	/**
	 * returns the total number of x-values this chartdata represents (the size of the xvals array)
	 * 
	 * @return
	 */
	public int getXValCount() {
		return mXVals.size();
	}

	/**
	 * returns the total number of y-values across all DataSets the chartdata represents. If a filter is set, the
	 * filtered count is returned
	 * 
	 * @return
	 */
	public int getYValCount() {
		int count = 0;
		// check which dataset to use
		ArrayList<DataSet> dataSets = mDataSets;

		for (int i = 0; i < dataSets.size(); i++) {
			count += dataSets.get(i).getEntryCount();
		}

		return count;
	}
}
