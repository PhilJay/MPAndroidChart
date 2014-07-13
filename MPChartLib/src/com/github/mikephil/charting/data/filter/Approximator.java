
package com.github.mikephil.charting.data.filter;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * Implemented according to Wiki-Pseudocode {@link}
 * http://en.wikipedia.org/wiki/Ramer�Douglas�Peucker_algorithm
 * 
 * @author Philipp Baldauf & Phliipp Jahoda
 */
public class Approximator {

    /** the type of filtering algorithm to use */
    private ApproximatorType mType = ApproximatorType.DOUGLAS_PEUCKER;

    /** the tolerance to be filtered with */
    private double mTolerance = 0;

    /**
     * array that contains "true" on all indices that will be kept after
     * filtering
     */
    private boolean[] keep;

    /** enums for the different types of filtering algorithms */
    public enum ApproximatorType {
        NONE, DOUGLAS_PEUCKER
    }

    /**
     * Initializes the approximator with type NONE
     */
    public Approximator() {
        this.mType = ApproximatorType.NONE;
    }

    /**
     * Initializes the approximator with the given type and tolerance. If
     * toleranec <= 0, no filtering will be done.
     * 
     * @param type
     */
    public Approximator(ApproximatorType type, double tolerance) {
        setup(type, tolerance);
    }

    /**
     * sets type and tolerance, if tolerance <= 0, no filtering will be done
     * 
     * @param type
     * @param tolerance
     */
    public void setup(ApproximatorType type, double tolerance) {
        mType = type;
        mTolerance = tolerance;
    }

    /**
     * sets the tolerance for the approximator, if tolerance <= 0, no filtering
     * will be done
     */
    public void setTolerance(double tolerance) {
        mTolerance = tolerance;
    }

    public void setType(ApproximatorType type) {
        this.mType = type;
    }

    /**
     * Filters according to type. Uses the pre set set tolerance
     * 
     * @param points the points to filter
     * @return
     */
    public ArrayList<Entry> filter(ArrayList<Entry> points) {
        return filter(points, mTolerance);
    }

    /**
     * Filters according to type.
     * 
     * @param points the points to filter
     * @param tolerance a separate tolerance can be specified
     * @return
     */
    public ArrayList<Entry> filter(ArrayList<Entry> points, double tolerance) {

        if (tolerance <= 0)
            return points;

        keep = new boolean[points.size()];

        switch (mType) {
            case DOUGLAS_PEUCKER:
                return reduceWithDouglasPeuker(points, tolerance);
            case NONE:
                return points;
            default:
                return points;
        }
    }

    /**
     * uses the douglas peuker algorithm to reduce the given arraylist of
     * entries
     * 
     * @param entries
     * @param epsilon
     * @return
     */
    private ArrayList<Entry> reduceWithDouglasPeuker(ArrayList<Entry> entries, double epsilon) {
        // if a shape has 2 or less points it cannot be reduced
        if (epsilon <= 0 || entries.size() < 3) {
            return entries;
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
     * apply the Douglas-Peucker-Reduction to an ArrayList of Entry with a given
     * epsilon (tolerance)
     * 
     * @param entries
     * @param epsilon as y-value
     * @param start
     * @param end
     */
    private void algorithmDouglasPeucker(ArrayList<Entry> entries, double epsilon, int start,
            int end) {
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
            double dist = calcPointToLineDistance(firstEntry, lastEntry, entries.get(i));

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
        } // else don't keep the point...
    }

    private ArrayList<Entry> reduceWithAngle(ArrayList<Entry> entries, double toleranceAngle) {

        for (int i = 0; i < entries.size() - 1; i++) {

            // if the angle is below the tolerance, it will not be removed
            if (calcAngle(entries.get(i), entries.get(i + 1)) < toleranceAngle) {
                keep[i + 1] = true;
            }
        }

        // first and last always stay
        keep[0] = true;
        keep[entries.size() - 1] = true;

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
     * calculate the distance between a line between two entries and an entry
     * (point)
     * 
     * @param startEntry line startpoint
     * @param endEntry line endpoint
     * @param entryPoint the point to which the distance is measured from the
     *            line
     * @return
     */
    public double calcPointToLineDistance(Entry startEntry, Entry endEntry, Entry entryPoint) {
                
        float xDiffEndStart = (float) endEntry.getXIndex() - (float) startEntry.getXIndex();
        float xDiffEntryStart = (float) entryPoint.getXIndex() - (float) startEntry.getXIndex();
        
        double normalLength = Math.sqrt((xDiffEndStart)
                * (xDiffEndStart)
                + (endEntry.getVal() - startEntry.getVal())
                * (endEntry.getVal() - startEntry.getVal())); 
        return Math.abs((xDiffEntryStart)
                * (endEntry.getVal() - startEntry.getVal())
                - (entryPoint.getVal() - startEntry.getVal())
                * (xDiffEndStart))
                / normalLength;
    }

    /**
     * Calculates the angle between two given lines. The provided Entry objects
     * mark the starting and end points of the lines.
     * 
     * @param start1
     * @param end1
     * @param start2
     * @param end2
     * @return
     */
    public double calcAngleBetweenLines(Entry start1, Entry end1, Entry start2, Entry end2) {

        double angle1 = calcAngle(start1, end1);
        double angle2 = calcAngle(start2, end2);
        return angle1 - angle2;
    }

    /**
     * calculates the angle between two Entries (points) in the chart
     * 
     * @param p1
     * @param p2
     * @return
     */
    public double calcAngle(Entry p1, Entry p2) {

        float dx = p2.getXIndex() - p1.getXIndex();
        float dy = p2.getVal() - p1.getVal();
        double angle = Math.atan2(dy, dx) * 180.0 / Math.PI;

        return angle;
    }
}
