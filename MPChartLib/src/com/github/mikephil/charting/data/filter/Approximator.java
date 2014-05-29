package com.github.mikephil.charting.data.filter;

import java.util.ArrayList;

import com.github.mikephil.charting.data.Entry;

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

	public ArrayList<Entry> filter(ArrayList<Entry> points, double tolerance) {
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

	private ArrayList<Entry> reduceWithDouglasPeuker(ArrayList<Entry> entries, double epsilon) {
		// if a shape has 2 or less points it cannot be reduced
		if (epsilon <= 0 || entries.size() < 3) {
			return null;
		}

		// first and last always stay
		keep[0] = true;
		keep[entries.size() - 1] = true;

		// first and last entry are entry point to recursion
		algorithmDouglasPeucker(entries, epsilon, 0, entries.size() - 1);

		// create a new array with series, only take the kept ones
		ArrayList<Entry> reducedEntries = new ArrayList<Entry>();
		for (int i = 0; i < entries.size(); i++) {
			if (keep[i]) {
				Entry curEntry = entries.get(i);
				reducedEntries.add(new Entry(curEntry.getVal(), curEntry.getXIndex()));
			}
		}
		return reducedEntries;
	}

	/**
	 * apply the Douglas-Peucker-Reduction to an ArrayList of Entry with a given epsilon (tolerance)
	 * 
	 * @param entries
	 * @param epsilon
	 *            as y-value
	 * @param start
	 * @param end
	 */
	private void algorithmDouglasPeucker(ArrayList<Entry> entries, double epsilon, int start, int end) {
		if (end <= start + 1) {
			// recursion finished
			return;
		}

		// find the greatest distance between start and endpoint
		int maxDistIndex = 0;
		double distMax = 0;

		Entry firstEntry = entries.get(start);
		Entry lastEntry = entries.get(end);

		for (int i = start + 1; i < end; i++) {
			double dist = pointToLineDistance(firstEntry, lastEntry, entries.get(i));

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
			algorithmDouglasPeucker(entries, epsilon, start, maxDistIndex);
			algorithmDouglasPeucker(entries, epsilon, maxDistIndex, end);
		}
	}

	/**
	 * calculate the distance between a line between two entries and an entry (point)
	 * 
	 * @param startEntry
	 * @param endEntry
	 * @param entryPoint
	 * @return
	 */
	public double pointToLineDistance(Entry startEntry, Entry endEntry, Entry entryPoint) {
		double normalLength = Math.sqrt((endEntry.getXIndex() - startEntry.getXIndex())
				* (endEntry.getXIndex() - startEntry.getXIndex()) + (endEntry.getVal() - startEntry.getVal())
				* (endEntry.getVal() - startEntry.getVal()));
		return Math.abs((entryPoint.getXIndex() - startEntry.getXIndex())
				* (endEntry.getVal() - startEntry.getVal()) - (entryPoint.getVal() - startEntry.getVal())
				* (endEntry.getXIndex() - startEntry.getXIndex()))
				/ normalLength;
	}
}
