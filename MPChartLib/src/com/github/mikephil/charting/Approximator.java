package com.github.mikephil.charting;

import java.util.ArrayList;

/**
 * Implemented according to Wiki-Pseudocode {@link} http://en.wikipedia.org/wiki/Ramer�Douglas�Peucker_algorithm
 * 
 * @author Philipp Baldauf & Phliipp Jahoda
 */
public class Approximator {

	private ApproximatorType type = ApproximatorType.NONE;

	public enum ApproximatorType {
		NONE, DOUGLAS_PEUCKER
	}

	private boolean[] keep;

	public Approximator(ApproximatorType type) {
		this.type = type;
	}

	public ArrayList<Series> filter(ArrayList<Series> points, double tolerance) {
		keep = new boolean[points.size()];

		switch (type) {
		case DOUGLAS_PEUCKER:
			return reduceWithDouglasPeuker(points, tolerance);
		case NONE:
			return null;
		default:
			return null;
		}

	}

	private ArrayList<Series> reduceWithDouglasPeuker(ArrayList<Series> series, double epsilon) {
		// if a shape has 2 or less points it cannot be reduced
		if (epsilon <= 0 || series.size() < 3) {
			return null;
		}

		// first and last always stay
		keep[0] = true;
		keep[series.size() - 1] = true;

		// first and last series are entry point to recursion
		algorithmDouglasPeucker(series, epsilon, 0, series.size() - 1);

		// create a new array with series, only take the kept ones
		ArrayList<Series> reducedSeries = new ArrayList<Series>();
		for (int i = 0; i < series.size(); i++) {
			if (keep[i]) {
				Series curSeries = series.get(i);
				reducedSeries.add(new Series(curSeries.getVal(), curSeries.getXIndex()));
			}
		}
		return reducedSeries;
	}

	/**
	 * apply the Douglas-Peucker-Reduction to an ArrayList of Series with a given epsilon (tolerance)
	 * 
	 * @param series
	 * @param epsilon
	 *            as y-value
	 * @param start
	 * @param end
	 */
	private void algorithmDouglasPeucker(ArrayList<Series> series, double epsilon, int start, int end) {
		if (end <= start + 1) {
			// recursion finished
			return;
		}

		// find the greatest distance between start and endpoint
		int maxDistIndex = 0;
		double distMax = 0;

		Series firstSeries = series.get(start);
		Series lastSeries = series.get(end);

		for (int i = start + 1; i < end; i++) {
			double dist = pointToLineDistance(firstSeries, lastSeries, series.get(i));

			// keep the point with the greatest distance
			if (dist > distMax) {
				distMax = dist;
				maxDistIndex = i;
			}
		}

		if (distMax > epsilon) {
			// keep max dist point
			keep[maxDistIndex] = true;

			// recursive call
			algorithmDouglasPeucker(series, epsilon, start, maxDistIndex);
			algorithmDouglasPeucker(series, epsilon, maxDistIndex, end);
		}
	}

	/**
	 * calculate the distance between a line between two series and a series (point)
	 * 
	 * @param startSeries
	 * @param endSeries
	 * @param seriesToInspect
	 * @return
	 */
	public double pointToLineDistance(Series startSeries, Series endSeries, Series seriesToInspect) {
		double normalLength = Math.sqrt((endSeries.getXIndex() - startSeries.getXIndex())
				* (endSeries.getXIndex() - startSeries.getXIndex()) + (endSeries.getVal() - startSeries.getVal())
				* (endSeries.getVal() - startSeries.getVal()));
		return Math.abs((seriesToInspect.getXIndex() - startSeries.getXIndex())
				* (endSeries.getVal() - startSeries.getVal()) - (seriesToInspect.getVal() - startSeries.getVal())
				* (endSeries.getXIndex() - startSeries.getXIndex()))
				/ normalLength;
	}
}
