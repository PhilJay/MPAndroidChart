package com.github.mikephil.charting;

import java.util.ArrayList;

public class Series {

	private float mVal = 0f;
	private int mType = 0;
	private int mXIndex = 0;

	/**
	 * A series represents one single entry in the chart
	 * 
	 * @param val
	 *            the y value
	 * @param type
	 *            the type (e.g. different lines in the line chart are of different types). The type can be seen as an
	 *            id and can be chosen freely.
	 * @param xIndex
	 *            the corresponding index in the x value array
	 */
	public Series(float val, int type, int xIndex) {
		mVal = val;
		mType = type;
		mXIndex = xIndex;
	}

	public void setType(int type) {
		this.mType = type;
	}

	public int getType() {
		return mType;
	}

	public int getXIndex() {
		return mXIndex;
	}
//
//	/**
//	 * Convenience method to create a series of double values
//	 * 
//	 * @param yValues
//	 * @return
//	 */
//	public static ArrayList<Series> makeSeries(double[] yValues) {
//		ArrayList<Series> series = new ArrayList<Series>();
//		for (int i = 0; i < yValues.length; i++) {
//			series.add(new Series((float) yValues[i], 0, i));
//		}
//		return series;
//	}
//
//	/**
//	 * Convenience method to create multiple series of different types of various double value arrays. Each double array
//	 * represents one type starting at 0.
//	 * 
//	 * @param yValues
//	 * @return
//	 */
//	public static ArrayList<Series> makeMultipleSeries(ArrayList<Double[]> yValues) {
//		ArrayList<Series> series = new ArrayList<Series>();
//
//		int sizeOfFirst = yValues.get(0).length;
//
//		for (int i = 0; i < yValues.size(); i++) {
//			Double[] curValues = yValues.get(i);
//			if (curValues.length != sizeOfFirst) {
//				throw new IllegalArgumentException("Array sizes do not match");
//			}
//			for (int j = 0; j < curValues.length; j++) {
//				series.add(new Series(curValues[j].floatValue(), i, j));
//			}
//		}
//
//		return series;
//	}
//
//	/**
//	 * Convenience method to add a series. The new series has to be the same size as the old. If you want to create a
//	 * different sized series, please add manually.
//	 * 
//	 * @param series
//	 * @param yValues
//	 * @param type
//	 */
//	public static void addSeries(ArrayList<Series> series, double[] yValues, int type) {
//		for (int i = 0; i < yValues.length; i++) {
//			series.add(new Series((float) yValues[i], type, i));
//		}
//	}

	public float getVal() {
		return mVal;
	}
}
