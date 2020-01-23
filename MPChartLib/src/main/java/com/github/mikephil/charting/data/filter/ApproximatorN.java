
package com.github.mikephil.charting.data.filter;

import java.util.ArrayList;

/**
 * Implemented according to modified Douglas Peucker {@link}
 * http://psimpl.sourceforge.net/douglas-peucker.html
 */
public class ApproximatorN
{
    public float[] reduceWithDouglasPeucker(float[] points, float resultCount) {

        int pointCount = points.length / 2;

        // if a shape has 2 or less points it cannot be reduced
        if (resultCount <= 2 || resultCount >= pointCount)
            return points;

        boolean[] keep = new boolean[pointCount];

        // first and last always stay
        keep[0] = true;
        keep[pointCount - 1] = true;

        int currentStoredPoints = 2;

        ArrayList<Line> queue = new ArrayList<>();
        Line line = new Line(0, pointCount - 1, points);
        queue.add(line);

        do {
            line = queue.remove(queue.size() - 1);

            // store the key
            keep[line.index] = true;

            // check point count tolerance
            currentStoredPoints += 1;

            if (currentStoredPoints == resultCount)
                break;

            // split the polyline at the key and recurse
            Line left = new Line(line.start, line.index, points);
            if (left.index > 0) {
                int insertionIndex = insertionIndex(left, queue);
                queue.add(insertionIndex, left);
            }

            Line right = new Line(line.index, line.end, points);
            if (right.index > 0) {
                int insertionIndex = insertionIndex(right, queue);
                queue.add(insertionIndex, right);
            }
        } while (queue.isEmpty());

        float[] reducedEntries = new float[currentStoredPoints * 2];

        for (int i = 0, i2 = 0, r2 = 0; i < currentStoredPoints; i++, r2 += 2) {
            if (keep[i]) {
                reducedEntries[i2++] = points[r2];
                reducedEntries[i2++] = points[r2 + 1];
            }
        }

        return reducedEntries;
    }

    private static float distanceToLine(
            float ptX, float ptY, float[]
            fromLinePoint1, float[] fromLinePoint2) {
        float dx = fromLinePoint2[0] - fromLinePoint1[0];
        float dy = fromLinePoint2[1] - fromLinePoint1[1];

        float dividend = Math.abs(
                dy * ptX -
                        dx * ptY -
                        fromLinePoint1[0] * fromLinePoint2[1] +
                        fromLinePoint2[0] * fromLinePoint1[1]);
        double divisor = Math.sqrt(dx * dx + dy * dy);

        return (float)(dividend / divisor);
    }

    private static class Line {
        int start;
        int end;

        float distance = 0;
        int index = 0;

        Line(int start, int end, float[] points) {
            this.start = start;
            this.end = end;

            float[] startPoint = new float[]{points[start * 2], points[start * 2 + 1]};
            float[] endPoint = new float[]{points[end * 2], points[end * 2 + 1]};

            if (end <= start + 1) return;

            for (int i = start + 1, i2 = i * 2; i < end; i++, i2 += 2) {
                float distance = distanceToLine(
                        points[i2], points[i2 + 1],
                        startPoint, endPoint);

                if (distance > this.distance) {
                    this.index = i;
                    this.distance = distance;
                }
            }
        }

        boolean equals(final Line rhs) {
            return (start == rhs.start) && (end == rhs.end) && index == rhs.index;
        }

        boolean lessThan(final Line rhs) {
            return distance < rhs.distance;
        }
    }

    private static int insertionIndex(Line line, ArrayList<Line> queue) {
        int min = 0;
        int max = queue.size();

        while (!queue.isEmpty()) {
            int midIndex = min + (max - min) / 2;
            Line midLine = queue.get(midIndex);

            if (midLine.equals(line)) {
                return midIndex;
            }
            else if (line.lessThan(midLine)) {
                // perform search in left half
                max = midIndex;
            }
            else {
                // perform search in right half
                min = midIndex + 1;
            }
        }

        return min;
    }
}
