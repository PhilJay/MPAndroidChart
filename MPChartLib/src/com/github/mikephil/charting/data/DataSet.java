package com.github.mikephil.charting.data;

import java.util.ArrayList;

/**
 * The DataSet class represents one group or type of entries (Entry) in the Chart that belong together. It is designed
 * to logically separate different groups of values inside the Chart (e.g. the values for a specific line in the
 * LineChart, or the values of a specific group of bars in the BarChart).
 * 
 * @author Philipp Jahoda
 */
public class DataSet {

	/** the entries that this dataset represents / holds together */
	private ArrayList<Entry> mYVals;

	/** maximum y-value in the y-value array */
	private float mYMax = 0.0f;

	/** the minimum y-value in the y-value array */
	private float mYMin = 0.0f;

	/** the total sum of all y-values */
	private float mYValueSum = 0f;

	/** type, used for identification amongst other DataSets */
	private int mType = 0;

	/**
	 * Creates a new DataSet object with the given values it represents and a type for identification amongst other
	 * DataSet objects (the type can be chosen freely and must not be equal to another type in the ChartData object).
	 * 
	 * @param yVals
	 * @param type
	 */
	public DataSet(ArrayList<Entry> yVals, int type) {
		this.mType = type;
		this.mYVals = yVals;

		if (yVals == null || yVals.size() <= 0)
			return;

		calcMinMax();
		calcYValueSum();
	}

	/**
	 * Use this method to tell the data set that the underlying data has changed
	 */
	public void notifyDataSetChanged() {
		calcMinMax();
		calcYValueSum();
	}

	public DataSet cloneDataSet() {
		ArrayList<Entry> duplicatedEntries = new ArrayList<Entry>();
		for (int i = 0; i < mYVals.size(); i++) {
			Entry entry = mYVals.get(i).cloneEntry();
			duplicatedEntries.add(entry);
		}
		DataSet dataSet = new DataSet(duplicatedEntries, mType);
		return dataSet;
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

		for (int i = 0; i < mYVals.size(); i++) {
			mYValueSum += Math.abs(mYVals.get(i).getVal());
		}
	}

	/**
	 * returns the number of y-values this DataSet represents
	 * 
	 * @return
	 */
	public int getEntryCount() {
		return mYVals.size();
	}

	/**
	 * Returns the value of the Entry object at the given xIndex. Returns Float.NaN if no value is at the given x-index.
	 * INFORMATION: This method does calculations at runtime. Do not over-use in performance critical situations.
	 * 
	 * @param xIndex
	 * @return
	 */
	public float getYValForXIndex(int xIndex) {

		Entry s = getEntryForXIndex(xIndex);

		if (s != null)
			return s.getVal();
		else
			return Float.NaN;
	}

	/**
	 * Returns the first Entry object found at the given xIndex. Returns null if no Entry object at that index. INFORMATION: This
	 * method does calculations at runtime. Do not over-use in performance critical situations.
	 * 
	 * @param xIndex
	 * @return
	 */
	public Entry getEntryForXIndex(int xIndex) {

		for (int i = 0; i < mYVals.size(); i++) {
			if (xIndex == mYVals.get(i).getXIndex())
				return mYVals.get(i);
		}

		return null;
	}
	
	/**
	 * Returns all Entry objects at the given xIndex. INFORMATION: This
     * method does calculations at runtime. Do not over-use in performance critical situations.
	 * @param xIndex
	 * @return
	 */
	public ArrayList<Entry> getEntriesForXIndex(int xIndex) {
	    
	    ArrayList<Entry> entries = new ArrayList<Entry>();
	    
	    for (int i = 0; i < mYVals.size(); i++) {
            if (xIndex == mYVals.get(i).getXIndex())
                entries.add(mYVals.get(i));
        }
	    
	    return entries;
	}

	/**
	 * returns the DataSets Entry array
	 * 
	 * @return
	 */
	public ArrayList<Entry> getYVals() {
		return mYVals;
	}

	/**
	 * gets the sum of all y-values
	 * 
	 * @return
	 */
	public float getYValueSum() {
		return mYValueSum;
	}

	/**
	 * returns the minimum y-value this DataSet holds
	 * 
	 * @return
	 */
	public float getYMin() {
		return mYMin;
	}

	/**
	 * returns the maximum y-value this DataSet holds
	 * 
	 * @return
	 */
	public float getYMax() {
		return mYMax;
	}

	/**
	 * returns the type of the DataSet, specified via constructor
	 * 
	 * @return
	 */
	public int getType() {
		return mType;
	}

	/**
	 * The xIndex of an Entry object is provided. This method returns the actual index in the Entry array of the
	 * DataSet. IMPORTANT: This method does calculations at runtime, do not over-use in performance critical situations.
	 * 
	 * @param xIndex
	 * @return
	 */
	public int getIndexInEntries(int xIndex) {

		for (int i = 0; i < mYVals.size(); i++) {
			if (xIndex == mYVals.get(i).getXIndex())
				return i;
		}

		return -1;
	}

	/**
	 * Convenience method to create multiple DataSets of different types with various double value arrays. Each double
	 * array represents the data of one DataSet with a type created by this method, starting at 0 (and incremented).
	 * 
	 * @param yValues
	 * @return
	 */
	public static ArrayList<DataSet> makeDataSets(ArrayList<Double[]> yValues) {

		ArrayList<DataSet> dataSets = new ArrayList<DataSet>();

		for (int i = 0; i < yValues.size(); i++) {

			Double[] curValues = yValues.get(i);

			ArrayList<Entry> entries = new ArrayList<Entry>();

			for (int j = 0; j < curValues.length; j++) {
				entries.add(new Entry(curValues[j].floatValue(), j));
			}

			dataSets.add(new DataSet(entries, i));
		}

		return dataSets;
	}
}
